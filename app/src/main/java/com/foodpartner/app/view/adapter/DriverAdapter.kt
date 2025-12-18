package com.foodpartner.app.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foodpartner.app.R
import com.foodpartner.app.databinding.ItemDriverBinding

class DriverAdapter(
    private val list: List<Map<String, Any>>,
    private val onClick: (Map<String, Any>) -> Unit
) : RecyclerView.Adapter<DriverAdapter.VH>() {

    inner class VH(val binding: ItemDriverBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            ItemDriverBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        val driver = list[position]
        val binding = holder.binding

        val isBusy = driver["isBusy"] as? Boolean ?: false
        val status = driver["status"]?.toString() ?: "Offline"

        // -------- BASIC INFO --------
        binding.txtName.text = driver["name"]?.toString() ?: ""
        binding.txtMobile.text = driver["mobileNumber"]?.toString() ?: ""
        binding.txtAddress.text = driver["landmark"]?.toString() ?: ""

        // -------- STATUS --------
        binding.txtStatus.text = status
        binding.txtStatus.setTextColor(
            if (status.equals("Online", true))
                Color.parseColor("#2E7D32")
            else
                Color.GRAY
        )

        if (isBusy) {
            binding.txtBusy.text = "Busy"
            binding.txtBusy.setBackgroundResource(R.drawable.bg_busy)
        } else {
            binding.txtBusy.text = "Available"
            binding.txtBusy.setBackgroundResource(R.drawable.bg_available)
        }

        // -------- IMAGES --------
        Glide.with(binding.imgProfile.context)
            .load(driver["profileImage"])
            .placeholder(R.drawable.ic_image_loader) // loader at center
            .error(R.drawable.ic_image_error)
            .into(binding.imgProfile)

        Glide.with(binding.imgAadhar.context)
            .load(driver["aadharImage"])
            .placeholder(R.drawable.ic_image_loader) // loader at center
            .error(R.drawable.ic_image_error)
            .into(binding.imgAadhar)

        Glide.with(binding.imgLicense.context)
            .load(driver["licenseImage"])
            .placeholder(R.drawable.ic_image_loader) // loader at center
            .error(R.drawable.ic_image_error)
            .into(binding.imgLicense)

        Glide.with(binding.imgPassport.context)
            .load(driver["passportImage"])
            .placeholder(R.drawable.ic_image_loader) // loader at center
            .error(R.drawable.ic_image_error)
            .into(binding.imgPassport)

        // -------- CLICK --------
        binding.root.setOnClickListener {
            onClick(driver)
        }
    }

    override fun getItemCount(): Int = list.size
}
