package com.foodpartner.app.ResponseMOdel

class FoodItemResponemodel : ArrayList<FoodItemResponemodelItem>()

data class FoodItemResponemodelItem(
    var foodId: String = "",
    var foodName: String = "",
    var description: String = "",
    var briefDescription: String = "",
    var imageUrl: String = "",
    var price: String = "",
    var type: String = "",
    var categoryId: String = "",
    var isActive: Boolean = true,
    var createdAt: Long = 0L
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

