package com.foodpartner.app.view.fragment

import android.view.View
import androidx.databinding.ViewDataBinding
import com.app.washeruser.repository.Status
import com.foodpartner.app.R
import com.foodpartner.app.ResponseMOdel.CancelOrderResponseModel
import com.foodpartner.app.ResponseMOdel.Orderresponsenmodel
import com.foodpartner.app.ResponseMOdel.canncelleteddOrderResponModelItem
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentCancelledBinding
import com.foodpartner.app.view.adapter.CancelledAdapter
import com.foodpartner.app.view.bottomsheetfragment.OrderDetailFragment
import com.foodpartner.app.viewModel.HomeViewModel
import com.kotlintest.app.utility.interFace.CommonInterface
import org.koin.androidx.viewmodel.ext.android.viewModel

class CancelOrderFragment : BaseFragment<FragmentCancelledBinding>() {
    lateinit var bottomSheetFragment: OrderDetailFragment
    var itemlist: ArrayList<Orderresponsenmodel.AOrder.OrderItem> = ArrayList()
    private val homeViewModel by viewModel<HomeViewModel>()
    var cancelorderlist:ArrayList<canncelleteddOrderResponModelItem> = ArrayList()

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        this.mViewDataBinding.apply {
           loader.visibility=View.VISIBLE

            swipeRefresh.setOnRefreshListener {

                homeViewModel.cancelledorders(sharedHelper.getFromUser("userid"),"cancelled")

            }

            homeViewModel.cancelledorders(sharedHelper.getFromUser("userid"),"cancelled")

            homeViewModel.response().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                processResponse(it)
            })
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_cancelled
    private fun processResponse(response: com.foodpartner.app.network.Response) {
        when (response.status) {
            Status.SUCCESS -> {
                this.mViewDataBinding.swipeRefresh.isRefreshing=false

                this.mViewDataBinding.loader.visibility=View.GONE
                when (response.data) {
                    is CancelOrderResponseModel -> {
                        cancelorderlist.clear()
                        println("cancelled")

                        cancelorderlist.addAll(response.data)
                        println("data"+cancelorderlist)
                        if(cancelorderlist.isNotEmpty()){
                            this.mViewDataBinding.cartEmpty.visibility=View.GONE

                            val cancelorderadapter = CancelledAdapter(cancelorderlist, object : CommonInterface {
                                override fun commonCallback(any: Any) {

                                    when (any) {
                                        is HashMap<*, *> -> {



                                            bottomSheetFragment = OrderDetailFragment(
                                                any["orderId"].toString()
                                            )
                                            bottomSheetFragment.show(
                                                childFragmentManager,
                                                "BSDialogFragment"
                                            )



                                        }
                                    }

                                }


                            })

                            this.mViewDataBinding.coompleteRV.adapter = cancelorderadapter
                        }else{
                            this.mViewDataBinding.cartEmpty.visibility=View.VISIBLE

                        }
                    }

                }

                }

            Status.LOADING -> {}
            Status.SECONDLOADING -> {}
            Status.DISMISS -> {}
            Status.ERROR -> {
                this.mViewDataBinding.loader.visibility=View.GONE

            }
        }


        }
    }

