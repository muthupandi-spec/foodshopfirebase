package com.foodpartner.app.view.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.ViewDataBinding
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentSettingfragmentBinding
import com.foodpartner.app.network.Constant
import com.foodpartner.app.view.activity.SplashActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingFragment : BaseFragment<FragmentSettingfragmentBinding>() {

    private val db = FirebaseFirestore.getInstance()

    override fun getLayoutId(): Int = R.layout.fragment_settingfragment

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        this.mViewDataBinding.apply {

            backBtn.setOnClickListener {
                fragmentManagers!!.popBackStackImmediate()
            }

            // Logout
            logoutcontainer.setOnClickListener {
                showConfirmDialog(
                    title = requireContext().getString(R.string.logout),
                    disc = requireContext().getString(R.string.logout),
                    confirmText = "Logout",
                    onConfirm = {
                        sharedHelper.clearUser()
                        sharedHelper.clearCache()
                        FirebaseAuth.getInstance().signOut()
                        Constant.restaurantcategory = ""
                        setIntent(SplashActivity::class.java, 2)
                        showToast("Logout successfully")
                    }
                )
            }

            // Delete Account
            deletaccountcontainer.setOnClickListener {
                showConfirmDialog(
                    title = "Delete Account",
                    disc = "Are you sure? This action cannot be undone.",
                    confirmText = "Delete",
                    onConfirm = {
                        deleteAccount()
                    }
                )
            }
        }
    }

    private fun showConfirmDialog(
        title: String,
        disc: String,
        confirmText: String,
        onConfirm: () -> Unit
    ) {
        val dialog = BottomSheetDialog(requireContext())
        val btmView = layoutInflater.inflate(R.layout.logout, null)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val cancelBtn = btmView.findViewById<AppCompatButton>(R.id.logCancelBtn)
        val confirmBtn = btmView.findViewById<AppCompatButton>(R.id.logOutBtn)
        val header = btmView.findViewById<TextView>(R.id.logTxtView1)
        val discTxt = btmView.findViewById<AppCompatTextView>(R.id.logTxtView2)

        header.text = title
        discTxt.text = disc
        confirmBtn.text = confirmText

        confirmBtn.setOnClickListener {
            dialog.dismiss()
            onConfirm()
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(btmView)
        dialog.show()
    }

    private fun deleteAccount() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val userId = user.uid

        // Firestore-ல document delete
        db.collection("shops").document(userId)
            .delete()
            .addOnSuccessListener {
                // Firebase Auth delete
                user.delete()
                    .addOnSuccessListener {
                        sharedHelper.clearUser()
                        sharedHelper.clearCache()
                        Constant.restaurantcategory = ""
                        showToast("Account deleted successfully")
                        setIntent(SplashActivity::class.java, 2)
                    }
                    .addOnFailureListener {
                        showToast("Failed to delete account: ${it.message}")
                    }
            }
            .addOnFailureListener {
                showToast("Failed to delete data: ${it.message}")
            }
    }
}