package com.foodpartner.app.view.responsemodel

data class CreateCategoryResponseModel(
    val isActive: Any,
    val isDelete: Any,
    val restaurantBo: RestaurantBo,
    val restaurantCatagory: String,
    val restaurantCatagoryId: Int,
    val restaurantId: Any
){
    data class RestaurantBo(
        val confirmPassword: Any,
        val dateCreated: Any,
        val dateModified: Any,
        val images: List<Any>,
        val isActive: Any,
        val isDelete: Any,
        val isVerified: Any,
        val mobileNumber: Any,
        val otp: Any,
        val password: Any,
        val restaurantCity: Any,
        val restaurantDescreption: Any,
        val restaurantEMail: Any,
        val restaurantId: Int,
        val restaurantLandMark: Any,
        val restaurantLat: Double,
        val restaurantLng: Double,
        val restaurantName: Any,
        val restaurantPhoneNumber: Any,
        val restaurantPinCode: Any,
        val restaurantStreet: Any,
        val restaurantType: Any,
        val shopFcmToken: Any,
        val tradeId: Any
    )

}


