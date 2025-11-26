package com.foodpartner.app.network

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.foodpartner.app.utility.SharedHelper
import com.foodpartner.app.utility.rx.SchedulersFacade
import com.foodpartner.app.view.requestmodel.CreateCategoryRequest
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.disposables.CompositeDisposable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.net.UnknownHostException

class  CommonApi constructor(
    var application: Application,
    val sharedHelper: SharedHelper,
    val api: ApiInterface,
    val schedulersFacade: SchedulersFacade
) {

    fun getuser(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: HashMap<String,String>

    ) {
        disable.add(api.getuser(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun orderdetail(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: HashMap<String,String>

    ) {
        disable.add(api.orderdetail(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun orderrecieve(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: String,
        type: String

    ) {
        disable.add(api.ordererrecieve(model,type)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun getallcategory(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: String

    ) {
        disable.add(api.getallcategory(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun getcategoryfooditem(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        map: HashMap<String, String>

    ) {
        disable.add(api.getcategoryfooditem(map)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun getrestaurant(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: String

    ) {


        disable.add(api.getrestaurant(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun acceptorder(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: String

    ) {
        disable.add(api.acceptorder(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun enablepacking(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: String

    ) {
        disable.add(api.enablepacking(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun assignpartnner(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: String

    ) {
        disable.add(api.assignpartner(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }


    fun createcategory(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: CreateCategoryRequest

    ) {
        disable.add(api.createcategory(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun cancelorder(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: String

    ) {
        disable.add(api.cancelorder(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun statuschange(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: HashMap<String,String>

    ) {
        disable.add(api.statuschange(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }

    fun trackorder(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: String

    ) {
        disable.add(api.trackorder(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun updateshop(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: HashMap<String,String>

    ) {
        disable.add(api.updateshop(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun updatecategory(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        id: String,
        model: HashMap<String,String>

    ) {
        disable.add(api.updatecategory(id,model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun addfooditem(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        avatar: MultipartBody.Part,
        model:RequestBody
    ) {
        disable.add(api.addfooditem(avatar,model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun updatefooditem(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        id: String,
        avatar: MultipartBody.Part,
        model:RequestBody
    ) {
        disable.add(api.updatefood(id,avatar,model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun completedorders(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        id: String,
        type: String

    ) {
        disable.add(api.completedorders(id,type)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun vieworders(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        id: String,

    ) {
        disable.add(api.vieworder(id)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun getfooditem(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: HashMap<String,String>

    ) {
        disable.add(api.getfooditem(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }

    fun cancelledorders(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        id: String,
        type: String

    ) {
        disable.add(api.cancelledorders(id,type)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun updateprofile(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: HashMap<String,String>

    ) {
        disable.add(api.updateprofile(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun updatebusines(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: HashMap<String,String>

    ) {
        disable.add(api.updatebusines(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun updatebank(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: HashMap<String,String>

    ) {
        disable.add(api.updatebank(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun getnotification(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: String

    ) {
        disable.add(api.getnotification(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun updateshopp(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: HashMap<String,String>

    ) {
        disable.add(api.updateshopp(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun deleteaccount(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: HashMap<String,String>

    ) {
        disable.add(api.deleteaccount(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun logout(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: HashMap<String,String>

    ) {
        disable.add(api.logout(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }

    fun login(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: HashMap<String,String>

    ) {
        disable.add(api.login(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun otp(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: HashMap<String,String>

    ) {
        disable.add(api.otp(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }

    fun register(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: HashMap<String,String>

    ) {
        disable.add(api.register(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun shopcreate(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        model: HashMap<String,String>

    ) {
        disable.add(api.shopcreate(model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }
    fun updateres(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        id: String,
        model: HashMap<String,String>

    ) {
        disable.add(api.updaterestaurat(id,model)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }




    fun otp(
        response: MutableLiveData<Response>,
        disable: CompositeDisposable,
        mobileno: String,
        otp: String

    ) {
        disable.add(api.verifyOtp(mobileno,otp)
            .doOnSubscribe{ response.postValue(Response.loading()) }
            .compose(schedulersFacade.applyAsync())
            .doFinally { response.value = Response.dismiss() }
            .subscribe({
                response.value = Response.success(it)
            }, {
                response.value = Response.error(it)
                response.value = Response.dismiss()
                onError(it)
            })
        )
    }


    protected fun onError(throwable: Throwable) {
        Toast.makeText(application, getErrorBody(throwable), Toast.LENGTH_LONG).show()
        Timber.i("errorss===>%s", getErrorBody(throwable))
        println("eroorror"+getErrorBody(throwable))
        if(getErrorBody(throwable).equals("access_denied")){
            sharedHelper.clearUser()
        }
    }
    fun getErrorBody(throwable: Throwable): String {
        try {
            when (throwable) {
                is HttpException -> {
                  /*  if (BuildConfig.DEBUG && throwable.response().errorBody() == null) {
                        error("Assertion failed")
                    }*/
                    val errorBody = throwable.response().errorBody()!!.string()
                    val jsonObject = JSONObject(errorBody)
                    jsonObject.optString("message")
                    Timber.e("errorBody: %s", errorBody)
                    println("errorBody"+errorBody)
                    return if (jsonObject.has("result")) {
                        jsonObject.optString("result")
                    } else if (jsonObject.has("message")) {
                        jsonObject.optString("message")
                    } else {
                        "Something went wrong!!"
                    }
                }
                is UnknownHostException -> {
                    return "No internet connection"
                }
                else -> return if (throwable.message == null) {
                    "Something went Wrong!"
                } else {
                    throwable.message.toString()
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            return "Something went wrong"
        } catch (e: Exception) {
            e.printStackTrace()
            return "Something went wrong"
        }
    }


}