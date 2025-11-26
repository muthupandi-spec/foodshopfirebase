package com.foodpartner.app.view.fragment

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.app.washeruser.repository.Status
import com.foodpartner.app.R
import com.foodpartner.app.ResponseMOdel.Orderresponsenmodel
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentLoginBinding
import com.foodpartner.app.databinding.FragmentNotificationBinding
import com.foodpartner.app.databinding.FragmentOtpBinding
import com.foodpartner.app.databinding.HomefragmentBinding
import com.foodpartner.app.network.Response
import com.foodpartner.app.view.adapter.Activeadapter
import com.foodpartner.app.view.adapter.CancelledAdapter
import com.foodpartner.app.view.adapter.NotificationAdapter
import com.foodpartner.app.view.bottomsheetfragment.OrderdetailBottomsheetFragment
import com.foodpartner.app.view.responsemodel.Notificationmodel
import com.foodpartner.app.view.responsemodel.RestuarantModel
import com.foodpartner.app.view.responsemodel.UserregisterResponseModel
import com.foodpartner.app.viewModel.HomeViewModel
import com.kotlintest.app.utility.interFace.CommonInterface
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Timer
import kotlin.concurrent.schedule

class Notificationnfragment : BaseFragment<FragmentNotificationBinding>() {
    private val homeViewModel by viewModel<HomeViewModel>()
    var notificationlist: ArrayList<String> = ArrayList()

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        homeViewModel.response().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            processResponse(it)
        })
        homeViewModel.getnotification(sharedHelper.getFromUser("fcm"))
        this.mViewDataBinding.apply {

            backBtn.setOnClickListener {
                fragmentManagers!!.popBackStackImmediate()
            }
        }

    }

    override fun getLayoutId(): Int = R.layout.fragment_notification


    private fun processResponse(response: com.foodpartner.app.network.Response) {
        when (response.status) {
            Status.SUCCESS -> {
                when (response.data) {
                    is Notificationmodel -> {

                        notificationlist.add(response.data.toString())
                        val notificatioadapter = NotificationAdapter(notificationlist, object : CommonInterface {
                            override fun commonCallback(any: Any) {


                            }


                        })

                        this.mViewDataBinding.notificationRC.adapter = notificatioadapter
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