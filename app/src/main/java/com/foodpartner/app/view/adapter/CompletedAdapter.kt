package com.foodpartner.app.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.foodpartner.app.R
import com.foodpartner.app.ResponseMOdel.CompleteddOrderResponModelItem
import com.foodpartner.app.ResponseMOdel.canncelleteddOrderResponModelItem
import com.foodpartner.app.baseClass.BaseAdapter
import com.foodpartner.app.databinding.CompletedorderadapterBinding
import com.kotlintest.app.utility.interFace.CommonInterface

class CompletedAdapter (
    private val aminitylistcategory: ArrayList<CompleteddOrderResponModelItem>,
    var commonInterface: CommonInterface
) : BaseAdapter<Any>(aminitylistcategory) {
    lateinit var context: Context
    override fun onCreateViewHolderBase(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return adapterviewholder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.completedorderadapter, parent, false)
        )
    }

    class adapterviewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var databding: CompletedorderadapterBinding? = null

        init {
            databding = DataBindingUtil.bind<ViewDataBinding>(itemView) as CompletedorderadapterBinding
        }

        fun getBinding(): CompletedorderadapterBinding {
            return databding!!
        }
    }

    override fun onBindViewHolderBase(holder: RecyclerView.ViewHolder, position: Int) {
        var binding = (holder as CompletedAdapter.adapterviewholder).getBinding()
      /*  showBase64Image(
            aminitylistcategory.get(position).orderItems.get(0).product.image.toString(),
            binding.foodImgBg
        )*/

        val order = aminitylistcategory[position]
        val item = order.orderItems.firstOrNull() // safe access to first item

        if (item != null) {
            binding.foodName.text = item.foodName
            binding.foodCount.text = item.quantity.toString()+" Items"
            binding.foodCost.text = item.subTotal.toString()+" AED"
            binding.orderid.text = "# Order Id " + item.orderItemId.toString()
            showBase64Image(
                item.product.image.toString(),
                binding.foodImgBg
            )
        }

        binding.orderdetail.setOnClickListener {
            val map = HashMap<String, Any>()
            map.put("click", "detail")
            map.put("orderId", aminitylistcategory.get(position).orderId.toString())
            commonInterface.commonCallback(map)
        }

    }

    override fun getItemCount(): Int {
        return aminitylistcategory.size
    }
}