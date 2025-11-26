package com.foodpartner.app.baseClass.BaseActivity

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import com.foodpartner.app.utility.CommonFunction
import com.foodpartner.app.utility.SharedHelper
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.foodpartner.app.utility.Safeclick
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import io.reactivex.disposables.CompositeDisposable
import org.json.JSONException
import org.json.JSONObject
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.net.UnknownHostException

abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {

    protected var baseLiveDataLoading = MutableLiveData<Boolean>()
    lateinit var mViewDataBinding: T
    lateinit var disposable: CompositeDisposable
    lateinit var activity: Activity
    var fragmentActivity: FragmentActivity? = null
    lateinit var fragmentManager: FragmentManager
    @LayoutRes
    protected abstract fun getLayoutId(): Int

    val loadingObservable: MutableLiveData<*> get() = baseLiveDataLoading

    val commonFunction : CommonFunction by inject()
    val sharedHelper : SharedHelper by inject()

    protected abstract fun initView(mViewDataBinding: ViewDataBinding?)


    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        disposable = CompositeDisposable()
        activity = this
        fragmentManager = supportFragmentManager
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        initView(mViewDataBinding)
    }


    fun showKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }

    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun openNewActivity(activity: AppCompatActivity, cls: Class<*>, finishCurrent: Boolean) {
        val intent = Intent(activity, cls)
        startActivity(intent)
        if (finishCurrent) activity.finish()
    }

    protected fun replaceFragment(@IdRes id: Int, fragmentName: Fragment, fragmentTag: String, addToBackStack: Boolean) {
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(id, fragmentName, fragmentTag)
        if (addToBackStack)
            transaction.addToBackStack(fragmentTag)
        transaction.commit()
    }
    fun getErrorBody(throwable: Throwable): String {
        try {
            when (throwable) {
                is HttpException -> {
                    /*if (BuildConfig.DEBUG && throwable.response().errorBody() == null) {
                        error("Assertion failed")
                    }*/
                    val errorBody = throwable.response().errorBody()!!.string()
                    Timber.e("errorBody: %s", errorBody)
                    val jsonObject = JSONObject(errorBody)
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
    protected  fun isGPS(): Boolean? {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun setIntent(cObjection: Class<*>, isFrom: Int) {
        startActivity(Intent(this, cObjection))
        when (isFrom) {
            1 -> {
                //just no need to finish
            }
            2 ->
                //just finishing the single activity
                finish()
            3 ->
                //finishing all previous activity
                finishAffinity()
        }

    }

    fun commonToast(activity: Context?, Message: String?) {
        try {
            Toast.makeText(activity, Message, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun movoToFragment(
        fragmentid: Int,
        fragment: Fragment?,
        bundle: Bundle?,
        isbackStack: Boolean
    ) {
        if (bundle != null) {
            fragment?.arguments = bundle
        }
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        if (isbackStack) {
            fragmentTransaction.addToBackStack(fragment?.tag)
        }
        fragment?.let { fragmentTransaction.add(fragmentid, it) }
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = Safeclick {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }
    private fun isValidEmail(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPhoneNumber(phoneNumber: CharSequence): Boolean {
        val phonePattern = "^[0-9]{10}\$"
        return phoneNumber.matches(phonePattern.toRegex())
    }

    //e.g., minimum length, at least one uppercase letter, one lowercase letter, one digit, and one special character
    private fun isValidPassword(password: CharSequence): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,}\$"
        return password.matches(passwordPattern.toRegex())
    }

    // eg : BAJPC4350M
    private fun isValidPAN(pan: CharSequence): Boolean {
        val panPattern = "[A-Z]{5}[0-9]{4}[A-Z]{1}"
        return pan.matches(panPattern.toRegex())
    }

    //eg: SBIN0018571
    private fun isValidIFSC(ifsc: CharSequence): Boolean {
        val ifscPattern = "[A-Z]{4}[0][A-Z0-9]{6}"
        return ifsc.matches(ifscPattern.toRegex())
    }

    //eg: 1234 5678 9012
    private fun isValidAadhaar(aadhaar: CharSequence): Boolean {
        return aadhaar.length == 12 && aadhaar.matches("[0-9]+".toRegex())
    }

    private fun isValidFssai(fssai: CharSequence): Boolean {
        return fssai.length == 14 && fssai.matches("[0-9]+".toRegex())
    }


}