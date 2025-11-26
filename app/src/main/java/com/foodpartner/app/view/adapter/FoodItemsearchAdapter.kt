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
import com.foodpartner.app.baseClass.BaseAdapter
import com.foodpartner.app.databinding.ActiveorderadapterBinding
import com.foodpartner.app.databinding.FooditemsearchadapterBinding
import com.foodpartner.app.view.responsemodel.RestuarantModel
import com.kotlintest.app.utility.interFace.CommonInterface

class FoodItemsearchAdapter(
    private var aminitylistcategory: ArrayList<RestuarantModel>,
    var commonInterface: CommonInterface
) : BaseAdapter<Any>(aminitylistcategory) {
    lateinit var context: Context
    fun filterList(filterlist: ArrayList<RestuarantModel>) {
        aminitylistcategory = filterlist
        notifyDataSetChanged()
    }
    override fun onCreateViewHolderBase(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return adapterviewholder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.fooditemsearchadapter, parent, false)
        )
    }

    class adapterviewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var databding: FooditemsearchadapterBinding? = null

        init {
            databding =
                DataBindingUtil.bind<ViewDataBinding>(itemView) as FooditemsearchadapterBinding
        }

        fun getBinding(): FooditemsearchadapterBinding {
            return databding!!
        }
    }

    override fun onBindViewHolderBase(holder: RecyclerView.ViewHolder, position: Int) {
        var binding = (holder as FoodItemsearchAdapter.adapterviewholder).getBinding()
        binding.foodname.text=aminitylistcategory[position].foodname
//binding.orderid.text=aminitylistcategory[position].rating
        holder.itemView.setOnClickListener {
            commonInterface.commonCallback(aminitylistcategory[position])
        }

    }

    override fun getItemCount(): Int {
        return aminitylistcategory.size
    }
}