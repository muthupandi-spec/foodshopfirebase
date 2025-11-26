package com.foodpartner.app.ResponseMOdel

data class CancelledOrderResponsemodel(
    val address: Any,
    val cart: Any,
    val city: Any,
    val customer: Customer3,
    val customerId: Any,
    val customerLat: Double,
    val customerLng: Double,
    val `data`: DataX,
    val dateCreated: Any,
    val firstName: Any,
    val foodNames: Any,
    val isActive: Any,
    val isDelete: Any,
    val landMark: Any,
    val lastName: Any,
    val mobileNumber: Any,
    val orderId: Int,
    val orderItems: Any,
    val orderStatus: String,
    val paymentStatus: Any,
    val pincode: Any,
    val restaurantId: Any,
    val restaurantLat: Double,
    val restaurantLng: Double,
    val shopDeviceToken: Any,
    val status: Any,
    val street: Any,
    val totalAmount: Any
)

data class Customer3(
    val confirmpassword: Any,
    val customerId: Int,
    val dateCreated: Any,
    val email: Any,
    val fcmToken: String,
    val firstName: Any,
    val isDelete: Any,
    val isVerified: Any,
    val lastName: Any,
    val mobileNumber: Any,
    val otp: Any,
    val password: Any
)

