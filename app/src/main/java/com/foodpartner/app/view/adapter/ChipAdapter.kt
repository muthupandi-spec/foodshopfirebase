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

class ChipAdapter(
    private val categoryList: ArrayList<GetallCategoryResponseModelItem>,
    private val commonInterface: CommonInterface
) : BaseAdapter<Any>(categoryList) {

    private lateinit var context: Context
    private var selectedPosition = 0

    override fun onCreateViewHolderBase(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_chips, parent, false)
        )
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: AdapterChipsBinding =
            DataBindingUtil.bind(itemView)!!
    }

    override fun onBindViewHolderBase(holder: RecyclerView.ViewHolder, position: Int) {

        val binding = (holder as ViewHolder).binding
        val item = categoryList[position]

        // Set category text
        binding.foodName.text = item.categoryName
        println("category"+item.categoryName)
        // Selected / unselected UI
        if (position == selectedPosition) {
            binding.recommendChip.background =
                context.getDrawable(R.drawable.custom_selected_solidtextview)
            binding.foodName.setTextColor(context.resources.getColor(R.color.White))
            commonInterface.commonCallback(item.categoryId)

        } else {
            binding.recommendChip.background =
                context.getDrawable(R.drawable.custom_selected_textview)
            binding.foodName.setTextColor(context.resources.getColor(R.color.Black))
        }

        // Click
        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()

            // **PASS categoryId**
            commonInterface.commonCallback(item.categoryId)
        }
    }

    override fun getItemCount(): Int = categoryList.size
}
