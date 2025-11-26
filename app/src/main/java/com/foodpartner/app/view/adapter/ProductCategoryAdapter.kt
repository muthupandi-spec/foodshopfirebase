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
import com.foodpartner.app.baseClass.BaseAdapter
import com.foodpartner.app.databinding.ActiveorderadapterBinding
import com.foodpartner.app.databinding.ProductcategoryadapterBinding
import com.foodpartner.app.view.responsemodel.RestuarantModel
import com.kotlintest.app.utility.interFace.CommonInterface

class ProductCategoryAdapter(
    private val aminitylistcategory: ArrayList<FoodItemResponemodel>,
    var commonInterface: CommonInterface
) : BaseAdapter<Any>(aminitylistcategory) {
    lateinit var context: Context
    var isVisible = true

    override fun onCreateViewHolderBase(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return adapterviewholder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.productcategoryadapter, parent, false)
        )
    }

    class adapterviewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var databding: ProductcategoryadapterBinding? = null

        init {
            databding =
                DataBindingUtil.bind<ViewDataBinding>(itemView) as ProductcategoryadapterBinding
        }

        fun getBinding(): ProductcategoryadapterBinding {
            return databding!!
        }
    }

    override fun onBindViewHolderBase(holder: RecyclerView.ViewHolder, position: Int) {
        var binding = (holder as ProductCategoryAdapter.adapterviewholder).getBinding()

/*
        holder.itemView.setOnClickListener {
            if (isVisible) {
                isVisible = false
                binding.view1.visibility = View.VISIBLE
                binding.foodrecycler.visibility = View.VISIBLE
                var bannerlist: ArrayList<RestuarantModel> = ArrayList()
                bannerlist.add(RestuarantModel("Chicken 65", "Order ID: 985451", "", "", 0))
                bannerlist.add(RestuarantModel("Chicken Thanthuri", "Order ID: 65446", "", "", 0))
                bannerlist.add(RestuarantModel("Chicken Lollipop", "Order ID: 54622", "", "", 0))

                val adapter = FoodAdapter(bannerlist, object : CommonInterface {
                    override fun commonCallback(any: Any) {
                        commonInterface.commonCallback(aminitylistcategory[position])
                    }

                })
                binding.foodrecycler.adapter = adapter
            } else {
                isVisible = true
                binding.foodrecycler.visibility = View.GONE
                binding.view1.visibility = View.GONE
            }
        }
*/


    }

    override fun getItemCount(): Int {
        return aminitylistcategory.size
    }
}