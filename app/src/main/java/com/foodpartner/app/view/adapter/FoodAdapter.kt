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
import com.foodpartner.app.ResponseMOdel.FoodItemResponemodel
import com.foodpartner.app.ResponseMOdel.FoodItemResponemodelItem
import com.foodpartner.app.baseClass.BaseAdapter
import com.foodpartner.app.databinding.ActiveorderadapterBinding
import com.foodpartner.app.databinding.FoodadapterBinding
import com.foodpartner.app.network.Constant
import com.foodpartner.app.view.responsemodel.RestuarantModel
import com.kotlintest.app.utility.interFace.CommonInterface

class FoodAdapter(
    private var aminitylistcategory: ArrayList<FoodItemResponemodelItem>,
    var commonInterface: CommonInterface
) : BaseAdapter<Any>(aminitylistcategory) {
    lateinit var context: Context
    fun filterList(filterlist: ArrayList<FoodItemResponemodelItem>) {
        aminitylistcategory = filterlist
        notifyDataSetChanged()
    }

    override fun onCreateViewHolderBase(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return adapterviewholder(
            LayoutInflater.from(parent.context).inflate(R.layout.foodadapter, parent, false)
        )
    }

    class adapterviewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var databding: FoodadapterBinding? = null

        init {
            databding = DataBindingUtil.bind<ViewDataBinding>(itemView) as FoodadapterBinding
        }

        fun getBinding(): FoodadapterBinding {
            return databding!!
        }
    }

    override fun onBindViewHolderBase(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as FoodAdapter.adapterviewholder).getBinding()
        val item = aminitylistcategory[position]

        binding.foodname.text = item.foodName
        Glide.with(context).load(item.imageUrl).into(binding.foodimage)

        // Make sure the switch reflects current item state (if applicable)
        binding.fooditemcheck.isChecked = item.isActive // Optional if your model has such a field

        // Handle switch state changes
        binding.fooditemcheck.setOnCheckedChangeListener { _, isChecked ->
            // Send true or false based on switch state
            if (isChecked) {
                println("ischeckeddd"+isChecked)
                val map = HashMap<String, Any>()
                map.put("foodid", item.foodId)
                map.put("isActive", "true")
                map.put("click", "update")
                commonInterface.commonCallback(map)
            } else {
                val map = HashMap<String, Any>()
                map.put("foodid", item.foodId)
                map.put("isActive", "false")
                map.put("click", "update")

                commonInterface.commonCallback(map)
            }


            // Optionally update your model if you want to preserve state
            // item.isSelected = isChecked
        }
        holder.itemView.setOnClickListener {

            val map = HashMap<String, Any>()
            map.put("click", "edit")
            map.put("foodid", item.foodId)
            commonInterface.commonCallback(map)
        }
    }

    override fun getItemCount(): Int {
        return aminitylistcategory.size
    }
}