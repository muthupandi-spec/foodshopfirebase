package com.foodpartner.app.view.fragment

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import androidx.databinding.ViewDataBinding
import com.app.washeruser.repository.Status
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentBusinessupdatefragmentBinding
import com.foodpartner.app.databinding.FragmentLoginBinding
import com.foodpartner.app.databinding.FragmentOtpBinding
import com.foodpartner.app.databinding.FragmentProfilefragmentBinding
import com.foodpartner.app.databinding.FragmentProfilepagefrgamentBinding
import com.foodpartner.app.view.responsemodel.UserregisterResponseModel
import com.foodpartner.app.viewModel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Timer
import kotlin.concurrent.schedule

class BusinessUpdateFragment : BaseFragment<FragmentBusinessupdatefragmentBinding>() {
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
            businessname.setText(sharedHelper.getFromUser("businessname"))
            businessaddress.setText(sharedHelper.getFromUser("businessaddress"))
            fssainumber.setText(sharedHelper.getFromUser("fssainumber"))
            pannumber.setText(sharedHelper.getFromUser("pannumber"))
            gstno.setText(sharedHelper.getFromUser("gstno"))
            update.setOnClickListener {
                if(TextUtils.isEmpty(businessname.text.toString())){
                    showToast("Please enter your business name")
                }else if(TextUtils.isEmpty(businessaddress.text.toString())){
                    showToast("Please enter your business address")
                }else if(TextUtils.isEmpty(fssainumber.text.toString())){
                    showToast("Please enter your FSSAI number")
                }else if(!isValidFssai(fssainumber.text.toString())){
                    showToast("Please enter your valid FSSAI number")
                }else if(TextUtils.isEmpty(pannumber.text.toString())){
                    showToast("Please enter your PAN number")
                }else if(!isValidPAN(pannumber.text.toString())){
                    showToast("Please enter your valid PAN number")
                }else if(TextUtils.isEmpty(gstno.text.toString())){
                    showToast("Please enter your GSTIN number")
                }else if(!isValidGSTIN(gstno.text.toString())){
                    showToast("Please enter your valid GSTIN number")
                }else{
                    val hashMap :HashMap<String,String> = HashMap()
                    hashMap["businessname"]=businessname.text.toString()
                    hashMap["businessaddress"]=businessaddress.text.toString()
                    hashMap["fssainumber"]=fssainumber.text.toString()
                    hashMap["pannumber"]=pannumber.text.toString()
                    hashMap["gstno"]=gstno.text.toString()
                    homeViewModel.updatebusines(hashMap)
                    showToast("Business update Sucessfully")
                }
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_businessupdatefragment
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