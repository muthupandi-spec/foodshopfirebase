package com.foodpartner.app.ResponseMOdel

 class CompletedOrderResponseModel : ArrayList<CompleteddOrderResponModelItem>()

data class CompleteddOrderResponModelItem(
    val address: Any?="",
    val cart: Cart,
    val city: String?="",
    val customer: Customer,
    val customerId: Int?=0,
    val customerLat: Double?=0.0,
    val customerLng: Double?=0.0,
    val dateCreated: String?="",
    val firstName: String?="",
    val foodNames: Any?="",
    val isActive: Any?="",
    val isDelete: Any?="",
    val landMark: String?="",
    val lastName: String?="",
    val mobileNumber: String?="",
    val orderId: Int?=0,
    val orderItems: List<OrderItem>,
    val orderStatus: String?="",
    val paymentStatus: Any?="",
    val pincode: Int?=0,
    val restaurantLat: Double?=0.0,
    val restaurantLng: Double?=0.0,
    val shopDeviceToken: Any?="",
    val status: String?="",
    val street: String?="",
    val totalAmount: Double?=0.0
){
    data class Cart(
        val cartItems: Any?="",
        val customerId: Int?=0,
        val id: Int?=0,
        val isActive: Any?="",
        val isDelete: Any?="",
        val totalPrice: Double?=0.0
    )
    data class Customer(
        val confirmpassword: Any?="",
        val customerId: Int?=0,
        val email: String?="",
        val fcmToken: Any?="",
        val firstName: String?="",
        val isDelete: Any?="",
        val isVerified: Any?="",
        val lastName: String?="",
        val mobileNumber: String?="",
        val otp: Any?="",
        val password: Any?=""
    )
    data class OrderItem(
        val foodName: String?="",
        val orderItemId: Int?=0,
        val price: Double?=0.0,
        val product: Product,
        val quantity: Int?=0,
        val subTotal: Double?=0.0
    ){
        data class Product(
            val catagoryId: Int?=0,
            val catagorybo: Catagorybo,
            val decription: String?="",
            val decription1: String?="",
            val decription2: String?="",
            val foodId: Int?=0,
            val foodName: String?="",
            val image: String?="",
            val imageData: Any?="",
            val isActive: Any?="",
            val isDelete: Any?="",
            val price: Int?=0,
            val restaurantCatagoryBO: Any?="",
            val type: Any?=""
        ){
            data class Catagorybo(
                val catagory: String?="",
                val catagoryId: Int?=0,
                val isActive: Any?="",
                val isDelete: Any?="",
                val time: Any?=""
            )
        }

    }

}

