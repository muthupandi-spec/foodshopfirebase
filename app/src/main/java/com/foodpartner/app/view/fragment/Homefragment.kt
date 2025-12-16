package com.foodpartner.app.view.fragment

import android.location.Location
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.transition.Visibility
import com.foodboy.app.view.fragment.TrackMapFragment
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.HomefragmentBinding
import com.foodpartner.app.network.OrderStatus
import com.foodpartner.app.view.adapter.Activeadapter
import com.foodpartner.app.view.responsemodel.Shop
import com.kotlintest.app.utility.interFace.CommonInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Homefragment : BaseFragment<HomefragmentBinding>() {
    private val db = FirebaseFirestore.getInstance()
    private val orders = ArrayList<Map<String, Any>>()

    override fun getLayoutId(): Int = R.layout.homefragment

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        val restId = sharedHelper.getFromUser("userid") ?: return
        listenRestaurantOrders(restId)
        parentFragmentManager.setFragmentResultListener(
            "driver_selected",
            viewLifecycleOwner
        ) { _, bundle ->

            val driver = bundle.getSerializable("driver") as? HashMap<String, Any>
                ?: return@setFragmentResultListener

            val orderId = bundle.getString("orderId")
                ?: return@setFragmentResultListener

            assignDriverToOrder(orderId, driver)
        }


        this.mViewDataBinding.backBtn.setOnClickListener {
            loadFragment(Offerfragment(), android.R.id.content, "produ", true)

        }
    }

    private fun listenRestaurantOrders(restId: String) {
        db.collection("orders")
            .whereEqualTo("restaurantId", restId)
            .whereIn(
                "orderStatus", listOf(
                    OrderStatus.ORDER_PLACED,
                    OrderStatus.ACCEPTED_BY_RESTAURANT,
                    OrderStatus.PREPARING,
                    OrderStatus.READY_FOR_PICKUP,
                    OrderStatus.DELIVERY_ASSIGNED,
                    OrderStatus.DELIVERY_BOY_ARRIVED,
                )
            )
            .addSnapshotListener { snaps, err ->
                if (err != null) return@addSnapshotListener
                orders.clear()
                snaps?.documents?.forEach { orders.add(it.data ?: emptyMap()) }

                if(orders.isEmpty()){
                    mViewDataBinding.activeRV.visibility= View.GONE
                    mViewDataBinding.emptyimg.visibility= View.VISIBLE
                    mViewDataBinding.emptytxt.visibility= View.VISIBLE
                }else{
                    mViewDataBinding.activeRV.visibility= View.VISIBLE
                    mViewDataBinding.emptyimg.visibility= View.GONE
                    mViewDataBinding.emptytxt.visibility= View.GONE
                    mViewDataBinding?.activeRV?.adapter = Activeadapter(orders, object : CommonInterface {
                        override fun commonCallback(any: Any) {
                            if (any is Map<*, *>) {
                                val action = any["action"].toString()
                                val orderId = any["orderId"].toString()
                                when (action) {
                                    "accept" -> updateOrderStatus(orderId, OrderStatus.ACCEPTED_BY_RESTAURANT)
                                    "start_preparing" -> updateOrderStatus(orderId, OrderStatus.PREPARING)
                                    "ready" -> updateOrderStatus(orderId, OrderStatus.READY_FOR_PICKUP)
                                    "assign_delivery" -> assignDeliveryBoy(orderId)
                                    "track"->{
                                        loadFragment(TrackMapFragment(orderId),android.R.id.content, "",true)

                                    }
                                }
                            }
                        }
                    })

                }
               }
    }

    private fun updateOrderStatus(orderId: String, status: String) {
        db.collection("orders").document(orderId)
            .update(mapOf("orderStatus" to status, "updatedAt" to System.currentTimeMillis()))
    }
    private fun assignDeliveryBoy(orderId: String) {
        val fragment = SelectDriverFragment(orderId, isPickup = false)
        loadFragment(fragment, android.R.id.content, "select_driver", true)
    }


    /*
        private fun assignDeliveryBoy(orderId: String) {
            db.collection("orders").document(orderId).get()
                .addOnSuccessListener { orderSnap ->
                    val shopId = orderSnap.getString("restaurantId") ?: return@addOnSuccessListener

                    // Get shop details before assigning delivery boy
                    getShopDetails(shopId) { shop ->
                        if (shop == null) {
                            showToast("Shop details not found")
                            return@getShopDetails
                        }

                        val restLat = shop.restaurantLat.toDoubleOrNull() ?: 0.0
                        val restLng = shop.restaurantLng.toDoubleOrNull() ?: 0.0

                        db.collection("deliveryboys")
                            .whereEqualTo("status", "Online")
                            .whereEqualTo("verify", "true")
                            .whereEqualTo("isBusy", false)   // <-- NEW CONDITION
                            .get()
                            .addOnSuccessListener { boysSnap ->

                                if (boysSnap.isEmpty) {
                                    showToast("No delivery boy is available right now")
                                    return@addOnSuccessListener
                                }

                                var nearestBoyDoc: Map<String, Any>? = null
                                var shortestDistance = Double.MAX_VALUE

                                for (doc in boysSnap.documents) {
                                    val lat = doc.getDouble("latitude") ?: 0.0
                                    val lng = doc.getDouble("longitude") ?: 0.0
                                    val distance = getDistanceInKm(restLat, restLng, lat, lng)
                                    if (distance < shortestDistance && distance <= 100.0) {
                                        shortestDistance = distance
                                        nearestBoyDoc = mapOf(
                                            "uid" to (doc.getString("uid") ?: ""),
                                            "name" to (doc.getString("name") ?: ""),
                                            "mobileNumber" to (doc.getString("mobileNumber") ?: ""),
                                            "profileImage" to (doc.getString("profileImage") ?: ""),
                                            "landmark" to (doc.getString("landmark") ?: ""),
                                            "latitude" to lat,
                                            "longitude" to lng
                                        )
                                    }
                                }

                                if (nearestBoyDoc == null) {
                                    showToast("No delivery boy found within 10 km")
                                    return@addOnSuccessListener
                                }

                                val otp = (1000..9999).random().toString()

                                val orderData = mapOf(
                                    "orderStatus" to OrderStatus.DELIVERY_ASSIGNED,
                                    "deliveryBoy" to nearestBoyDoc,
                                    "shop" to shop,
                                    "otp" to otp,
                                    "updatedAt" to System.currentTimeMillis()
                                )

                                // Assign order to boy
                                db.collection("orders").document(orderId)
                                    .update(orderData)
                                    .addOnSuccessListener {
                                        showToast("Delivery boy assigned successfully")

                                        // Mark delivery boy as busy
                                        db.collection("deliveryboys")
                                            .document(nearestBoyDoc["uid"].toString())
                                            .update("isBusy", true)

                                        // Optionally, store the assignment for reference
                                  */
/*      db.collection("orders")
                                        .document(orderId)
                                        .collection("orderDeliveryBoys")
                                        .document(nearestBoyDoc["uid"].toString())
                                        .set(nearestBoyDoc)*//*

                                }
                                .addOnFailureListener {
                                    showToast("Failed to assign delivery boy")
                                }
                        }
                }
            }
    }
*/

    private fun getShopDetails(shopId: String, onResult: (Shop?) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("shops")
            .document(shopId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    val shop = Shop(
                        restaurantId = doc.getString("restaurantId") ?: "",
                        restaurantName = doc.getString("restaurantName") ?: "",
                        restaurantLat = doc.getString("restaurantLat") ?: "",
                        restaurantLng = doc.getString("restaurantLng") ?: "",
                        mobileNumber = doc.getString("mobileNumber") ?: "",
                        profileImage = doc.getString("profileImage") ?: "",
                        restaurantLandMark = doc.getString("restaurantLandMark") ?: "",

                    )
                    onResult(shop)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                onResult(null)
            }
    }

    private fun getDistanceInKm(
        lat1: Double, lon1: Double, lat2: Double, lon2: Double
    ): Double {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0] / 1000.0
    }
    private fun assignDriverToOrder(orderId: String, driver: Map<String, Any>) {
        val driverId = driver["uid"].toString()
        val otp = (1000..9999).random().toString()

        val driverData = mapOf(
            "orderStatus" to OrderStatus.DELIVERY_ASSIGNED,
            "deliveryBoy" to driver,
            "otp" to otp,
            "updatedAt" to System.currentTimeMillis()
        )

        db.collection("orders").document(orderId)
            .update(driverData)
            .addOnSuccessListener {
                showToast("Delivery boy assigned successfully")

                // Mark driver as busy
                db.collection("deliveryboys")
                    .document(driverId)
                    .update("isBusy", true)
            }
            .addOnFailureListener {
                showToast("Failed to assign delivery boy")
            }
    }


}
