package com.foodpartner.app.ResponseMOdel

class GetallCategoryResponseModel : ArrayList<GetallCategoryResponseModelItem>()

data class GetallCategoryResponseModelItem(

    var categoryId: String = "",
    var categoryName: String = "",
    var imageUrl: String = "",
    var isActive: Boolean =true ,
    var createdAt: Long = 0L
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
        val restaurantName: String,
        val restaurantPhoneNumber: Any,
        val restaurantPinCode: Any,
        val restaurantStreet: Any,
        val restaurantType: Any,
        val shopFcmToken: Any,
        val tradeId: Any
    )

}


