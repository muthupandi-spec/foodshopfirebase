package com.foodpartner.app.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.foodpartner.app.R
import com.foodpartner.app.ResponseMOdel.GetallCategoryResponseModelItem
import com.foodpartner.app.baseClass.BaseAdapter
import com.foodpartner.app.databinding.AdapterChipsBinding

import com.kotlintest.app.utility.interFace.CommonInterface

class ChipAdapter (
    private val aminitylistcategory: ArrayList<GetallCategoryResponseModelItem>,
    var commonInterface: CommonInterface
) :
BaseAdapter<Any>(aminitylistcategory) {
    lateinit var context: Context
    private var click = 0
    override fun onCreateViewHolderBase(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return adapterviewholder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.adapter_chips, parent, false)
        )
    }
    class adapterviewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var databding: AdapterChipsBinding? = null
        init {
            databding = DataBindingUtil.bind<ViewDataBinding>(itemView) as AdapterChipsBinding
        }

        fun getBinding(): AdapterChipsBinding {
            return databding!!
        }
    }
    override fun onBindViewHolderBase(holder: RecyclerView.ViewHolder, position: Int) {
        var binding = (holder as ChipAdapter.adapterviewholder).getBinding()
        binding.foodName.text =aminitylistcategory[position].restaurantCatagory

        if (position == click) {
            binding.recommendChip.background =context.getDrawable(R.drawable.custom_selected_solidtextview)
            binding.foodName.setTextColor(R.color.White)
            commonInterface.commonCallback(aminitylistcategory[position].restaurantCatagoryId)
        } else {
            binding.recommendChip.background =context.getDrawable(R.drawable.custom_selected_textview)
        }
        holder.itemView.setOnClickListener {
            click = position
            notifyDataSetChanged()
            commonInterface.commonCallback(aminitylistcategory[position].restaurantCatagoryId)
        }

    }
    override fun getItemCount(): Int {
        return aminitylistcategory.size
    }
}