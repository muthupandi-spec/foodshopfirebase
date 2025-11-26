package com.foodpartner.app.ResponseMOdel

data class AcceptOrderResponsemodel(
    val confirmPassword: String,
    val dateCreated: Any,
    val images: List<Any>,
    val isActive: Boolean,
    val isDelete: Boolean,
    val isVerified: Boolean,
    val mobileNumber: String,
    val otp: String,
    val password: String,
    val restaurantCity: String,
    val restaurantDescreption: Any,
    val restaurantEMail: String,
    val restaurantId: Int,
    val restaurantLandMark: Any,
    val restaurantLat: Double,
    val restaurantLng: Double,
    val restaurantName: String,
    val restaurantPhoneNumber: Long,
    val restaurantPinCode: Any,
    val restaurantStreet: String,
    val restaurantType: String,
    val shopFcmToken: String,
    val tradeId: Any
)

