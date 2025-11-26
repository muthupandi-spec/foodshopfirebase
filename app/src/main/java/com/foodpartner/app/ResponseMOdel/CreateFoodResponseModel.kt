package com.foodpartner.app.ResponseMOdel

data class CreateFoodResponseModel(
    val catagoryId: Int,
    val catagorybo: Any,
    val decription: String,
    val decription1: String,
    val decription2: String,
    val foodId: Int,
    val foodName: String,
    val image: Any,
    val imageData: Any,
    val isActive: Any,
    val isDelete: Any,
    val price: Int,
    val restaurantCatagoryBO: RestaurantCatagoryBO,
    val type: String
){
    data class RestaurantCatagoryBO(
        val isActive: Any,
        val isDelete: Any,
        val restaurantBo: Any,
        val restaurantCatagory: Any,
        val restaurantCatagoryId: Int,
        val restaurantId: Any
    )
}

