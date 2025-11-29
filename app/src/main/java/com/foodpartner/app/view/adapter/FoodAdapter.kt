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
        val binding = (holder as adapterviewholder).getBinding()
        val item = aminitylistcategory[position]

        binding.foodname.text = item.foodName
        Glide.with(context).load(item.imageUrl).into(binding.foodimage)

        // Switch state
        binding.fooditemcheck.isChecked = item.isActive

        binding.fooditemcheck.setOnCheckedChangeListener { _, isChecked ->
            val map = HashMap<String, Any>()
            map["foodid"] = item.foodId
            map["isActive"] = isChecked.toString()
            map["click"] = "update"
            commonInterface.commonCallback(map)
        }

        // Normal click → edit
        holder.itemView.setOnClickListener {
            val map = HashMap<String, Any>()
            map["click"] = "edit"
            map["foodid"] = item.foodId
            commonInterface.commonCallback(map)
        }

        // LONG PRESS → DELETE
        holder.itemView.setOnLongClickListener {

            val map = HashMap<String, Any>()
            map["click"] = "delete"
            map["foodid"] = item.foodId
            map["foodname"] = item.foodName

            commonInterface.commonCallback(map)
            true
        }
    }

    override fun getItemCount(): Int {
        return aminitylistcategory.size
    }
}