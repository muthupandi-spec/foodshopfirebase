package com.foodpartner.app.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foodpartner.app.R
import com.foodpartner.app.databinding.ItemOfferBinding
import com.foodpartner.app.view.responsemodel.OfferModel

class OfferAdapter(
    private val list: ArrayList<OfferModel>,
    private val listener: OfferClick
) : RecyclerView.Adapter<OfferAdapter.ViewHolder>() {

    interface OfferClick {
        fun onDelete(offer: OfferModel)
    }

    inner class ViewHolder(val binding: ItemOfferBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemOfferBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]


        Glide.with(holder.binding.root)
            .load(item.bannerUrl)
            .placeholder(R.drawable.plus)
            .into(holder.binding.imgBanner)

        holder.binding.btnDelete.setOnClickListener {
            listener.onDelete(item)
        }
    }
}
