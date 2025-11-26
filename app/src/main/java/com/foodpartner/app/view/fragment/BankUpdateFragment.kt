package com.foodpartner.app.view.fragment

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.ViewDataBinding
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentBankdetaileditfragmentBinding
import com.foodpartner.app.network.Constant
import com.foodpartner.app.network.Response
import com.foodpartner.app.view.responsemodel.UserregisterResponseModel
import com.foodpartner.app.viewModel.HomeViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.ext.android.viewModel

class BankUpdateFragment : BaseFragment<FragmentBankdetaileditfragmentBinding>() {

    private val homeViewModel by viewModel<HomeViewModel>()
    private val firestore = FirebaseFirestore.getInstance()
    private var bankNameValue: String = "State Bank of India"

    override fun getLayoutId(): Int = R.layout.fragment_bankdetaileditfragment

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        
            // Observe ViewModel Response

            // Load user bank data
            this.mViewDataBinding.accname.setText(sharedHelper.getFromUser("accname"))
            this.mViewDataBinding.accnumber.setText(sharedHelper.getFromUser("accnumber"))
            this.mViewDataBinding.ifsccode.setText(sharedHelper.getFromUser("ifsccode"))

            // Setup bank spinner
            val banks = arrayOf("State Bank of India", "ICICI Bank", "Kotak Mahindra Bank", "Indane Bank")
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, banks)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            this.mViewDataBinding.bankname.adapter = adapter
            this.mViewDataBinding.bankname.setSelection(0)
            this.mViewDataBinding.bankname.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    bankNameValue = parent.getItemAtPosition(position).toString()
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            // Back button
            this.mViewDataBinding.backBtn.setOnClickListener { fragmentManagers?.popBackStackImmediate() }

            // Update button
            this.mViewDataBinding.update.setOnClickListener {
                val accName = this.mViewDataBinding.accname.text.toString().trim()
                val accNumber = this.mViewDataBinding.accnumber.text.toString().trim()
                val ifscCode = this.mViewDataBinding.ifsccode.text.toString().trim()

                when {
                    TextUtils.isEmpty(accName) -> showToast("Please enter your account name")
                    TextUtils.isEmpty(accNumber) -> showToast("Please enter your account number")
                    TextUtils.isEmpty(ifscCode) -> showToast("Please enter your IFSC code")
                    !isValidIFSC(ifscCode) -> showToast("Please enter a valid IFSC code")
                    else -> {
                        val bankData = hashMapOf(
                            "accname" to accName,
                            "accnumber" to accNumber,
                            "ifsccode" to ifscCode,
                            "bankname" to bankNameValue
                        )
                        saveBankDetailsToFirestore(bankData)
                    }
                }
            }
        
    }

    private fun saveBankDetailsToFirestore(bankData: HashMap<String, String>) {
        val uid = sharedHelper.getFromUser("userid") ?: run {
            showToast("User not logged in")
            return
        }

        showLoader()

        firestore.collection("shops")
            .document(uid)
            .update(bankData as Map<String, Any>)
            .addOnSuccessListener {
                hideLoader()
                showToast("Bank details updated successfully")
                fragmentManagers!!.popBackStackImmediate()

                // Save locally for next session
                sharedHelper.putInUser("accname", bankData["accname"].orEmpty())
                sharedHelper.putInUser("accnumber", bankData["accnumber"].orEmpty())
                sharedHelper.putInUser("ifsccode", bankData["ifsccode"].orEmpty())
            }
            .addOnFailureListener { e ->
                hideLoader()
                // If document doesn't exist, create it
                firestore.collection("shops")
                    .document(uid)
                    .set(bankData)
                    .addOnSuccessListener {
                        hideLoader()
                        showToast("Bank details updated successfully")
                        sharedHelper.putInUser("accname", bankData["accname"].orEmpty())
                        sharedHelper.putInUser("accnumber", bankData["accnumber"].orEmpty())
                        sharedHelper.putInUser("ifsccode", bankData["ifsccode"].orEmpty())
                    }
                    .addOnFailureListener { ex ->
                        hideLoader()
                        showToast("Failed to update bank details: ${ex.message}")
                    }
            }
    }


}
