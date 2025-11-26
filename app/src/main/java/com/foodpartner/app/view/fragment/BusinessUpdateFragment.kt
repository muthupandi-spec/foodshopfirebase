package com.foodpartner.app.view.fragment

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import androidx.databinding.ViewDataBinding
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentBusinessupdatefragmentBinding
import com.foodpartner.app.view.responsemodel.UserregisterResponseModel
import com.foodpartner.app.viewModel.HomeViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Timer
import kotlin.concurrent.schedule

class BusinessUpdateFragment : BaseFragment<FragmentBusinessupdatefragmentBinding>() {

    private val firestore = FirebaseFirestore.getInstance()

    override fun getLayoutId(): Int = R.layout.fragment_businessupdatefragment

    override fun initView(mViewDataBinding: ViewDataBinding?) {
            // Observe ViewModel

            // Back button
            this.mViewDataBinding.backBtn.setOnClickListener { fragmentManagers?.popBackStackImmediate() }

          

            // Load saved values
            this.mViewDataBinding.businessname.setText(sharedHelper.getFromUser("businessname"))
            this.mViewDataBinding.businessaddress.setText(sharedHelper.getFromUser("businessaddress"))
            this.mViewDataBinding.fssainumber.setText(sharedHelper.getFromUser("fssainumber"))
            this.mViewDataBinding.pannumber.setText(sharedHelper.getFromUser("pannumber"))
            this.mViewDataBinding.gstno.setText(sharedHelper.getFromUser("gstno"))

            // Update button
            this.mViewDataBinding.update.setOnClickListener {
                val businessName = this.mViewDataBinding.businessname.text.toString().trim()
                val businessAddress = this.mViewDataBinding.businessaddress.text.toString().trim()
                val fssaiNumber = this.mViewDataBinding.fssainumber.text.toString().trim()
                val panNumber = this.mViewDataBinding.pannumber.text.toString().trim()
                val gstNumber = this.mViewDataBinding.gstno.text.toString().trim()

                when {
                    TextUtils.isEmpty(businessName) -> showToast("Please enter your business name")
                    TextUtils.isEmpty(businessAddress) -> showToast("Please enter your business address")
                    TextUtils.isEmpty(fssaiNumber) -> showToast("Please enter your FSSAI number")
                    !isValidFssai(fssaiNumber) -> showToast("Please enter a valid FSSAI number")
                    TextUtils.isEmpty(panNumber) -> showToast("Please enter your PAN number")
                    !isValidPAN(panNumber) -> showToast("Please enter a valid PAN number")
                    TextUtils.isEmpty(gstNumber) -> showToast("Please enter your GSTIN number")
                    !isValidGSTIN(gstNumber) -> showToast("Please enter a valid GSTIN number")
                    else -> {
                        val businessData = hashMapOf(
                            "businessname" to businessName,
                            "businessaddress" to businessAddress,
                            "fssainumber" to fssaiNumber,
                            "pannumber" to panNumber,
                            "gstno" to gstNumber
                        )
                        saveBusinessDetailsToFirestore(businessData)
                    }
                }
            
        }
    }

    private fun saveBusinessDetailsToFirestore(businessData: HashMap<String, String>) {
        val uid = sharedHelper.getFromUser("userid") ?: run {
            showToast("User not logged in")
            return
        }

        showLoader()

        firestore.collection("shops")
            .document(uid)
            .update(businessData as Map<String, Any>)
            .addOnSuccessListener {
                hideLoader()
                showToast("Business details updated successfully")
                saveLocally(businessData)
            }
            .addOnFailureListener {
                // If document doesn't exist, create it
                firestore.collection("shops")
                    .document(uid)
                    .set(businessData)
                    .addOnSuccessListener {
                        hideLoader()
                        showToast("Business details updated successfully")
                        saveLocally(businessData)
                    }
                    .addOnFailureListener { ex ->
                        hideLoader()
                        showToast("Failed to update business details: ${ex.message}")
                    }
            }
    }

    private fun saveLocally(data: HashMap<String, String>) {
        sharedHelper.putInUser("businessname", data["businessname"].orEmpty())
        sharedHelper.putInUser("businessaddress", data["businessaddress"].orEmpty())
        sharedHelper.putInUser("fssainumber", data["fssainumber"].orEmpty())
        sharedHelper.putInUser("pannumber", data["pannumber"].orEmpty())
        sharedHelper.putInUser("gstno", data["gstno"].orEmpty())
    }
 

}
