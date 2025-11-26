package com.foodpartner.app.view.fragment

import android.view.View
import androidx.databinding.ViewDataBinding
import com.app.washeruser.repository.Status
import com.foodpartner.app.R
import com.foodpartner.app.ResponseMOdel.FoodItemResponemodel
import com.foodpartner.app.ResponseMOdel.FoodItemResponemodelItem
import com.foodpartner.app.ResponseMOdel.GetallCategoryResponseModel
import com.foodpartner.app.ResponseMOdel.GetallCategoryResponseModelItem
import com.foodpartner.app.ResponseMOdel.UpdatecategoryResponsemodel
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.ProductfragmentBinding
import com.foodpartner.app.network.Constant
import com.foodpartner.app.view.adapter.ChipAdapter
import com.foodpartner.app.view.adapter.FoodAdapter
import com.foodpartner.app.view.eventmodel.FoodCreateEvent
import com.foodpartner.app.view.eventmodel.FoodEditEvent
import com.foodpartner.app.view.requestmodel.LocationEvent
import com.foodpartner.app.viewModel.HomeViewModel
import com.kotlintest.app.utility.interFace.CommonInterface
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductCategoryFragment : BaseFragment<ProductfragmentBinding>() {
    private val homeViewModel by viewModel<HomeViewModel>()
    var categorylist: ArrayList<GetallCategoryResponseModelItem> = ArrayList()
    var foodlist: ArrayList<FoodItemResponemodelItem> = ArrayList()
    var foodtype: String = "true"

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        adapter()
        this.mViewDataBinding.apply {
            active.setOnClickListener {
                foodtype = "true"
            }
            inactive.setOnClickListener {
                foodtype = "false"
            }

            homeViewModel.getallcategory(sharedHelper.getFromUser("userid"))
            homeViewModel.response().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                processResponse(it)
            })

            backBtn.setOnClickListener {
                fragmentManagers!!.popBackStackImmediate()
            }
            plus.setOnClickListener {
                loadFragment(ProductDetailFragment(), android.R.id.content, "produ", true)
            }
            search.setOnClickListener {
                loadFragment(SearchFragment(), android.R.id.content, "search", true)
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.productfragment


    fun adapter() {

    }

    private fun processResponse(response: com.foodpartner.app.network.Response) {
        when (response.status) {
            Status.SUCCESS -> {
                this.mViewDataBinding.loader.visibility= View.GONE
                when (response.data) {
                    is GetallCategoryResponseModel -> {
                        categorylist.clear()
                        categorylist.addAll(response.data)
                        val chipadaptert = ChipAdapter(categorylist, object : CommonInterface {
                            override fun commonCallback(any: Any) {
                                mViewDataBinding.loader.visibility = View.VISIBLE
                                val map: HashMap<String, String> = HashMap()
                                Constant.restaurantcategory = any.toString()
                                map.put("restaurantCatagoryId", any.toString())
                                map.put("isActive", foodtype)
                                homeViewModel.getcategoryfooditem(map)
                            }


                        })
                        this.mViewDataBinding.recommendChipRC.adapter = chipadaptert
                    }

                    is FoodItemResponemodel -> {
                        this.mViewDataBinding.loader.visibility = View.GONE
                        foodlist.clear()

                        foodlist.addAll(response.data)

                        if (foodlist.isEmpty() || foodlist == null) {
                            mViewDataBinding.emptyimg.visibility = View.VISIBLE
                            mViewDataBinding.emptytxt.visibility = View.VISIBLE
                            mViewDataBinding.productrecyclerview.visibility = View.GONE
                        } else {
                            mViewDataBinding.emptyimg.visibility = View.GONE
                            mViewDataBinding.emptytxt.visibility = View.GONE
                            mViewDataBinding.productrecyclerview.visibility = View.VISIBLE
                            val foodadapter = FoodAdapter(foodlist, object : CommonInterface {
                                override fun commonCallback(any: Any) {
                                    when (any) {
                                        is HashMap<*, *> -> {
                                            if (any["click"].toString().equals("edit")) {
                                                loadFragment(
                                                    ProductEditDetailFragment(any["foodid"].toString()),
                                                    android.R.id.content,
                                                    "product",
                                                    true
                                                )
                                            } else if (any["click"].toString().equals("update")) {
                                                println("sucesssss")
                                                val map1: HashMap<String, String> = HashMap()
                                                map1.put("isActive", any["isActive"].toString())
                                                homeViewModel.updatecategory(
                                                    any["foodid"].toString(),
                                                    map1
                                                )
                                            }


                                        }
                                    }


                                }

                            })

                            this.mViewDataBinding.productrecyclerview.adapter = foodadapter

                        }


                    }

                    is UpdatecategoryResponsemodel -> {
                        homeViewModel.getallcategory(sharedHelper.getFromUser("userid"))

                    }

                }
            }

            Status.ERROR -> {
                this.mViewDataBinding.loader.visibility= View.GONE

            }

            Status.LOADING -> {}
            Status.SECONDLOADING -> {}
            Status.DISMISS -> {}
        }
    }


    override fun onStart() {
        super.onStart()
        // ✅ Register EventBus listener
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        // ✅ Unregister when fragment stops
        EventBus.getDefault().unregister(this)
    }

    // ✅ Listener method (must be public)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun foodcreate(event: FoodCreateEvent) {
        homeViewModel.getallcategory(sharedHelper.getFromUser("userid"))


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun foodedit(event: FoodEditEvent) {

        homeViewModel.getallcategory(sharedHelper.getFromUser("userid"))


    }
}
