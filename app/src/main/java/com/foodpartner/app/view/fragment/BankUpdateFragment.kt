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
import kotlin.sequences.ifEmpty

class BankUpdateFragment : BaseFragment<FragmentBankdetaileditfragmentBinding>() {

    private val firestore = FirebaseFirestore.getInstance()
    private var bankNameValue: String = "State Bank of India"

    override fun getLayoutId(): Int = R.layout.fragment_bankdetaileditfragment

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        val binding = mViewDataBinding as FragmentBankdetaileditfragmentBinding

        // Load existing bank data from SharedHelper
        binding.accname.setText(sharedHelper.getFromUser("accname"))
        binding.accnumber.setText(sharedHelper.getFromUser("accnumber"))
        binding.ifsccode.setText(sharedHelper.getFromUser("ifsccode"))

        // Bank spinner
        val banks = arrayOf("State Bank of India", "ICICI Bank", "Kotak Mahindra Bank", "Indane Bank")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, banks)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.bankname.adapter = adapter
        binding.bankname.setSelection(banks.indexOf(sharedHelper.getFromUser("bankname").ifEmpty { "State Bank of India" }))
        binding.bankname.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                bankNameValue = parent.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Back button
        binding.backBtn.setOnClickListener { fragmentManagers?.popBackStackImmediate() }

        // Update bank details
        binding.update.setOnClickListener {
            val accName = binding.accname.text.toString().trim()
            val accNumber = binding.accnumber.text.toString().trim()
            val ifscCode = binding.ifsccode.text.toString().trim()

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

        // Use merge = true to avoid overwriting existing fields
        firestore.collection("shops")
            .document(uid)
            .set(bankData, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener {
                hideLoader()
                showToast("Bank details updated successfully")
                fragmentManagers?.popBackStackImmediate()

                // Save locally
                sharedHelper.putInUser("accname", bankData["accname"].orEmpty())
                sharedHelper.putInUser("accnumber", bankData["accnumber"].orEmpty())
                sharedHelper.putInUser("ifsccode", bankData["ifsccode"].orEmpty())
                sharedHelper.putInUser("bankname", bankData["bankname"].orEmpty())
            }
            .addOnFailureListener { e ->
                hideLoader()
                showToast("Failed to update bank details: ${e.message}")
            }
    }

    // Simple IFSC validation
    private fun isValidIFSC(code: String): Boolean {
        val regex = Regex("^[A-Z]{4}0[A-Z0-9]{6}$")
        return regex.matches(code)
    }
}
