package com.foodpartner.app.view.responsemodel

import com.google.firebase.Timestamp

data class CouponModel(
    val id: String = "",
    val code: String = "",
    val discountType: String = "flat",
    val discountValue: Double = 0.0,
    val minOrderAmount: Double = 0.0,
    val expiryDate: Timestamp? = null,  // ✅ ? added
    val isActive: Boolean = true
)