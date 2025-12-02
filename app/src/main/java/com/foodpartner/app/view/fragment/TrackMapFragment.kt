package com.foodboy.app.view.fragment

import android.graphics.Color
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.foodboy.app.view.adapter.FoodItemAdapter
import com.foodpartner.app.R

import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class TrackMapFragment(
    private val orderId: String
) : BaseFragment<FragmentMapBinding>() {

    private val db = FirebaseFirestore.getInstance()

    override fun getLayoutId() = R.layout.fragment_map

    override fun initView(mViewDataBinding: ViewDataBinding?) {
   loadOrder()
        this.mViewDataBinding.backBtn.setOnClickListener {
            fragmentManagers!!.popBackStackImmediate()
        }
    }



    private fun loadOrder() {
        db.collection("orders").document(orderId)
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null || !snap.exists()) {
                    showToast("Order not found")
                    return@addSnapshotListener
                }
                setOrderUi(snap)
                val foodList =
                    snap.get("orderItems") as? ArrayList<HashMap<String, Any>> ?: arrayListOf()
                this.mViewDataBinding.foodlist.adapter = FoodItemAdapter(foodList)


            }
    }


    private fun setOrderUi(snap: DocumentSnapshot) {

        this.mViewDataBinding.orderid.text = "Order #${snap.getString("orderId") ?: ""}"
        this.mViewDataBinding.foodName.text = snap.getString("foodName") ?: "Food"
        this.mViewDataBinding.foodCount.text = "${snap.getLong("itemCount") ?: 1} Items"

        this.mViewDataBinding.foodCost.text = "₹${snap.getDouble("totalAmount") ?: 0.0}"
        this.mViewDataBinding.status.text = snap.getString("orderStatus") ?: "Pending"

        Glide.with(this)
            .load(sharedHelper.getFromUser("profileImage"))
            .into(mViewDataBinding.foodImgBg)

        this.mViewDataBinding.deliveryamt.text = "₹${snap.getDouble("deliveryCharge") ?: 0.0}"
        this.mViewDataBinding.promovalue.text = "₹${snap.getDouble("promoDiscount") ?: 0.0}"
        this.mViewDataBinding.adminchargevalue.text = "₹${snap.getDouble("adminCharge") ?: 0.0}"
        this.mViewDataBinding.totalvalue.text = "₹${snap.getDouble("grandTotal") ?: 0.0}"
    }

}
