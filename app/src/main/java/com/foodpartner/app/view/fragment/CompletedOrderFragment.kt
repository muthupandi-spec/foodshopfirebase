package com.foodpartner.app.view.fragment

import android.app.DatePickerDialog
import android.view.View
import androidx.databinding.ViewDataBinding
import com.foodpartner.app.R
import com.foodpartner.app.ResponseMOdel.CompleteddOrderResponModelItem
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentCompletedBinding
import com.foodpartner.app.network.OrderStatus
import com.foodpartner.app.view.adapter.Activeadapter
import com.foodpartner.app.view.adapter.CompletedAdapter
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.kotlintest.app.utility.interFace.CommonInterface
import java.text.SimpleDateFormat
import java.util.*

class CompletedOrderFragment : BaseFragment<FragmentCompletedBinding>() {

    private val db = FirebaseFirestore.getInstance()
    private val allOrders = ArrayList<Map<String, Any>>()
    private var filteredOrders = ArrayList<Map<String, Any>>()
    private var startDate: Date? = null
    private var endDate: Date? = null
    private lateinit var binding: FragmentCompletedBinding

    override fun getLayoutId() = R.layout.fragment_completed

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        binding = mViewDataBinding as FragmentCompletedBinding
        val uid = sharedHelper.getFromUser("userid") ?: return

        listenOrders(uid)

        // Initially hide the chart
        binding.earningsChart.visibility = View.GONE

        // Date Pickers
        binding.startDateLayout.setOnClickListener { showDatePicker(true) }
        binding.endDateLayout.setOnClickListener { showDatePicker(false) }

        // Apply filter
        binding.applyButton.setOnClickListener { applyFilter() }

        // Long press to reset filter
        binding.applyButton.setOnLongClickListener {
            startDate = null
            endDate = null
            binding.startDateText.text = "Start Date"
            binding.endDateText.text = "End Date"
            filteredOrders = ArrayList(allOrders)
            updateUI()
            binding.earningsChart.visibility = View.GONE
            true
        }

        // Graph buttons
        binding.btnWeek.setOnClickListener {
            binding.earningsChart.visibility = View.VISIBLE
            showWeeklyGraph()
        }

        binding.btnMonth.setOnClickListener {
            binding.earningsChart.visibility = View.VISIBLE
            showMonthlyGraph()
        }
    }

    private fun showDatePicker(isStart: Boolean) {
        val cal = Calendar.getInstance()
        val dp = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                cal.set(year, month, dayOfMonth, if (isStart) 0 else 23, if (isStart) 0 else 59, if (isStart) 0 else 59)
                if (isStart) {
                    startDate = cal.time
                    binding.startDateText.text =
                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(startDate!!)
                } else {
                    endDate = cal.time
                    binding.endDateText.text =
                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(endDate!!)
                }
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        dp.show()
    }

    private fun listenOrders(uid: String) {
        db.collection("orders")
            .whereEqualTo("restaurantId", uid)
            .whereEqualTo("orderStatus", OrderStatus.DELIVERED)
            .addSnapshotListener { snap, _ ->
                allOrders.clear()
                snap?.documents?.forEach { it.data?.let { data -> allOrders.add(data) } }
                filteredOrders = ArrayList(allOrders)
                updateUI()
            }
    }

    // Safe mapping from Firestore map to model

    private fun applyFilter() {
        filteredOrders = allOrders.filter { order ->
            val ts = order["createdAt"] as? Timestamp ?: return@filter false
            val date = ts.toDate()
            val afterStart = startDate?.let { date >= it } ?: true
            val beforeEnd = endDate?.let { date <= it } ?: true
            afterStart && beforeEnd
        } as ArrayList<Map<String, Any>>

        updateUI()
    }


    private fun updateUI() {
        val binding = mViewDataBinding

        // Total orders
        binding.tvTotalOrders.text = filteredOrders.size.toString()

        // Total amount
        val total = filteredOrders.sumOf { (it["totalAmount"] as? Number)?.toDouble() ?: 0.0 }
        binding.tvTotalAmount.text = "₹%.2f".format(total)

        // Show empty or list
        if (filteredOrders.isEmpty()) {
            binding.ordersRV.visibility = View.GONE
            binding.emptyCons.visibility = View.VISIBLE
        } else {
            binding.ordersRV.visibility = View.VISIBLE
            binding.emptyCons.visibility = View.GONE
            binding.ordersRV.adapter = Activeadapter(filteredOrders, object : CommonInterface {
                override fun commonCallback(any: Any) {}
            })
        }
    }

    private fun showWeeklyGraph() {
        val map = LinkedHashMap<String, Float>()
        val cal = Calendar.getInstance()

        for (i in 6 downTo 0) {
            val day = SimpleDateFormat("dd MMM", Locale.getDefault()).format(cal.time)
            map[day] = 0f
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }

        filteredOrders.forEach {
            val ts = it["createdAt"] as? Timestamp ?: return@forEach
            val key = SimpleDateFormat("dd MMM", Locale.getDefault()).format(ts.toDate())
            map[key] = (map[key] ?: 0f) + ((it["totalAmount"] as? Number)?.toFloat() ?: 0f)
        }

        drawGraph(map, "Weekly Earnings")
    }

    // ---------------- MONTHLY GRAPH ----------------
    private fun showMonthlyGraph() {
        val map = LinkedHashMap<String, Float>()
        val cal = Calendar.getInstance()
        val days = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 1..days) map[i.toString()] = 0f

        filteredOrders.forEach {
            val ts = it["createdAt"] as? Timestamp ?: return@forEach
            val c = Calendar.getInstance().apply { time = ts.toDate() }
            if (c.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)) {
                val day = c.get(Calendar.DAY_OF_MONTH).toString()
                map[day] = (map[day] ?: 0f) + ((it["totalAmount"] as? Number)?.toFloat() ?: 0f)
            }
        }

        drawGraph(map, "Monthly Earnings")
    }

    private fun drawGraph(data: Map<String, Float>, label: String) {
        val entries = ArrayList<Entry>()
        val labels = ArrayList<String>()

        data.entries.forEachIndexed { i, e ->
            entries.add(Entry(i.toFloat(), e.value))
            labels.add(e.key)
        }

        val set = LineDataSet(entries, label).apply {
            lineWidth = 2f
            setDrawValues(false)
            setDrawCircles(true)
        }

        // Correct assignment
        val lineData = LineData(set) // <-- LineData object
        binding.earningsChart.apply {
            this.data = lineData
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            axisRight.isEnabled = false
            description.isEnabled = false
            invalidate()
        }
    }



}
