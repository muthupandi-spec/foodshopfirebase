package com.foodpartner.app.view.activity

import androidx.databinding.ViewDataBinding
import com.app.washeruser.repository.Status
import com.foodpartner.app.R
import com.foodpartner.app.ResponseMOdel.HomeMOdel
import com.foodpartner.app.appControl.AppController
import com.foodpartner.app.baseClass.BaseActivity.BaseActivity
import com.foodpartner.app.databinding.ActivityMainBinding
import com.foodpartner.app.network.Constant
import com.foodpartner.app.viewModel.HomeViewModel
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val homeViewModel by viewModel<HomeViewModel>()
    private var baseApplication : AppController?=null
    override fun getLayoutId():Int=R.layout.activity_main

    override fun initView(mViewDataBinding: ViewDataBinding?) {

        baseApplication = activity?.applicationContext as AppController

        homeViewModel.response().observe(this, androidx.lifecycle.Observer {
            processResponse(it)
        })
        this.mViewDataBinding.apply {
            ttext.text= Constant.fcm
            ttext.setSafeOnClickListener {
                println("namrrr")
            }
            text.setSafeOnClickListener {
                println("najhfiuayfi")
            }
        }

        this.mViewDataBinding.ttext.setSafeOnClickListener {
            println("namrrr")
        }
        val emitServiceLocation = JSONObject()
//        emitServiceLocation.put("latitude", currentLocation.latitude.toString())
//        emitServiceLocation.put("longitude", currentLocation.longitude.toString())
//        emitServiceLocation.put("user_id", user_id)
        baseApplication?.emitcurrentlocation("currentLocation",emitServiceLocation)


    }


    private fun processResponse(response: com.foodpartner.app.network.Response) {
        when (response.status) {
            Status.SUCCESS -> {
                when (response.data) {
                    is HomeMOdel -> {
                    }
                }
            }

            Status.ERROR -> {
            }

            Status.LOADING -> TODO()
            Status.SECONDLOADING -> TODO()
            Status.DISMISS -> TODO()
        }
    }



}
