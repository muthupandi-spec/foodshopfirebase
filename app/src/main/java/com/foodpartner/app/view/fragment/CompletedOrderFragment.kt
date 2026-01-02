package com.foodpartner.app.view.fragment

import android.app.DatePickerDialog
import android.view.View
import androidx.databinding.ViewDataBinding
import com.foodboy.app.view.fragment.TrackMapFragment
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentCompletedBinding
import com.foodpartner.app.network.OrderStatus
import com.foodpartner.app.view.adapter.Activeadapter
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
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

        binding.earningsChart.visibility = View.GONE

        binding.startDateLayout.setOnClickListener { showDatePicker(true) }
        binding.endDateLayout.setOnClickListener { showDatePicker(false) }

        // APPLY CUSTOM DATE
        binding.applyButton.setOnClickListener { applyFilter() }

        // 🔥 RESET TO LAST 6 MONTHS
        binding.applyButton.setOnLongClickListener {
            binding.startDateText.text = "Start Time"
            binding.endDateText.text = "End Time"
            filterTodayOrders()
            true
        }

        binding.btnWeek.setOnClickListener {
            binding.earningsChart.visibility = View.VISIBLE
            showWeeklyGraph()
        }

        binding.btnMonth.setOnClickListener {
            binding.earningsChart.visibility = View.VISIBLE
            showMonthlyGraph()
        }
    }

    // ================= FIRESTORE =================
    private fun listenOrders(uid: String) {
        db.collection("orders")
            .whereEqualTo("restaurantId", uid)
            .whereEqualTo("orderStatus", OrderStatus.DELIVERED)
            .addSnapshotListener { snap, _ ->
                allOrders.clear()
                snap?.documents?.forEach { it.data?.let { data -> allOrders.add(data) } }

                // 🔥 DEFAULT = LAST 6 MONTHS
                filterTodayOrders()
            }
    }

    // ================= DATE PICKER =================
    private fun showDatePicker(isStart: Boolean) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, y, m, d ->
                cal.set(
                    y, m, d,
                    if (isStart) 0 else 23,
                    if (isStart) 0 else 59,
                    if (isStart) 0 else 59
                )
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
        ).show()
    }

    // ================= CUSTOM FILTER =================
    private fun applyFilter() {
        filteredOrders = ArrayList(
            allOrders.filter { order ->
                val ts = order["createdAt"] as? Timestamp ?: return@filter false
                val date = ts.toDate()

                val afterStart = startDate?.let { date >= it } ?: true
                val beforeEnd = endDate?.let { date <= it } ?: true

                afterStart && beforeEnd
            }
        )
        updateUI()
    }

    // ================= LAST 6 MONTHS =================
/*
    private fun filterLastSixMonths() {
        val cal = Calendar.getInstance()
        val endDate = cal.time

        cal.add(Calendar.MONTH, -6)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        val startDate = cal.time

        filteredOrders = ArrayList(
            allOrders.filter { order ->
                val ts = order["createdAt"] as? Timestamp ?: return@filter false
                val date = ts.toDate()
                date.after(startDate) && date.before(endDate)
            }
        )



        updateUI()
    }
*/
    private fun filterTodayOrders() {
        val cal = Calendar.getInstance()

        // Start of today (00:00:00)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfDay = cal.time

        // End of today (23:59:59)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val endOfDay = cal.time

        filteredOrders = ArrayList(
            allOrders.filter { order ->
                val ts = order["createdAt"] as? Timestamp ?: return@filter false
                val date = ts.toDate()
                date.after(startOfDay) && date.before(endOfDay)
            }
        )

        updateUI()
    }


    // ================= UI =================
    private fun updateUI() {
        binding.tvTotalOrders.text = filteredOrders.size.toString()

        val total = filteredOrders.sumOf {
            (it["totalAmount"] as? Number)?.toDouble() ?: 0.0
        }
        binding.tvTotalAmount.text = "₹%.2f".format(total)

        if (filteredOrders.isEmpty()) {
            binding.ordersRV.visibility = View.GONE
            binding.emptyCons.visibility = View.VISIBLE
        } else {
            binding.ordersRV.visibility = View.VISIBLE
            binding.emptyCons.visibility = View.GONE
            binding.ordersRV.adapter =
                Activeadapter(filteredOrders, object : CommonInterface {
                    override fun commonCallback(any: Any) {
                        if (any is Map<*, *>) {
                            if (any["action"] == "track") {
                                loadFragment(
                                    TrackMapFragment(any["orderId"].toString()),
                                    android.R.id.content, "", true
                                )
                            }
                        }
                    }
                })
        }
    }

    // ================= WEEKLY GRAPH =================
    private fun showWeeklyGraph() {
        val map = LinkedHashMap<String, Float>()
        val cal = Calendar.getInstance()

        for (i in 6 downTo 0) {
            val key = SimpleDateFormat("dd MMM", Locale.getDefault()).format(cal.time)
            map[key] = 0f
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }

        allOrders.forEach {
            val ts = it["createdAt"] as? Timestamp ?: return@forEach
            val key = SimpleDateFormat("dd MMM", Locale.getDefault()).format(ts.toDate())
            map[key] =
                (map[key] ?: 0f) + ((it["totalAmount"] as? Number)?.toFloat() ?: 0f)
        }

        drawGraph(map, "Weekly Earnings")
    }

    // ================= MONTHLY GRAPH =================
    private fun showMonthlyGraph() {
        val map = LinkedHashMap<String, Float>()
        val cal = Calendar.getInstance()
        val days = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 1..days) map[i.toString()] = 0f

        allOrders.forEach {
            val ts = it["createdAt"] as? Timestamp ?: return@forEach
            val c = Calendar.getInstance().apply { time = ts.toDate() }

            if (c.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)) {
                val day = c.get(Calendar.DAY_OF_MONTH).toString()
                map[day] =
                    (map[day] ?: 0f) + ((it["totalAmount"] as? Number)?.toFloat() ?: 0f)
            }
        }

        drawGraph(map, "Monthly Earnings")
    }

    private fun drawGraph(data: Map<String, Float>, label: String) {

        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        data.entries.forEachIndexed { index, entry ->
            entries.add(BarEntry(index.toFloat(), entry.value))
            labels.add(entry.key)
        }

        val barDataSet = BarDataSet(entries, label).apply {
            setDrawValues(false)
        }

        val barData = BarData(barDataSet).apply {
            barWidth = 0.6f
        }

        binding.earningsChart.apply {
            this.data = barData
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)

            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false
            xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
            xAxis.setLabelCount(labels.size, false)

            description.isEnabled = false
            setFitBars(true)
            animateY(800)
            invalidate()
        }
    }
}
