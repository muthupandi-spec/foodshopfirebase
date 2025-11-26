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
import com.kotlintest.app.utility.interFace.CommonInterface
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Timer
import kotlin.concurrent.schedule

class Shopfragment : BaseFragment<FragmentShopfragmentBinding>() {
    private val homeViewModel by viewModel<HomeViewModel>()

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        adapter()
        this.mViewDataBinding.apply {

            swipe.setOnRefreshListener {

                homeViewModel.getrestaurant(sharedHelper.getFromUser("userid"))
                swipe.isRefreshing=false
            }
            shopcontainer.setOnClickListener {
                loadFragment(ProductCategoryFragment(), android.R.id.content, "homepage", true)

            }
            addNewShop.setOnClickListener {
                loadFragment(ShopCreateFragment(), android.R.id.content, "shopecreate", true)
            }
        }
        homeViewModel.getrestaurant(sharedHelper.getFromUser("userid"))

        homeViewModel.response().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            processResponse(it)
        })
    }

    override fun getLayoutId(): Int = R.layout.fragment_shopfragment


    fun adapter() {
        var bannerlist: ArrayList<RestuarantModel> = ArrayList()
        bannerlist.add(RestuarantModel("Chicken 65", "Order ID: 985451", "", "", 0))
        bannerlist.add(RestuarantModel("Chicken Thanthuri", "Order ID: 65446", "", "", 0))
        bannerlist.add(RestuarantModel("Chicken Lollipop", "Order ID: 54622", "", "", 0))

        val bannerAdapter = ShopAdapter(bannerlist, object : CommonInterface {
            override fun commonCallback(any: Any) {
                if(any.toString().equals("switch")){
                    val map:HashMap<String,String> = HashMap()
                    map["userid"] ="5664"
                    map["shop"]="update"
                    homeViewModel.updateshop(map)

                }else{
                    loadFragment(ProductCategoryFragment(), android.R.id.content, "homepage", true)

                }


            }

        })

        this.mViewDataBinding.shoprecycler.adapter = bannerAdapter
    }
    private fun processResponse(response: com.foodpartner.app.network.Response) {
        when (response.status) {
            Status.SUCCESS -> {
                this.mViewDataBinding.loader.visibility= View.GONE

                when (response.data) {
                    is RestaurantResponsemodel -> {
                        mViewDataBinding.shopname.text=response.data.restaurantName
                        mViewDataBinding.shop.text=response.data.restaurantCity
                    }

                }
            }

            Status.ERROR -> {
            }

            Status.LOADING -> {}
            Status.SECONDLOADING -> {}
            Status.DISMISS -> {}
        }
    }

}
