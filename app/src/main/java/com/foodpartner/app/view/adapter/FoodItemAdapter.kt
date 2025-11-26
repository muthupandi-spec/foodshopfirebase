package com.foodpartner.app.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.foodpartner.app.R
import com.foodpartner.app.ResponseMOdel.OrderDetailResponsemodel
import com.foodpartner.app.baseClass.BaseAdapter
import com.foodpartner.app.databinding.FooditemadapterBinding
import com.kotlintest.app.utility.interFace.CommonInterface

class FoodItemAdapter (
    private val aminitylistcategory: ArrayList<OrderDetailResponsemodel.OrderItem>,
    var commonInterface: CommonInterface
) : BaseAdapter<Any>(aminitylistcategory) {
    lateinit var context: Context
    override fun onCreateViewHolderBase(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return adapterviewholder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.fooditemadapter, parent, false)
        )
    }

    class adapterviewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var databding: FooditemadapterBinding? = null

        init {
            databding = DataBindingUtil.bind<ViewDataBinding>(itemView) as FooditemadapterBinding
        }

        fun getBinding(): FooditemadapterBinding {
            return databding!!
        }
    }

    override fun onBindViewHolderBase(holder: RecyclerView.ViewHolder, position: Int) {
        var binding = (holder as FoodItemAdapter.adapterviewholder).getBinding()
binding.fooditem.text=aminitylistcategory[position].foodName
binding.foodCount.text=aminitylistcategory[position].quantity.toString()+" qty"
binding.foodamt.text=aminitylistcategory[position].price.toString()+" AED"
        holder.itemView.setOnClickListener {
            commonInterface.commonCallback(aminitylistcategory[position])
        }

    }

    override fun getItemCount(): Int {
        return aminitylistcategory.size
    }
}