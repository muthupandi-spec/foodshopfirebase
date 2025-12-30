package com.foodpartner.app.view.fragment

import android.location.Location
import android.util.Log
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
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

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
            .addOnSuccessListener {
                showToast("Order updated")

                // Send notifications
                db.collection("orders").document(orderId).get().addOnSuccessListener { doc ->
                    val customerFcm = doc.getString("cusfcmtoken") ?: ""
                    val shopFcm = (doc.get("shop") as? Map<*, *>)?.get("fcm") as? String ?: ""
                    val deliveryFcm = (doc.get("deliveryBoy") as? Map<*, *>)?.get("fcm") as? String ?: ""
                    sharedHelper.putInUser("custoken",customerFcm)
                    sharedHelper.putInUser("shopFcm",shopFcm)
                    sharedHelper.putInUser("deliveryFcm",deliveryFcm)


                    val message = when (status) {
                        OrderStatus.ACCEPTED_BY_RESTAURANT -> "Your order has been accepted by the restaurant."
                        OrderStatus.PREPARING -> "Your order is being prepared."
                        OrderStatus.READY_FOR_PICKUP -> "Your order is ready for pickup."
                        OrderStatus.DELIVERY_ASSIGNED -> "Delivery boy has been assigned."
                        OrderStatus.DELIVERY_BOY_ARRIVED -> "Delivery boy has arrived."
                        else -> "Order status updated."
                    }

                    if (customerFcm.isNotEmpty()) sendPushNotification(customerFcm, "Order Update", message)
                    if (shopFcm.isNotEmpty()) sendPushNotification(shopFcm, "Order Update", message)
                    if (deliveryFcm.isNotEmpty()) sendPushNotification(deliveryFcm, "Order Update", message)
                }
            }
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

                // Send notification
                val customerFcm =  sharedHelper.getFromUser("custoken")
                val shopFcm = sharedHelper.getFromUser("shopFcm")
                val driverFcm = sharedHelper.getFromUser("deliveryFcm")


                if (customerFcm.isNotEmpty()) sendPushNotification(customerFcm, "Order Update", "Delivery boy has been assigned.")
                if (shopFcm.isNotEmpty()) sendPushNotification(shopFcm, "Order Update", "Delivery boy has been assigned.")
                if (driverFcm.isNotEmpty()) sendPushNotification(driverFcm, "Order Update", "You have been assigned a new order.")

                // Mark driver as busy
                db.collection("deliveryboys")
                    .document(driverId)
                    .update("isBusy", true)
            }
            .addOnFailureListener {
                showToast("Failed to assign delivery boy")
            }
    }
    fun sendPushNotification(token: String, title: String, message: String) {

        val client = OkHttpClient()

        val json = """
        {
          "token": "$token",
          "title": "$title",
          "body": "$message"
        }
    """.trimIndent()

        val body = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://us-central1-bhashabhai-da0a6.cloudfunctions.net/sendTestNotification")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("FCM", "Push failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("FCM", "Push success: ${response.body?.string()}")
            }
        })
    }


}
