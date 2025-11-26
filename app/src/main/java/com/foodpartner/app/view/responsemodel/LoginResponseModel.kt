package com.foodpartner.app.view.responsemodel

data class LoginResponseModel(
    val accessToken: String,
    val customerId: Any,
    val deliveryPartnerId: Any,
    val email: String,
    val id: Int,
    val mobileNumber: String,
    val restaurantId: Int,
    val roles: List<String>,
    val tokenType: String,
    val username: String
)