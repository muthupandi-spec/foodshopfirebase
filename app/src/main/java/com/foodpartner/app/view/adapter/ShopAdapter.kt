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
import com.foodpartner.app.databinding.ShopadapterBinding
import com.foodpartner.app.view.responsemodel.RestuarantModel
import com.kotlintest.app.utility.interFace.CommonInterface

class ShopAdapter (
    private val aminitylistcategory: ArrayList<RestuarantModel>,
    var commonInterface: CommonInterface
) : BaseAdapter<Any>(aminitylistcategory) {
    lateinit var context: Context
    override fun onCreateViewHolderBase(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return adapterviewholder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.shopadapter, parent, false)
        )
    }

    class adapterviewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var databding: ShopadapterBinding? = null

        init {
            databding = DataBindingUtil.bind<ViewDataBinding>(itemView) as ShopadapterBinding
        }

        fun getBinding(): ShopadapterBinding {
            return databding!!
        }
    }

    override fun onBindViewHolderBase(holder: RecyclerView.ViewHolder, position: Int) {
        var binding = (holder as ShopAdapter.adapterviewholder).getBinding()

        holder.itemView.setOnClickListener {
            commonInterface.commonCallback(aminitylistcategory[position])
        }
        binding.shopswitch.setOnClickListener {
            commonInterface.commonCallback("switch")

        }
    }

    override fun getItemCount(): Int {
        return aminitylistcategory.size
    }
}