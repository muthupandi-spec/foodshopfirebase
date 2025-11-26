package com.foodpartner.app.baseClass

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T>(val dataList: ArrayList<*>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return onCreateViewHolderBase(parent, viewType)
    }

    abstract fun onCreateViewHolderBase(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        this.onBindViewHolderBase(holder, position)
    }

    abstract fun onBindViewHolderBase(holder: RecyclerView.ViewHolder, position: Int)
    override fun getItemCount(): Int = if (dataList.size > 0) dataList.size else 5
    operator fun get(position: Int): Any {
        return dataList[position]
    }
    fun showBase64Image(base64String: String, imageView: ImageView) {
        // Decode Base64 string into a byte array
        val decodedString: ByteArray = Base64.decode(base64String, Base64.DEFAULT)

        // Convert byte array to Bitmap
        val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

        // Set the Bitmap to the ImageView
        imageView.setImageBitmap(bitmap)
    }
}