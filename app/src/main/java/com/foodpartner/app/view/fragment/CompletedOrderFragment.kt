package com.foodpartner.app.view.fragment

import android.view.View
import androidx.databinding.ViewDataBinding
import com.app.washeruser.repository.Status
import com.foodpartner.app.R
import com.foodpartner.app.ResponseMOdel.CompletedOrderResponseModel
import com.foodpartner.app.ResponseMOdel.CompleteddOrderResponModelItem
import com.foodpartner.app.ResponseMOdel.Orderresponsenmodel
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentCompletedBinding
import com.foodpartner.app.view.adapter.CompletedAdapter
import com.foodpartner.app.view.bottomsheetfragment.OrderDetailFragment
import com.foodpartner.app.viewModel.HomeViewModel
import com.kotlintest.app.utility.interFace.CommonInterface
import org.koin.androidx.viewmodel.ext.android.viewModel

class CompletedOrderFragment : BaseFragment<FragmentCompletedBinding>() {
    lateinit var bottomSheetFragment: OrderDetailFragment
    var itemlist: ArrayList<Orderresponsenmodel.AOrder.OrderItem> = ArrayList()
    private val homeViewModel by viewModel<HomeViewModel>()
    var completeorderlist:ArrayList<CompleteddOrderResponModelItem> = ArrayList()

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        this.mViewDataBinding.apply {

            swipeRefresh.setOnRefreshListener {
                homeViewModel.completedorders(sharedHelper.getFromUser("userid"),"Completed")
            }

            homeViewModel.completedorders(sharedHelper.getFromUser("userid"),"Completed")
            homeViewModel.response().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                processResponse(it)
            })
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_completed
    private fun processResponse(response: com.foodpartner.app.network.Response) {
        when (response.status) {
            Status.SUCCESS -> {
                this.mViewDataBinding.swipeRefresh.isRefreshing=false

                this.mViewDataBinding.loader.visibility=View.GONE
                when (response.data)  {
                    is CompletedOrderResponseModel -> {
                        println("completepage")
                        completeorderlist.clear()
                        completeorderlist.addAll(response.data)
                        if(completeorderlist.isNotEmpty()){
                            this.mViewDataBinding.cartEmpty.visibility=View.GONE
                            val cancelorderadapter = CompletedAdapter(completeorderlist, object : CommonInterface {
                                override fun commonCallback(any: Any) {

                                    when (any) {
                                        is HashMap<*, *> -> {
                                            // Replace with real class
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