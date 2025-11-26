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
import com.foodpartner.app.utility.GlideApp
import com.foodpartner.app.view.responsemodel.RestuarantModel
import com.kotlintest.app.utility.interFace.CommonInterface
import kotlin.toString

class Activeadapter (
    private val aminitylistcategory: ArrayList<OrderRecieveModellItem>,
    var commonInterface: CommonInterface
) : BaseAdapter<Any>(aminitylistcategory) {
    lateinit var context: Context
    override fun onCreateViewHolderBase(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return adapterviewholder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.activeorderadapter, parent, false)
        )
    }

    class adapterviewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var databding: ActiveorderadapterBinding? = null

        init {
            databding = DataBindingUtil.bind<ViewDataBinding>(itemView) as ActiveorderadapterBinding
        }

        fun getBinding(): ActiveorderadapterBinding {
            return databding!!
        }
    }

    override fun onBindViewHolderBase(holder: RecyclerView.ViewHolder, position: Int) {
        var binding = (holder as Activeadapter.adapterviewholder).getBinding()

        val order = aminitylistcategory[position]
        val item = order.orderItems.firstOrNull() // safe access to first item
        val otp = order.otp
        otp?.let {
            println("OTP is: $it")
            binding.otp.text="OTP: ${otp}"
        } ?: run {
            println("OTP is null")
        }

//848708

        if (item != null) {
            binding.foodName.text = item.foodName
            binding.status.text = order.status
            binding.foodCount.text = item.quantity.toString()+" Items"
            binding.foodCost.text = item.subTotal.toString()+" AED"
            binding.orderid.text = "# Order Id " + item.orderItemId.toString()
            showBase64Image(
                item.product.image.toString(),
                binding.foodImgBg
            )
        }

        if(aminitylistcategory[position].orderStatus.equals("Your Order is Placed")){
            binding.trackOrderBtn.text = "Accept Order"

        }
    else if(aminitylistcategory[position].orderStatus.equals("Order is accepted")){
    binding.trackOrderBtn.text = "Enable Packing"
    binding.cancelBtn.visibility = View.GONE
}else if(aminitylistcategory[position].orderStatus.equals("EnablePacking")){
    binding.trackOrderBtn.text = "Searching for a boy"
    binding.cancelBtn.visibility = View.GONE
}else if(aminitylistcategory[position].orderStatus.equals("Assign Partner")){
    binding.trackOrderBtn.text = "Handover to delivery boy"
    binding.cancelBtn.visibility = View.GONE
}else if(aminitylistcategory[position].orderStatus.equals("HANDOVER_TO_DELIVERY")){
            binding.trackOrderBtn.text = "Order Completed"
            binding.cancelBtn.visibility = View.GONE

        }


        binding.trackOrderBtn.setOnClickListener {
            Constant.orderid=aminitylistcategory[position].orderId.toString()
            commonInterface.commonCallback(binding.trackOrderBtn.text.toString())
            println("dghjs")
        }

//        GlideApp.with(context)
//            .load(aminitylistcategory[position].foodName)
//            .into(Images)

        binding.cancelBtn.setOnClickListener {
            Constant.orderid=aminitylistcategory[position].orderId.toString()
            commonInterface.commonCallback("cancel")
        }

        holder.itemView.setOnClickListener {
            Constant.orderid=aminitylistcategory[position].orderId.toString()
            commonInterface.commonCallback("detail")
            Constant.foodid=item!!.orderItemId.toString()

        }

    }

    override fun getItemCount(): Int {
        return aminitylistcategory.size
    }
}