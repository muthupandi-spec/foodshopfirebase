package com.foodpartner.app.ResponseMOdel

class OrderRecieveModell : ArrayList<OrderRecieveModellItem>()

data class OrderRecieveModellItem(
    val address: Any,
    val cart: Cart,
    val city: String,
    val customer: Customer,
    val customerId: Int,
    val customerLat: Double,
    val customerLng: Double,
    val dateCreated: String,
    val firstName: String,
    val foodNames: Any,
    val isActive: Any,
    val isDelete: Any,
    val landMark: String,
    val lastName: String,
    val mobileNumber: String,
    val orderId: Int,
    val orderItems: List<OrderItem>,
    val orderStatus: String,
    val paymentStatus: Any,
    val pincode: Int,
    val otp: String,
    val restaurantId: Int,
    val restaurantLat: Double,
    val restaurantLng: Double,
    val shopDeviceToken: Any,
    val status: String,
    val street: String,
    val totalAmount: Double
){
    data class Cart(
        val cartItems: Any,
        val customerId: Int,
        val id: Int,
        val isActive: Any,
        val isDelete: Any,
        val totalPrice: Double
    )
    data class Customer(
        val confirmpassword: Any,
        val customerId: Int,
        val dateCreated: Any,
        val email: String,
        val fcmToken: Any,
        val firstName: String,
        val isDelete: Any,
        val isVerified: Any,
        val lastName: String,
        val mobileNumber: String,
        val otp: Any,
        val password: Any
    )
    data class OrderItem(
        val foodName: String,
        val orderItemId: Int,
        val price: Double,
        val product: Product,
        val quantity: Int,
        val subTotal: Double
    ){
        data class Product(
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
            val type: Any
        ){
            data class RestaurantCatagoryBO(
                val isActive: Any,
                val isDelete: Any,
                val restaurantBo: Any,
                val restaurantCatagory: String,
                val restaurantCatagoryId: Int,
                val restaurantId: Any
            )
        }

    }


}






