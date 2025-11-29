package com.foodboy.app.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foodpartner.app.R
import com.bumptech.glide.Glide

class FoodItemAdapter(
    private val items: ArrayList<HashMap<String, Any>>
) : RecyclerView.Adapter<FoodItemAdapter.FoodVH>() {

    inner class FoodVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodImg: ImageView = itemView.findViewById(R.id.foodImage)
        val foodName: TextView = itemView.findViewById(R.id.foodName)
        val foodQty: TextView = itemView.findViewById(R.id.foodQty)
        val foodPrice: TextView = itemView.findViewById(R.id.foodPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_order_list, parent, false)
        return FoodVH(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: FoodVH, position: Int) {
        val item = items[position]

        val name = item["foodName"]?.toString() ?: "Food"
        val qty = item["quantity"]?.toString() ?: "0"
        val price = item["price"]?.toString() ?: "0"
        val img = item["foodimage"]?.toString()

        holder.foodName.text = name
        holder.foodQty.text = "x$qty"
        holder.foodPrice.text = "₹$price"

        if (!img.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(img)
                .placeholder(R.drawable.ic_logo)
                .into(holder.foodImg)
        }
    }
}
