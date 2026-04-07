package com.foodpartner.app.view.fragment

import android.text.InputType
import android.util.Log
import androidx.databinding.ViewDataBinding
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentLoginBinding
import com.foodpartner.app.view.activity.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private val auth = FirebaseAuth.getInstance()
    private var isPasswordVisible = false

    override fun initView(mViewDataBinding: ViewDataBinding?) {

        val binding = this.mViewDataBinding!!

        // 👁️ PASSWORD TOGGLE
        binding.eyeBtn.setOnClickListener {

            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                binding.password.inputType =
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.eyeBtn.setImageResource(R.drawable.ic_eye)
            } else {
                binding.password.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.eyeBtn.setImageResource(R.drawable.ic_eye_hide)
            }

            binding.password.setSelection(binding.password.text.length)
        }

        // 🔐 LOGIN
        binding.logInBtn.setOnClickListener {

            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            when {
                email.isEmpty() ->
                    showToast("Please enter your email")

                password.isEmpty() ->
                    showToast("Please enter your password")

                else ->
                    signInUser(email, password)
            }
        }

        // 🔁 FORGOT PASSWORD
        binding.forgotPassword.setOnClickListener {

            val email = binding.email.text.toString().trim()

            if (email.isEmpty()) {
                showToast("Please enter your email first")
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Enter valid email address")
                return@setOnClickListener
            }

            sendResetPasswordEmail(email)
        }

        // 🆕 SIGN UP
        binding.signUpBtn.setOnClickListener {
            loadFragment(
                ShopCreateFragment(),
                android.R.id.content,
                "register",
                true
            )
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_login

    // ------------------------------------------------------------------
    // LOGIN + FIRESTORE CHECK
    // ------------------------------------------------------------------
    private fun signInUser(email: String, password: String) {

        showLoader()

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                if (!isAdded) return@addOnSuccessListener

                val uid = auth.currentUser?.uid ?: run {
                    hideLoader()
                    showToast("User not found")
                    return@addOnSuccessListener
                }

                FirebaseFirestore.getInstance()
                    .collection("shops")
                    .document(uid)
                    .get()
                    .addOnSuccessListener { document ->

                        hideLoader()

                        if (document.exists()) {

                            sharedHelper.putInUser("userid", uid)
                            saveFcmToken(uid)

                            showToast("Login successful")
                            setIntent(HomeActivity::class.java, 2)

                        } else {

                            FirebaseAuth.getInstance().signOut()
                            showToast("Account not registered. Please sign up first.")
                        }
                    }
                    .addOnFailureListener {

                        hideLoader()
                        showToast("Something went wrong. Try again.")
                    }
            }
            .addOnFailureListener {

                if (!isAdded) return@addOnFailureListener

                hideLoader()
                showToast("Login failed: ${it.message}")
            }
    }

    // ------------------------------------------------------------------
    // FORGOT PASSWORD
    // ------------------------------------------------------------------
    private fun sendResetPasswordEmail(email: String) {

        showLoader()

        FirebaseAuth.getInstance()
            .sendPasswordResetEmail(email)
            .addOnSuccessListener {

                hideLoader()
showResetDialog()            }
            .addOnFailureListener {

                hideLoader()
                showToast(it.message ?: "Failed to send reset email")
            }
    }

    // ------------------------------------------------------------------
    // SAVE FCM TOKEN
    // ------------------------------------------------------------------
    private fun saveFcmToken(uid: String) {

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->

                val data = hashMapOf(
                    "fcmToken" to token
                )

                FirebaseFirestore.getInstance()
                    .collection("shops")
                    .document(uid)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d("FCM", "FCM token saved")
                    }
                    .addOnFailureListener { e ->
                        Log.e("FCM", "FCM save failed", e)
                    }
            }
    }
    private fun showResetDialog() {

        androidx.appcompat.app.AlertDialog.Builder(activitys)
            .setTitle("Password Reset")
            .setMessage(
                "Reset link has been sent to your email.\n\n" +
                        "Please check Inbox or Spam folder."
            )
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
