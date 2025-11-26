package com.foodpartner.app.ResponseMOdel

class FoodItemResponemodel : ArrayList<FoodItemResponemodelItem>()

data class FoodItemResponemodelItem(
    val catagoryId: Int,
    val catagorybo: Any,
    val decription: String,
    val decription1: String,
    val decription2: String,
    val foodId: Int,
    val foodName: String,
    val image: String,
    val imageData: Any,
    val isActive: Boolean,
    val isDelete: Boolean,
    val price: Int,
    val restaurantCatagoryBO: RestaurantCatagoryBO,
    val type: String
){
    data class RestaurantCatagoryBO(
        val isActive: Any,
        val isDelete: Any,
        val restaurantBo: Any,
        val restaurantCatagory: String,
        val restaurantCatagoryId: Int,
        val restaurantId: Int
    )
}

