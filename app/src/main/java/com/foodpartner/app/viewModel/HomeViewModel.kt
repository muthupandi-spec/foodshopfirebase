package com.foodpartner.app.viewModel

import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.airbnb.lottie.model.content.ShapeStroke
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseViewModel
import com.foodpartner.app.network.CommonApi
import com.foodpartner.app.network.Response
import com.foodpartner.app.view.fragment.HistoryFragment
import com.foodpartner.app.view.fragment.Homefragment
import com.foodpartner.app.view.fragment.ProfilepageFragment
import com.foodpartner.app.view.fragment.Shopfragment
import com.foodpartner.app.view.requestmodel.CreateCategoryRequest
import com.google.android.material.navigation.NavigationView
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart

class HomeViewModel(var commonApi: CommonApi)  : BaseViewModel(), NavigationView.OnNavigationItemSelectedListener {
    var fragmentList = MutableLiveData(ArrayList<Fragment>())
    var previousFragment = MutableLiveData(Fragment())

    init {
        fragmentList.value!!.add(Homefragment())
        fragmentList.value!!.add(Shopfragment())
        fragmentList.value!!.add(HistoryFragment())
        fragmentList.value!!.add(ProfilepageFragment())
    }
    fun response(): MutableLiveData<Response> {
        return response
    }
    fun getuser(parammodel:HashMap<String,String>){
        commonApi.getuser(response,disable,parammodel)
    }
    fun orderdetail(parammodel:HashMap<String,String>){
        commonApi.orderdetail(response,disable,parammodel)
    }
    fun acceptorder(parammodel:String){
        commonApi.acceptorder(response,disable,parammodel)
    }
    fun enablepacking(parammodel:String){
        commonApi.enablepacking(response,disable,parammodel)
    }
    fun assignpartnner(parammodel:String){
        commonApi.assignpartnner(response,disable,parammodel)
    }


    fun createcategory(parammodel: CreateCategoryRequest){
        commonApi.createcategory(response,disable,parammodel)
    }
    fun cancelorder(parammodel:String){
        commonApi.cancelorder(response,disable,parammodel)
    }
    fun statuschange(parammodel:HashMap<String,String>){
        commonApi.statuschange(response,disable,parammodel)
    }
    fun updateshop(parammodel:HashMap<String,String>){
        commonApi.updateshop(response,disable,parammodel)
    }
    fun updatecategory(id: String, parammodel:HashMap<String,String>){
        commonApi.updatecategory(response,disable,id,parammodel)
    }
    fun cancelledorders( id: String,
                         type: String){
        commonApi.cancelledorders(response,disable,id,type)
    }
    fun updateprofile(parammodel:HashMap<String,String>){
        commonApi.updateprofile(response,disable,parammodel)
    }
    fun updatebank(parammodel:HashMap<String,String>){
        commonApi.updatebank(response,disable,parammodel)
    }
    fun updatebusines(parammodel:HashMap<String,String>){
        commonApi.updatebusines(response,disable,parammodel)
    }
    fun getnotification(parammodel: String){
        commonApi.getnotification(response,disable,parammodel)
    }
    fun updateshopp(parammodel:HashMap<String,String>){
        commonApi.updateshopp(response,disable,parammodel)
    }
    fun deleteaccount(parammodel:HashMap<String,String>){
        commonApi.deleteaccount(response,disable,parammodel)
    }
    fun logout(parammodel:HashMap<String,String>){
        commonApi.logout(response,disable,parammodel)
    }
    fun completedorders( id: String,
                         type: String){
        commonApi.completedorders(response,disable,id,type)
    }
    fun vieworders( id: String,
                         ){
        commonApi.vieworders(response,disable,id)
    }
    fun getfooditem(parammodel:HashMap<String,String>){
        commonApi.getfooditem(response,disable,parammodel)
    }

    fun addfooditem(avatar: MultipartBody.Part, parammodel:RequestBody){
        commonApi.addfooditem(response,disable,avatar,parammodel)
    }
    fun updatefooditem(id: String, avatar: MultipartBody.Part, parammodel:RequestBody){
        commonApi.updatefooditem(response,disable,id,avatar,parammodel)
    }
    fun updateres(id: String,parammodel:HashMap<String,String>){
        commonApi.updateres(response,disable,id,parammodel)
    }
    fun trackorder(parammodel: String){
        commonApi.trackorder(response,disable,parammodel)
    }
    fun orderrecieve(parammodel:String,type: String){
        commonApi.orderrecieve(response,disable,parammodel,type)
    }
    fun getallcategory(parammodel:String){
        commonApi.getallcategory(response,disable,parammodel)
    }
    fun getcategoryfooditem(map: HashMap<String, String>){
        commonApi.getcategoryfooditem(response,disable,map)
    }
    fun getrestaurant(parammodel:String){
        commonApi.getrestaurant(response,disable,parammodel)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navHomeBtn -> {
                response.value = Response.success(fragmentList.value!![0])
            }
            R.id.shopbtn -> {
                response.value = Response.success(fragmentList.value!![1])
            }
            R.id.navhistoryBtn -> {
                response.value = Response.success(fragmentList.value!![2])
            }
            R.id.navProfBtn -> {
                response.value = Response.success(fragmentList.value!![3])
            }

        }
        return true
    }

}