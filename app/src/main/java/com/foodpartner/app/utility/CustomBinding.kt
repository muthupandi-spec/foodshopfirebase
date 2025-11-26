package com.foodpartner.app.utility

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView

object CustomBinding {

    @BindingAdapter("ImageUrl")
    @JvmStatic
    fun loadImage(imageView: ImageView, url: String?) {
        Glide.with(imageView.context).load(url).apply(RequestOptions().circleCrop()).into(imageView)
    }
    @BindingAdapter("StaticImage")
    @JvmStatic
    fun loadStaticImage(imageView: ImageView, url: Int?) {
        Glide.with(imageView.context).load(url).into(imageView)
    }
    @BindingAdapter("url")
    @JvmStatic
    fun urlImage(imageView: ImageView, url: String?) {
        println("image call"+url)
        Glide.with(imageView.context).load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }




    @BindingAdapter("onNavigationItemSelected")
    @JvmStatic
    fun setOnNavigationItemSelected(
        view: BottomNavigationView, listener: BottomNavigationView.OnNavigationItemSelectedListener?
    ) {
        view.setOnNavigationItemSelectedListener(listener)
    }

}