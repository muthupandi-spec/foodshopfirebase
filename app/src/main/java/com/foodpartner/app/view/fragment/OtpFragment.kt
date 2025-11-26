package com.foodpartner.app.view.fragment

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import androidx.databinding.ViewDataBinding
import com.app.washeruser.repository.Status
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentLoginBinding
import com.foodpartner.app.databinding.FragmentOtpBinding
import com.foodpartner.app.view.activity.HomeActivity
import com.foodpartner.app.view.responsemodel.LoginResponseModel
import com.foodpartner.app.view.responsemodel.OtpResponseModel
import com.foodpartner.app.viewModel.LoginViewModel
import com.mukesh.OtpView
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Timer
import kotlin.concurrent.schedule

class OtpFragment : BaseFragment<FragmentOtpBinding>() {
    private val loginViewModel by viewModel<LoginViewModel>()

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        loginViewModel.response().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            processResponse(it)
        })
        this.mViewDataBinding.apply {
            Timer().schedule(2000) {
                Handler(Looper.getMainLooper()).post {
                    loader.visibility = View.GONE // Update UI on the main thread
                }
            }
nextbtn.setOnClickListener {
    if (TextUtils.isEmpty(otp.text)){
        showToast(getString(R.string.please_enter_your_otp_number))
    }else{
        val map :HashMap<String,String> = HashMap()
        map.put("otp",otp.text.toString())
        loginViewModel.otp(map)
        loadFragment(UserRegistrationFragment(),android.R.id.content,"homepage",true)
    }



}
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_otp

    private fun processResponse(response: com.foodpartner.app.network.Response) {
        when (response.status) {
            Status.SUCCESS -> {
                when (response.data) {

                    is OtpResponseModel ->{

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