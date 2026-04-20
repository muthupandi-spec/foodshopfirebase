package com.foodpartner.app.view.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.foodpartner.app.databinding.ItemCouponBinding
import com.foodpartner.app.view.responsemodel.CouponModel

import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.apply
import kotlin.let

class CouponAdapter(
    private val list: ArrayList<CouponModel>,
    private val onAction: (CouponModel, String) -> Unit
) : RecyclerView.Adapter<CouponAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCouponBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val coupon = list[position]
        holder.binding.apply {
            tvCouponCode.text    = coupon.code
            tvDiscount.text      = if (coupon.discountType == "percentage")
                "${coupon.discountValue.toInt()}% OFF"
            else
                "AED ${coupon.discountValue} OFF"

            tvMinOrder.text      = "Min order: AED ${coupon.minOrderAmount}"
            tvExpiry.text        = coupon.expiryDate?.toDate()?.let {
                "Expires: " + SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it)
            } ?: "No expiry"

            switchActive.isChecked = coupon.isActive
            switchActive.setOnCheckedChangeListener(null)
            switchActive.setOnCheckedChangeListener { _, _ ->
                onAction(coupon, "toggle")
            }

            btnDelete.setOnClickListener { onAction(coupon, "delete") }
        }
    }
}