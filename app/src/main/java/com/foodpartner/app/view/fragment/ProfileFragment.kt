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
import com.foodpartner.app.databinding.FragmentProfilefragmentBinding
import com.foodpartner.app.databinding.FragmentProfilepagefrgamentBinding
import com.foodpartner.app.view.responsemodel.UserregisterResponseModel
import com.foodpartner.app.viewModel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Timer
import kotlin.concurrent.schedule

class ProfileFragment : BaseFragment<FragmentProfilefragmentBinding>() {
    private val homeViewModel by viewModel<HomeViewModel>()

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        homeViewModel.response().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            processResponse(it)
        })

        this.mViewDataBinding.apply {
            backBtn.setOnClickListener {
                fragmentManagers!!.popBackStackImmediate()
            }
            Timer().schedule(5000) {
                Handler(Looper.getMainLooper()).post {
                    loader.visibility = View.GONE // Update UI on the main thread
                }
            }
            firstname.setText(sharedHelper.getFromUser("firstname"))
            lastname.setText(sharedHelper.getFromUser("lastname"))
            accMobNum.setText(sharedHelper.getFromUser("mobileno"))
            emailedt.setText(sharedHelper.getFromUser("email"))
            aadharno.setText(sharedHelper.getFromUser("aadharno"))
            update.setOnClickListener {
                if(TextUtils.isEmpty(firstname.text.toString())){
                    showToast("Please enter your first name")
                }else if(TextUtils.isEmpty(lastname.text.toString())){
                    showToast("Please enter your last name")
                }else if(TextUtils.isEmpty(accMobNum.text.toString())){
                    showToast("Please enter your Mobile number")
                }else if(!isValidPhoneNumber(accMobNum.text.toString())){
                    showToast("Please enter your valid Mobile number")
                }else if(TextUtils.isEmpty(emailedt.text.toString())){
                    showToast("Please enter your Email address")
                }else if(!isValidEmail(emailedt.text.toString())){
                    showToast("Please enter your valid Email address")
                }else if(TextUtils.isEmpty(aadharno.text.toString())){
                    showToast("Please enter your Aadhaar number")
                }else if(!isValidAadhaar(aadharno.text.toString())){
                    showToast("Please enter your valid Aadhaar number")
                }else{
                    val hashMap :HashMap<String,String> = HashMap()
                    hashMap["firstname"]=firstname.text.toString()
                    hashMap["lastname"]=lastname.text.toString()
                    hashMap["mobileno"]=accMobNum.text.toString()
                    hashMap["email"]=emailedt.text.toString()
                    hashMap["aadharno"]=aadharno.text.toString()
                    homeViewModel.updateprofile(hashMap)
                    showToast("Profile update successfully")
                }
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_profilefragment
    private fun processResponse(response: com.foodpartner.app.network.Response) {
        when (response.status) {
            Status.SUCCESS -> {
                when (response.data) {
                    is UserregisterResponseModel -> {
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
