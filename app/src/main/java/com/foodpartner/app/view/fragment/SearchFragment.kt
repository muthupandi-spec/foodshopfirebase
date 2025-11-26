package com.foodpartner.app.view.fragment

import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.ViewDataBinding
import com.app.washeruser.repository.Status
import com.foodpartner.app.R
import com.foodpartner.app.ResponseMOdel.FoodItemResponemodel
import com.foodpartner.app.ResponseMOdel.FoodItemResponemodelItem
import com.foodpartner.app.ResponseMOdel.GetallCategoryResponseModel
import com.foodpartner.app.ResponseMOdel.Orderresponsenmodel
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentLoginBinding
import com.foodpartner.app.databinding.FragmentOtpBinding
import com.foodpartner.app.databinding.HomefragmentBinding
import com.foodpartner.app.databinding.SearchfoodfragmentBinding
import com.foodpartner.app.network.Constant
import com.foodpartner.app.view.adapter.Activeadapter
import com.foodpartner.app.view.adapter.CancelledAdapter
import com.foodpartner.app.view.adapter.ChipAdapter
import com.foodpartner.app.view.adapter.FoodAdapter
import com.foodpartner.app.view.adapter.FoodItemsearchAdapter
import com.foodpartner.app.view.bottomsheetfragment.OrderdetailBottomsheetFragment
import com.foodpartner.app.view.responsemodel.RestuarantModel
import com.foodpartner.app.viewModel.HomeViewModel
import com.kotlintest.app.utility.interFace.CommonInterface
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale
import java.util.Timer
import kotlin.concurrent.schedule
import kotlin.getValue

class SearchFragment : BaseFragment<SearchfoodfragmentBinding>() {
    lateinit var bottomSheetFragment: OrderdetailBottomsheetFragment
    private var adapter: FoodAdapter? = null
    private val homeViewModel by viewModel<HomeViewModel>()
    var foodlist: ArrayList<FoodItemResponemodelItem> = ArrayList()

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        homeViewModel.response().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            processResponse(it)
        })
        this.mViewDataBinding.apply {
            val map: HashMap<String, String> = HashMap()
            map.put("restaurantCatagoryId", Constant.restaurantcategory)
            map.put("isActive", "true")
            homeViewModel.getcategoryfooditem(map)
            backBtn.setOnClickListener{
                fragmentManagers!!.popBackStackImmediate()
            }
          search.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    filterlist(s.toString())
                }
            })
        }
    }

    override fun getLayoutId(): Int = R.layout.searchfoodfragment
    private fun filterlist(query: String) {
        val filteredList = foodlist.filter {
            it.foodName.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT))
        }

        if (filteredList.isEmpty()) {
            mViewDataBinding.filterrecycler.visibility = View.GONE
            mViewDataBinding.notfoundgroup.visibility = View.VISIBLE
        } else {
            mViewDataBinding.filterrecycler.visibility = View.VISIBLE
            mViewDataBinding.notfoundgroup.visibility = View.GONE
            adapter?.filterList(ArrayList(filteredList))
        }
    }


    private fun processResponse(response: com.foodpartner.app.network.Response) {
        when (response.status) {
            Status.SUCCESS -> {
                when (response.data) {

                    is FoodItemResponemodel -> {
                        this.mViewDataBinding.loader.visibility = View.GONE
                        foodlist.clear()
                        foodlist.addAll(response.data)

                        adapter = FoodAdapter(foodlist,object: CommonInterface {
                            override fun commonCallback(any: Any) {


                            }

                        })

                        this. mViewDataBinding.filterrecycler.adapter=adapter
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