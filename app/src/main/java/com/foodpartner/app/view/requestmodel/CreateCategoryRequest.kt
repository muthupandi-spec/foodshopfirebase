package com.foodpartner.app.view.requestmodel

data class CreateCategoryRequest(
    val restaurantCatagory: String,
    val restaurantBo: RestaurantBo
)

data class RestaurantBo(
    val restaurantId: Int
)

