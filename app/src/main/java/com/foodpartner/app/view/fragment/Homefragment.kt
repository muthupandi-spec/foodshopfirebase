package com.foodpartner.app.view.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import com.app.washeruser.repository.Status
import com.foodpartner.app.R
import com.foodpartner.app.ResponseMOdel.AcceptOrderResponsemodel
import com.foodpartner.app.ResponseMOdel.AssignorderModel
import com.foodpartner.app.ResponseMOdel.CancelledOrderResponsemodel
import com.foodpartner.app.ResponseMOdel.EnablePackingModel
import com.foodpartner.app.ResponseMOdel.OrderRecieveModell
import com.foodpartner.app.ResponseMOdel.OrderRecieveModellItem
import com.foodpartner.app.ResponseMOdel.Orderresponsenmodel
import com.foodpartner.app.ResponseMOdel.RestaurantResponsemodel
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.HomefragmentBinding
import com.foodpartner.app.network.Constant
import com.foodpartner.app.view.adapter.Activeadapter
import com.foodpartner.app.view.bottomsheetfragment.OrderDetailFragment
import com.foodpartner.app.view.bottomsheetfragment.OrderdetailBottomsheetFragment
import com.foodpartner.app.viewModel.HomeViewModel
import com.kotlintest.app.utility.interFace.CommonInterface
import com.mukesh.OtpView
import org.koin.androidx.viewmodel.ext.android.viewModel

class Homefragment : BaseFragment<HomefragmentBinding>() {
    lateinit var bottomSheetFragment: OrderDetailFragment
    private val homeViewModel by viewModel<HomeViewModel>()
    var activelist: ArrayList<OrderRecieveModellItem> = ArrayList()
    lateinit var dialog: Dialog  // class-level declaration

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        adapter()
        homeViewModel.getrestaurant(sharedHelper.getFromUser("userid"))

        println("useriddd" + sharedHelper.getFromUser("userid"))
        homeViewModel.orderrecieve(sharedHelper.getFromUser("userid"), "Order Placed")

        this.mViewDataBinding.apply {

            notification.setOnClickListener {
                loadFragment(Notificationnfragment(), android.R.id.content, "noti", true)
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.homefragment
    fun adapter() {


    }

    private fun processResponse(response: com.foodpartner.app.network.Response) {
        when (response.status) {
            Status.SUCCESS -> {
                this.mViewDataBinding.loader.visibility = View.GONE // Update UI on the main thread

                when (response.data) {

                    is OrderRecieveModell -> {

                        activelist.clear()
                        activelist.addAll(response.data)
                        if (activelist.isEmpty() || activelist == null) {
                            mViewDataBinding.emptyimg.visibility = View.VISIBLE
                            mViewDataBinding.emptytxt.visibility = View.VISIBLE
                            mViewDataBinding.activeRV.visibility = View.GONE
                        } else {
                            mViewDataBinding.emptyimg.visibility = View.GONE
                            mViewDataBinding.emptytxt.visibility = View.GONE
                            mViewDataBinding.activeRV.visibility = View.VISIBLE
                            val activeadapter = Activeadapter(activelist, object : CommonInterface {
                                override fun commonCallback(any: Any) {
                                    if (any.toString().equals("detail")) {
                                        bottomSheetFragment =
                                            OrderDetailFragment(Constant.orderid)
                                        bottomSheetFragment.show(
                                            childFragmentManager,
                                            "BSDialogFragment"
                                        )
                                    } else {
                                        if (any.toString().equals("cancel")) {
                                            mViewDataBinding.loader.visibility= View.VISIBLE
                                            homeViewModel.cancelorder(Constant.orderid)

                                        }
                                        else if (any.toString().equals("Accept Order")) {
                                            mViewDataBinding.loader.visibility= View.VISIBLE

                                            homeViewModel.acceptorder(Constant.orderid)

                                        } else if (any.toString().equals("Enable Packing")) {
                                            mViewDataBinding.loader.visibility= View.VISIBLE
                                            homeViewModel.enablepacking(Constant.orderid)
                                        } else if (any.toString().equals("Searching for a boy")) {
                                            mViewDataBinding.loader.visibility= View.VISIBLE
                                            homeViewModel.assignpartnner(Constant.orderid)
                                        } else if (any.toString().equals("Handover to delivery boy")
                                        ) {

                                        }

                                    }


                                }

                            })

                            this.mViewDataBinding.activeRV.adapter = activeadapter
                        }

                    }

                    is CancelledOrderResponsemodel ->{
                        showToast("cancel order")

                        homeViewModel.orderrecieve(
                            sharedHelper.getFromUser("userid"),
                            "Order Placed"
                        )
                    }

                    is AcceptOrderResponsemodel -> {
                        this.mViewDataBinding.loader.visibility = View.VISIBLE // Update UI on the main thread

                        showToast("accept order")
                        homeViewModel.orderrecieve(
                            sharedHelper.getFromUser("userid"),
                            "Order Placed"
                        )

                    }

                    is EnablePackingModel -> {
                        this.mViewDataBinding.loader.visibility = View.VISIBLE // Update UI on the main thread

                        homeViewModel.orderrecieve(
                            sharedHelper.getFromUser("userid"),
                            "Order Placed"
                        )

                    }

                    is AssignorderModel -> {
                        this.mViewDataBinding.loader.visibility = View.VISIBLE // Update UI on the main thread
//956366
                        homeViewModel.orderrecieve(
                            sharedHelper.getFromUser("userid"),
                            "Order Placed"
                        )

                    }



                    is RestaurantResponsemodel -> {
                        sharedHelper.putInUser("resname", response.data.restaurantName)
                        sharedHelper.putInUser("resemail", response.data.restaurantEMail)
                        sharedHelper.putInUser("resmobno", response.data.mobileNumber)
                        sharedHelper.putInUser("resstreet", response.data.restaurantStreet)
                        sharedHelper.putInUser("rescity", response.data.restaurantCity)
                        sharedHelper.putInUser("reslat", response.data.restaurantLat.toString())
                        sharedHelper.putInUser("reslong", response.data.restaurantLng.toString())
                        sharedHelper.putInUser(
                            "respincode",
                            response.data.restaurantPinCode.toString()
                        )
                        sharedHelper.putInUser(
                            "reslandmark",
                            response.data.restaurantLandMark.toString()
                        )
                        sharedHelper.putInUser("resdesc", response.data.restaurantDescreption)
                        sharedHelper.putInUser("restradeid", response.data.tradeId.toString())
                        sharedHelper.putInUser("restype", response.data.restaurantType.toString())
                    }

                }
            }

            Status.ERROR -> {
                this.mViewDataBinding.loader.visibility = View.GONE // Update UI on the main thread

            }

            Status.LOADING -> {}
            Status.SECONDLOADING -> {}
            Status.DISMISS -> {}
        }
    }


}