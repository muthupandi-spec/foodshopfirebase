package com.foodpartner.app.viewModel

import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.foodpartner.app.baseClass.BaseViewModel
import com.foodpartner.app.network.CommonApi
import com.foodpartner.app.network.Response

class LoginViewModel(var commonApi: CommonApi)  : BaseViewModel(){
    var previousFragment = MutableLiveData(Fragment())


    fun response(): MutableLiveData<Response> {
        return response
    }
    fun login(parammodel:HashMap<String,String>){
        commonApi.login(response,disable,parammodel)
    }
    fun otp(parammodel:HashMap<String,String>){
        commonApi.otp(response,disable,parammodel)
    }
    fun register(parammodel:HashMap<String,String>){
        commonApi.register(response,disable,parammodel)
    }
    fun shopcreate(parammodel:HashMap<String,String>){
        commonApi.shopcreate(response,disable,parammodel)
    }
    fun otp(mobileno:String,otp: String){
        commonApi.otp(response,disable,mobileno,otp)
    }


}