package com.foodpartner.app.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foodpartner.app.R
import com.foodpartner.app.ResponseMOdel.OrderRecieveModellItem
import com.foodpartner.app.baseClass.BaseAdapter
import com.foodpartner.app.databinding.ActiveorderadapterBinding
import com.foodpartner.app.network.Constant
import com.foodpartner.app.network.OrderStatus
import com.foodpartner.app.utility.GlideApp
import com.foodpartner.app.view.responsemodel.RestuarantModel
import com.kotlintest.app.utility.interFace.CommonInterface
import kotlin.toString

class Activeadapter (
    private val items: List<Map<String, Any>>,
    private val callback: CommonInterface
) : RecyclerView.Adapter<Activeadapter.VH>() {

    inner class VH(val binding: ActiveorderadapterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Activeadapter.VH {
        val bind = DataBindingUtil.inflate<ActiveorderadapterBinding>(
            LayoutInflater.from(parent.context), R.layout.activeorderadapter, parent, false)
        return VH(bind)
    }
    override fun onBindViewHolder(holder: VH, position: Int) {

        val item = items[position]
        val orderId = item["orderId"].toString()
        val otpValue = item["otp"]?.toString() ?: ""

        if (otpValue.isNotEmpty()) {
            holder.binding.otp.visibility = View.VISIBLE
            holder.binding.otp.text = "OTP: $otpValue"
        } else {
            holder.binding.otp.visibility = View.GONE
        }

        val status = item["orderStatus"] as? String ?: ""
        // FULL orderItems list
        val orderItems = item["orderItems"] as? List<Map<String, Any>> ?: emptyList()

        // show first item for main display
        val firstItem = orderItems.firstOrNull()

        if (firstItem != null) {
            val foodName = firstItem["foodName"].toString()
            val price = firstItem["price"].toString()
            val qty = firstItem["quantity"].toString()
            val image = firstItem["foodimage"].toString()


            holder.binding.foodName.text = foodName
            holder.binding.foodCost.text = "₹ $price"
            holder.binding.foodCount.text = "$qty qty"

            Glide.with(holder.binding.root)
                .load(image)
                .into(holder.binding.foodImgBg)
        }

        holder.binding.orderid.text = "Order ID: #$orderId"
        holder.binding.status.text = status

        when (status) {
            OrderStatus.ORDER_PLACED -> holder.binding.statusbtnn.text = "Accept Order"
            OrderStatus.ACCEPTED_BY_RESTAURANT -> holder.binding.statusbtnn.text = "Start Preparing"
            OrderStatus.PREPARING -> holder.binding.statusbtnn.text = "Mark Ready"
            OrderStatus.READY_FOR_PICKUP -> holder.binding.statusbtnn.text = "Assign Delivery"
        }
if(status.equals("DELIVERY_ASSIGNED")){
    holder.binding.statusbtnn.visibility= View.GONE
}else{
    holder.binding.statusbtnn.visibility= View.VISIBLE

}
        if(status.equals("DELIVERED")){
            holder.binding.statusbtnn.text = "Delivered"
            holder.binding.statusbtnn.visibility = View.VISIBLE
        }
        holder.binding.statusbtnn.setOnClickListener {
            when (status) {
                OrderStatus.ORDER_PLACED -> callback.commonCallback(mapOf("action" to "accept", "orderId" to item["orderId"].toString()))
                OrderStatus.ACCEPTED_BY_RESTAURANT -> callback.commonCallback(mapOf("action" to "start_preparing", "orderId" to item["orderId"].toString()))
                OrderStatus.PREPARING -> callback.commonCallback(mapOf("action" to "ready", "orderId" to item["orderId"].toString()))
                OrderStatus.READY_FOR_PICKUP -> callback.commonCallback(mapOf("action" to "assign_delivery", "orderId" to item["orderId"].toString()))
            }
        }
    }

    override fun getItemCount(): Int = items.size
}