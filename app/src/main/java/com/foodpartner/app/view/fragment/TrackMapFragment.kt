package com.foodboy.app.view.fragment

import android.graphics.Color
import android.view.View
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
        this.mViewDataBinding.show.setOnClickListener {
            if (this.mViewDataBinding.container.visibility == View.VISIBLE) {
                this.mViewDataBinding.container.visibility = View.GONE
                this.mViewDataBinding.show.text = "Show" // Optional: toggle text
            } else {
                this.mViewDataBinding.container.visibility = View.VISIBLE
                this.mViewDataBinding.show.text = "Hide"
            }
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

        mViewDataBinding.orderid.text =
            "Order #${snap.getString("orderId") ?: ""}"

        mViewDataBinding.foodCount.text =
            "${snap.getLong("itemCount") ?: 1} Items"

        mViewDataBinding.foodCost.text =
            "₹${snap.getDouble("totalAmount") ?: 0.0}"

        mViewDataBinding.status.text =
            snap.getString("orderStatus") ?: "Pending"

        val shopImg = snap.getString("shop.restaurantImage")
        val shopName = snap.getString("shop.restaurantName")

        mViewDataBinding.foodName.text = shopName ?: ""

        if (!shopImg.isNullOrEmpty() && isAdded) {
            Glide.with(activitys)
                .load(shopImg)
                .into(mViewDataBinding.foodImgBg)
        }

        // -------------------------------
        // ✅ DELIVERED IMAGE LOGIC
        // -------------------------------
        val deliveredImage = snap.getString("deliveredImage")

        if (!deliveredImage.isNullOrEmpty()) {
            mViewDataBinding.deliveredCard.visibility = View.VISIBLE

            Glide.with(activitys)
                .load(deliveredImage)
                .placeholder(R.drawable.ic_image_loader)
                .error(R.drawable.ic_image_error)
                .into(mViewDataBinding.deliveredImg)
        } else {
            mViewDataBinding.deliveredCard.visibility = View.GONE
        }


        mViewDataBinding.deliveryamt.text =
            "₹${snap.getDouble("deliveryCharge") ?: 0.0}"

        mViewDataBinding.promovalue.text =
            "₹${snap.getDouble("promoDiscount") ?: 0.0}"

        mViewDataBinding.adminchargevalue.text =
            "₹${snap.getDouble("adminCharge") ?: 0.0}"

        mViewDataBinding.totalvalue.text =
            "₹${snap.getDouble("grandTotal") ?: 0.0}"
    }

}
