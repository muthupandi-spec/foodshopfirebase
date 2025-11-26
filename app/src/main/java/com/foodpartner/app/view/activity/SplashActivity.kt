package com.foodpartner.app.view.activity

import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseActivity.BaseActivity
import com.foodpartner.app.databinding.ActivitySplashBinding
import com.foodpartner.app.network.Constant
import com.foodpartner.app.view.fragment.LoginFragment
import com.foodpartner.app.view.fragment.PlaceSearchFragment
import com.foodpartner.app.view.fragment.TrackMapFragment
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit


class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    var tokenn: String=""
    override fun getLayoutId(): Int = R.layout.activity_splash
    override fun initView(mViewDataBinding: ViewDataBinding?) {
        getFCMToken()
        requestNotificationPermission()
        disposable.add(Observable.timer(3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { t ->
                run {
                    Timber.d(t)
                }
            }
            .subscribe { aLong ->
                callIntent()
            })
    }


    protected fun callIntent() {
        if (sharedHelper.getFromUser("token").isNotEmpty()){
            setIntent(HomeActivity::class.java,2)

        }else{

            movoToFragment(android.R.id.content, LoginFragment(),null,false)
//            setIntent(HomeActivity::class.java,2)

        }
//

    }

    fun getFCMToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM Token", "Fetching FCM token failed", task.exception)
                    return@addOnCompleteListener
                }
                // Get new FCM token
                tokenn = task.result
                println("tokenn" + tokenn)
                sharedHelper.putInUser("fcm",tokenn)
                Constant.fcm=tokenn.toString()
                // You can now send this token to your server or save it as needed
            }
    }


    fun requestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }

    }

}
