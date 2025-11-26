package com.foodpartner.app.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.app.washeruser.repository.Status
import com.foodpartner.app.R
import com.foodpartner.app.ResponseMOdel.RestaurantResponsemodel
import com.foodpartner.app.baseClass.BaseActivity.BaseActivity
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentSampleBinding
import com.foodpartner.app.databinding.FragmentShopfragmentBinding
import com.foodpartner.app.view.adapter.Activeadapter
import com.foodpartner.app.view.adapter.ShopAdapter
import com.foodpartner.app.view.responsemodel.RestuarantModel
import com.foodpartner.app.view.responsemodel.UserregisterResponseModel
import com.foodpartner.app.viewModel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kotlintest.app.utility.interFace.CommonInterface
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Timer
import kotlin.concurrent.schedule

class Shopfragment : BaseFragment<FragmentShopfragmentBinding>() {

    private val db = FirebaseFirestore.getInstance()

    override fun initView(mViewDataBinding: ViewDataBinding?) {

        fetchShopData()  // Fetch shop when page opens

        this.mViewDataBinding.apply {

            shopcontainer.setOnClickListener {
                loadFragment(ProductCategoryFragment(), android.R.id.content, "homepage", true)
            }

            addNewShop.setOnClickListener {
                loadFragment(ShopCreateFragment(), android.R.id.content, "shopecreate", true)
            }

            shopswitch.setOnCheckedChangeListener { _, isChecked ->
                updateShopStatus(if (isChecked) "online" else "offline")
            }

        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_shopfragment

    // ---------------------------------------------------------
    // FETCH SHOP FROM FIRESTORE
    // ---------------------------------------------------------
    private fun fetchShopData() {
         val uid = FirebaseAuth.getInstance().currentUser?.uid
        println("iddd"+uid.toString())
        if (uid == null) {
            showToast("User ID not found")
            return
        }

        showLoader()

        db.collection("shops")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                hideLoader()

                if (document.exists()) {

                    val name = document.getString("restaurantName") ?: ""
                    val shopType = document.getString("restaurantType") ?: ""
                    val status = document.getString("mode") ?: "offline"
                    val image = document.getString("profileImage") ?: ""

                    // UI Update
                    mViewDataBinding.shopname.text = name
                    mViewDataBinding.shop.text = shopType

                    // Switch mode update
                    mViewDataBinding.shopswitch.isChecked = status == "online"

                } else {
                    showToast("No shop registered")
                }
            }
            .addOnFailureListener {
                hideLoader()
                showToast("Failed to fetch shop: ${it.message}")
            }
    }

    // ---------------------------------------------------------
    // SWITCH TO UPDATE SHOP STATUS
    // ---------------------------------------------------------

    private fun updateShopStatus(newStatus: String) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) return

        db.collection("shops")
            .document(uid)
            .update("mode", newStatus)
            .addOnSuccessListener {
                showToast("Shop is now: $newStatus")
            }
            .addOnFailureListener {
                showToast("Failed to update status")
            }
    }

}
