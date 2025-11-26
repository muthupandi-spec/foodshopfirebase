package com.foodpartner.app.view.fragment

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.ViewDataBinding
import com.app.washeruser.repository.Status
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentBankdetaileditfragmentBinding
import com.foodpartner.app.databinding.FragmentLoginBinding
import com.foodpartner.app.databinding.FragmentOtpBinding
import com.foodpartner.app.databinding.FragmentProfilefragmentBinding
import com.foodpartner.app.databinding.FragmentProfilepagefrgamentBinding
import com.foodpartner.app.view.responsemodel.UserregisterResponseModel
import com.foodpartner.app.viewModel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Timer
import kotlin.concurrent.schedule

class BankUpdateFragment : BaseFragment<FragmentBankdetaileditfragmentBinding>() {
    var banknamevale :String="State Bank of India"
    private val homeViewModel by viewModel<HomeViewModel>()

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        this.mViewDataBinding.apply {
            homeViewModel.response().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                processResponse(it)
            })
            Timer().schedule(5000) {
                Handler(Looper.getMainLooper()).post {
                    loader.visibility = View.GONE // Update UI on the main thread
                }
            }
            val items = arrayOf("State Bank of India", "ICICI Bank", "Kotak Mahindra Bank", "Indane Bank")

            val adapter = ArrayAdapter(activitys, android.R.layout.simple_spinner_item, items)
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Apply the adapter to the spinner
            bankname.adapter = adapter
            bankname.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // Get the selected item from the spinner
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    banknamevale = selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Another interface callback
                }
            }

            accname.setText( sharedHelper.getFromUser("accname"))
            accnumber.setText( sharedHelper.getFromUser("accnumber"))
            ifsccode.setText( sharedHelper.getFromUser("ifsccode"))
            bankname.setSelection(2)
backBtn.setOnClickListener {
    fragmentManagers!!.popBackStackImmediate()
}

            update.setOnClickListener {
                if(TextUtils.isEmpty(accname.text.toString())){
                    showToast("Please enter your account name")
                }else if(TextUtils.isEmpty(accnumber.text.toString())){
                    showToast("Please enter your account number")
                }else if(TextUtils.isEmpty(ifsccode.text.toString())){
                    showToast("Please enter your IFSC code")
                }else if(!isValidIFSC(ifsccode.text.toString())){
                    showToast("Please enter your valid IFSC code")
                }else{
                    val hashMap :HashMap<String,String> = HashMap()
                    hashMap["accname"]=accname.text.toString()
                    hashMap["accnumber"]=accnumber.text.toString()
                    hashMap["ifsccode"]=ifsccode.text.toString()
                    homeViewModel.updatebank(hashMap)
                    showToast("Bank update sucessfully")
                }
            }

        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_bankdetaileditfragment
    private fun processResponse(response: com.foodpartner.app.network.Response) {
        when (response.status) {
            Status.SUCCESS -> {
                when (response.data) {
                    is UserregisterResponseModel -> {
                    }

                }
            }

            Status.ERROR -> {
            }

            Status.LOADING -> {}
            Status.SECONDLOADING -> {}
            Status.DISMISS -> {}
        }
    }

}