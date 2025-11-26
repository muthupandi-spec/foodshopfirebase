package com.foodpartner.app.view.responsemodel

data class OtpResponseModel(
    val confirmPassword: String,
    val dateCreated: Any,
    val images: List<Any>,
    val isActive: Any,
    val isDelete: Boolean,
    val isVerified: Boolean,
    val mobileNumber: String,
    val otp: String,
    val password: String,
    val restaurantCity: String,
    val restaurantDescreption: String,
    val restaurantEMail: String,
    val restaurantId: Int,
    val restaurantLandMark: String,
    val restaurantLat: Double,
    val restaurantLng: Double,
    val restaurantName: String,
    val restaurantPhoneNumber: Any,
    val restaurantPinCode: String,
    val restaurantStreet: String,
    val restaurantType: String,
    val shopFcmToken: Any
)
