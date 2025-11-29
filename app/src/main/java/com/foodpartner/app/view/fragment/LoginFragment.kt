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

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private val auth = FirebaseAuth.getInstance()

    override fun initView(mViewDataBinding: ViewDataBinding?) {

        val binding = this.mViewDataBinding!!   // FIXED

        binding.signUpBtn.setOnClickListener {
            loadFragment(
                ShopCreateFragment(),
                android.R.id.content,
                "register",
                true
            )
        }

        binding.logInBtn.setOnClickListener {

            val emailStr = binding.email.text.toString().trim()
            val passStr = binding.password.text.toString().trim()

            when {
                emailStr.isEmpty() -> showToast("Please enter your Email")
                passStr.isEmpty() -> showToast("Please enter your Password")
                else -> signInUser(emailStr, passStr)
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_login

    private fun signInUser(email: String, password: String) {

        showLoader()   // Show loader

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                if (!isAdded) return@addOnSuccessListener   // FIXED crash issue

                hideLoader()
                showToast("Login Successful")

                val uid = auth.currentUser?.uid ?: ""

                sharedHelper.putInUser("userid", uid)

                setIntent(HomeActivity::class.java, 2)
            }
            .addOnFailureListener {

                if (!isAdded) return@addOnFailureListener   // FIXED

                hideLoader()
                showToast("Login failed: ${it.message}")
            }
    }
}
