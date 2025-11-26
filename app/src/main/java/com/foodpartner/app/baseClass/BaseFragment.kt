package com.foodpartner.app.baseClass

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.foodpartner.app.R
import com.foodpartner.app.utility.CommonFunction
import com.foodpartner.app.utility.SharedHelper
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.foodpartner.app.utility.GlideApp
import com.foodpartner.app.utility.Safeclick
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject

abstract class BaseFragment<T : ViewDataBinding>  : Fragment() {
    lateinit var mViewDataBinding : T
    //    lateinit var activity: Activity
    lateinit var disposable: CompositeDisposable
    var fragmentActivity: FragmentActivity? = null
    var fragmentManagers: FragmentManager? = null
    lateinit var views:View
    lateinit var activitys: Activity
    private lateinit var loader: Dialog

    val commonFunction : CommonFunction by inject()
    val sharedHelper : SharedHelper by inject()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        mViewDataBinding.lifecycleOwner = this
        initView(mViewDataBinding)
        views=mViewDataBinding.root
        return mViewDataBinding.root
    }
    protected abstract fun initView(mViewDataBinding: ViewDataBinding?)
    @LayoutRes
    protected abstract fun getLayoutId(): Int
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disposable = CompositeDisposable()
        activitys = requireActivity()
        fragmentManagers = getActivity()?.supportFragmentManager
    }
    override fun onDestroyView() {
        super.onDestroyView()
        if (disposable.isDisposed) {
            disposable.clear()
        }
    }

    fun glideApp(imagePath: String, Images: ImageView) {
        GlideApp.with(activitys)
            .load(imagePath)
            .into(Images)
    }

    fun showSnackBar(message: String, view: View) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
            .show()
    }

    private fun showNetworkSnackBar(message: String, view: View?) {
        val snackBar: Snackbar?
        snackBar = Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
        snackBar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
        snackBar.setAction("Dismiss", View.OnClickListener {
            snackBar.dismiss()
        })
        snackBar.show()
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
    fun loadFragment(fragment: Fragment, layoutId: Int, tag: String, isBack: Boolean) {
        val frag = fragmentManagers!!.beginTransaction().add(layoutId, fragment, removeFragmentWithTag(tag))
        if (isBack) {
            frag.addToBackStack(tag)
        }
        frag.commitAllowingStateLoss()
    }

    fun loadingFragment(fragment: Fragment, layoutId: Int, tag: String, isBack: Boolean) {
        val frag = fragmentManagers!!.beginTransaction()
            .add(layoutId, fragment, removeFragmentWithTag(tag))
        if (isBack) {
            frag.addToBackStack(null)
        }
        frag.commitAllowingStateLoss()
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
        val fragmentTransaction: FragmentTransaction
        fragmentTransaction = requireFragmentManager().beginTransaction()
        if (isbackStack) {
            fragmentTransaction.addToBackStack(fragment?.tag)
        }
        fragment?.let { fragmentTransaction.add(fragmentid, it) }
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun removeFragmentWithTag(tag: String): String {
        val fragment: Fragment? = fragmentManagers!!.findFragmentByTag(tag)
        if (fragment != null) {
            fragmentManagers!!.beginTransaction()
                .remove(fragment).commitAllowingStateLoss()
        }
        return tag
    }
    fun setIntent(cObjection: Class<*>, isFrom: Int) {
        startActivity(Intent(activitys, cObjection))
        when (isFrom) {
            1 -> {
                //just no need to finish
            }
            2 ->
                //just finishing the single activity
                activitys.finish()
            3 ->
                //finishing all previous activity
                activitys.finishAffinity()
        }
    }

    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = Safeclick {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }
    fun isValidEmail(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

     fun isValidPhoneNumber(phoneNumber: CharSequence): Boolean {
        val phonePattern = "^[0-9]{10}\$"
        return phoneNumber.matches(phonePattern.toRegex())
    }

    //e.g., minimum length, at least one uppercase letter, one lowercase letter, one digit, and one special character
     fun isValidPassword(password: CharSequence): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,}\$"
        return password.matches(passwordPattern.toRegex())
    }

    // eg : BAJPC4350M
     fun isValidPAN(pan: CharSequence): Boolean {
        val panPattern = "[A-Z]{5}[0-9]{4}[A-Z]{1}"
        return pan.matches(panPattern.toRegex())
    }

    //eg: SBIN0018571
     fun isValidIFSC(ifsc: CharSequence): Boolean {
        val ifscPattern = "[A-Z]{4}[0][A-Z0-9]{6}"
        return ifsc.matches(ifscPattern.toRegex())
    }

    //eg: 1234 5678 9012
     fun isValidAadhaar(aadhaar: CharSequence): Boolean {
        return aadhaar.length == 12 && aadhaar.matches("[0-9]+".toRegex())
    }

     fun isValidFssai(fssai: CharSequence): Boolean {
        return fssai.length == 14 && fssai.matches("[0-9]+".toRegex())
    }
    //    val gstin = "22AAAAA0000A1Z5"
    fun isValidGSTIN(gstin: String): Boolean {
        val gstinPattern = Regex("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$")

        return gstin.matches(gstinPattern)
    }

    fun showBase64Image(base64String: String, imageView: ImageView) {
        // Decode Base64 string into a byte array
        val decodedString: ByteArray = Base64.decode(base64String, Base64.DEFAULT)

        // Convert byte array to Bitmap
        val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

        // Set the Bitmap to the ImageView
        imageView.setImageBitmap(bitmap)
    }
    fun showLoader() {
        loader = Dialog(requireContext())
        loader.requestWindowFeature(Window.FEATURE_NO_TITLE)
        loader.setContentView(R.layout.loader_layout) // create a simple loader XML
        loader.setCancelable(false)
        loader.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loader.show()
    }
    fun hideLoader() {
        if (::loader.isInitialized && loader.isShowing) loader.dismiss()
    }
}

