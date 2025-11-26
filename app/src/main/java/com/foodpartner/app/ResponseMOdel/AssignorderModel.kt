package com.foodpartner.app.ResponseMOdel

data class AssignorderModel(
    val address: Any,
    val cart: Any,
    val city: String,
    val customer: Customer1,
    val customerId: Any,
    val customerLat: Double,
    val customerLng: Double,
    val dateCreated: String,
    val firstName: String,
    val foodNames: Any,
    val isActive: Boolean,
    val isDelete: Boolean,
    val landMark: String,
    val lastName: String,
    val mobileNumber: String,
    val orderId: Int,
    val orderItems: Any,
    val orderStatus: String,
    val paymentStatus: String,
    val pincode: Int,
    val restaurantId: Int,
    val restaurantLat: Double,
    val restaurantLng: Double,
    val shopDeviceToken: Any,
    val status: String,
    val street: String,
    val totalAmount: Double
)

data class Customer1(
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



