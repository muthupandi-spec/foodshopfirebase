package com.foodpartner.app.view.fragment
import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentSelectDriverBinding
import com.foodpartner.app.view.adapter.DriverAdapter
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class SelectDriverFragment(
    private val orderId: String,
    private val isPickup: Boolean
) : BaseFragment<FragmentSelectDriverBinding>() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: DriverAdapter
    private val drivers = ArrayList<Map<String, Any>>()

    override fun getLayoutId() = R.layout.fragment_select_driver

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        this.mViewDataBinding = mViewDataBinding as FragmentSelectDriverBinding

        adapter = DriverAdapter(drivers) { selectedDriver ->
            sendResult(selectedDriver)
        }
mViewDataBinding.btnBack.setOnClickListener {
    fragmentManagers!!.popBackStack()
}
        mViewDataBinding.rvDrivers.adapter = adapter
        fetchDrivers()
    }

private fun fetchDrivers() {

    db.collection("deliveryboys").get()
        .addOnSuccessListener { snapshot ->

            drivers.clear()

            for (doc in snapshot.documents) {

                val verifyValue = doc.get("verify")
                val isVerified = when (verifyValue) {
                    is Boolean -> verifyValue
                    is String -> verifyValue.equals("true", ignoreCase = true)
                    else -> false
                }
                if (!isVerified) continue

                if (doc.getString("status") != "Online") continue

                val lat = getDoubleSafe(doc, "latitude") ?: continue
                val lng = getDoubleSafe(doc, "longitude") ?: continue
               val shopLat= sharedHelper.getFromUser("restaurantLat")
               val shopLng= sharedHelper.getFromUser("restaurantLng")
                // 🔥 5 KM RADIUS FILTER
                val distance = distanceInKm(shopLat.toDouble(), shopLng.toDouble(), lat, lng)
                if (distance > 5) continue

                drivers.add(
                    mutableMapOf(
                        "uid" to doc.getString("uid").orEmpty(),
                        "name" to doc.getString("name").orEmpty(),
                        "mobileNumber" to doc.getString("mobileNumber").orEmpty(),
                        "status" to doc.getString("status").orEmpty(),
                        "fcm" to doc.getString("fcmToken").orEmpty(),
                        "isBusy" to (doc.getBoolean("isBusy") ?: false),
                        "profileImage" to doc.getString("profileImage").orEmpty(),
                        "latitude" to lat,
                        "longitude" to lng
                    )
                )
            }

            adapter.notifyDataSetChanged()
        }
        .addOnFailureListener {
            showToast("Failed to load delivery boys")
        }
}


    private fun sendResult(driver: Map<String, Any>) {

        val bundle = Bundle().apply {
            putSerializable("driver", HashMap(driver))
            putString("orderId", orderId)   // ✅ SEND ORDER ID
            putBoolean("isPickup", isPickup)
        }

        parentFragmentManager.setFragmentResult(
            "driver_selected",
            bundle
        )

        parentFragmentManager.popBackStack()
    }


    private fun getDoubleSafe(snapshot: DocumentSnapshot, field: String): Double? {
        val value = snapshot.get(field)
        return when (value) {
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull()
            else -> null
        }
    }
    private fun distanceInKm(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val earthRadius = 6371 // KM

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a =
            Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(lat1)) *
                    Math.cos(Math.toRadians(lat2)) *
                    Math.sin(dLon / 2) *
                    Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }

}