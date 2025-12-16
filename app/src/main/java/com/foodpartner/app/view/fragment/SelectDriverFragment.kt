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

        mViewDataBinding.rvDrivers.adapter = adapter
        fetchDrivers()
    }

private fun fetchDrivers() {

    db.collection("deliveryboys").get()
        .addOnSuccessListener { snapshot ->

            drivers.clear()

            for (doc in snapshot.documents) {

                // ✅ verify (String / Boolean safe)
                val verifyValue = doc.get("verify")
                val isVerified = when (verifyValue) {
                    is Boolean -> verifyValue
                    is String -> verifyValue.equals("true", ignoreCase = true)
                    else -> false
                }
                if (!isVerified) continue

                // ✅ Only online drivers
                if (doc.getString("status") != "Online") continue

                // ✅ Optional: skip busy drivers
                // if (doc.getBoolean("isBusy") == true) continue

                val lat = getDoubleSafe(doc, "latitude")
                val lng = getDoubleSafe(doc, "longitude")
                drivers.add(
                    mutableMapOf<String, Any>(
                        "uid" to doc.getString("uid").orEmpty(),
                        "name" to doc.getString("name").orEmpty(),
                        "mobileNumber" to doc.getString("mobileNumber").orEmpty(),
                        "landmark" to doc.getString("landmark").orEmpty(),
                        "status" to doc.getString("status").orEmpty(),
                        "isBusy" to (doc.getBoolean("isBusy") ?: false),

                        // Images (KEYS MUST MATCH ADAPTER)
                        "profileImage" to doc.getString("profileImage").orEmpty(),
                        "aadharImage" to doc.getString("aadharImage").orEmpty(),
                        "licenseImage" to doc.getString("licenseImage").orEmpty(),
                        "passportImage" to doc.getString("passportImage").orEmpty(),

                        "latitude" to (lat ?: 0.0),
                        "longitude" to (lng ?: 0.0)
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

}