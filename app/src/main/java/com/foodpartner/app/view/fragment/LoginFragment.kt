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
import com.foodpartner.app.view.activity.HomeActivity
import com.foodpartner.app.view.responsemodel.LoginResponseModel
import com.foodpartner.app.view.responsemodel.OtpResponseModel
import com.foodpartner.app.viewModel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Timer
import kotlin.concurrent.schedule
import kotlin.toString

class LoginFragment: BaseFragment<FragmentLoginBinding>(){
    private val auth = FirebaseAuth.getInstance()

    override fun initView(mViewDataBinding: ViewDataBinding?) {

this.mViewDataBinding.apply {

    signUpBtn.setOnClickListener {
        loadFragment(ShopCreateFragment(),android.R.id.content, "register",true)
    }
    logInBtn.setOnClickListener {
        val emailStr = email.text.toString()
        val passStr = password.text.toString()

        if (TextUtils.isEmpty(emailStr)) {
            showToast("Please enter your Email")
        } else if (TextUtils.isEmpty(passStr)) {
            showToast("Please enter your Password")
        } else {
            signInUser(emailStr, passStr)
        }
    }
}
    }
    override fun getLayoutId(): Int = R.layout.fragment_login


    private fun signInUser(email: String, password: String) {

        showLoader()

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                hideLoader()
                showToast("Login Successful")
                val uid = FirebaseAuth.getInstance().currentUser?.uid

                sharedHelper.putInUser("userid",uid.toString())

                // Move to Home after login
                setIntent(HomeActivity::class.java,2)


            }
            .addOnFailureListener {
                hideLoader()
                showToast("Login failed: ${it.message}")
            }
    }
}