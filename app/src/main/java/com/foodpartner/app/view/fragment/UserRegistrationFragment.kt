package com.foodpartner.app.view.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import com.app.washeruser.repository.Status
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentLoginBinding
import com.foodpartner.app.databinding.FragmentUserregistrationBinding
import com.foodpartner.app.view.responsemodel.LoginResponseModel
import com.foodpartner.app.view.responsemodel.UserregisterResponseModel
import com.foodpartner.app.viewModel.LoginViewModel
import com.mukesh.OtpView
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Timer
import kotlin.concurrent.schedule
class UserRegistrationFragment: BaseFragment<FragmentUserregistrationBinding>(){
    var banknamevale :String="State Bank of India"
    private val loginViewModel by viewModel<LoginViewModel>()
    lateinit var dialog: Dialog  // class-level declaration


    override fun initView(mViewDataBinding: ViewDataBinding?) {
        loginViewModel.response().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            processResponse(it)
        })
this.mViewDataBinding.apply {
    Timer().schedule(5000) {
        Handler(Looper.getMainLooper()).post {
            loader.visibility = View.GONE // Update UI on the main thread
        }
    }
    accConBtn.setOnClickListener {

        if(TextUtils.isEmpty(firstname.text)){
            showToast("Please enter your first name")
        }else if(TextUtils.isEmpty(lastname.text)){
            showToast("Please enter your last name")
        }else if(TextUtils.isEmpty(accMobNum.text)){
            showToast("Please enter your mobile number")
        }else if(TextUtils.isEmpty(emailedt.text)){
            showToast("Please enter your email address")
        }else if(!isValidEmail(emailedt.text.toString())){
            showToast("Please enter your valid email address")
        }else if(TextUtils.isEmpty(aadharno.text)){
            showToast("Please enter your aadhar no")
        }else if(!isValidAadhaar(aadharno.text.toString())){
            showToast("Please enter your valid aadhar number")
        }else if(TextUtils.isEmpty(businessname.text)){
            showToast("Please enter your Business name")
        }else if(TextUtils.isEmpty(businessaddress.text)){
            showToast("Please enter your Business address")
        }else if(TextUtils.isEmpty(fssainumber.text)){
            showToast("Please enter your FSSAI number")
        }else if(!isValidFssai(fssainumber.text.toString())){
            showToast("Please enter your valid FSSAI number")
        }else if(TextUtils.isEmpty(pannumber.text)){
            showToast("Please enter your PAN name")
        }else if(!isValidPAN(pannumber.text.toString())){
            showToast("Please enter your valid PAN name")
        }else if(TextUtils.isEmpty(gstno.text)){
            showToast("Please enter your GSTIN number")
        }else if(!isValidGSTIN(gstno.text.toString())){
            showToast("Please enter your valid GSTIN number")
        }else if(TextUtils.isEmpty(accname.text)){
            showToast("Please enter your account name")
        }else if(TextUtils.isEmpty(accnumber.text)){
            showToast("Please enter your account number")
        }else if(TextUtils.isEmpty(ifsccode.text)){
            showToast("Please enter your IFSC code")
        }else if(!isValidIFSC(ifsccode.text.toString())){
            showToast("Please enter your valid IFSC code")
        }else {


            sharedHelper.putInUser("firstname",firstname.text.toString())
            sharedHelper.putInUser("lastname",lastname.text.toString())
            sharedHelper.putInUser("mobileno",accMobNum.text.toString())
            sharedHelper.putInUser("email",emailedt.text.toString())
            sharedHelper.putInUser("aadharno",aadharno.text.toString())
            sharedHelper.putInUser("businessname",businessname.text.toString())
            sharedHelper.putInUser("businessaddress",businessaddress.text.toString())
            sharedHelper.putInUser("fssainumber",fssainumber.text.toString())
            sharedHelper.putInUser("pannumber",pannumber.text.toString())
            sharedHelper.putInUser("gstno",gstno.text.toString())
            sharedHelper.putInUser("accname",accname.text.toString())
            sharedHelper.putInUser("bankname",banknamevale)
            sharedHelper.putInUser("accnumber",accnumber.text.toString())
            sharedHelper.putInUser("ifsccode",ifsccode.text.toString())
            val map :HashMap<String,String> = HashMap()
            map.put("firstname",firstname.text.toString())
            map.put("lastname",lastname.text.toString())
            map.put("mobileno",accMobNum.text.toString())
            map.put("email",emailedt.text.toString())
            map.put("aadharno",aadharno.text.toString())
            map.put("businessname",businessname.text.toString())
            map.put("businessaddress",businessaddress.text.toString())
            map.put("fssainumber",fssainumber.text.toString())
            map.put("pannumber",pannumber.text.toString())
            map.put("gstno",gstno.text.toString())
            map.put("accname",accname.text.toString())
            map.put("bankname",banknamevale)
            map.put("accnumber",accnumber.text.toString())
            map.put("ifsccode",ifsccode.text.toString())
            loginViewModel.register(map)
            loadFragment(ShopCreateFragment(),android.R.id.content,"shopcreate",false)


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

}
    }
    override fun getLayoutId(): Int = R.layout.fragment_userregistration

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
    @SuppressLint("SetTextI18n")
    private fun showDialog() {
        dialog = Dialog(activitys)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.otp_popup)
        val otpTxtView = dialog.findViewById<TextView>(R.id.otp_details)
        val otp = dialog.findViewById<OtpView>(R.id.otp)
        val backBtn = dialog.findViewById<ImageView>(R.id.backBtn)
        otpTxtView.text = "Enter OTP send to " + this.mViewDataBinding.accMobNum.text.toString()
        val okBtn = dialog.findViewById<Button>(R.id.submit_otp)

        backBtn.setOnClickListener {
            dialog.dismiss()
        }
        okBtn.setOnClickListener {
            if (TextUtils.isEmpty(otp.text)) {
                showToast("Please enter your otp number")
            } else {

                loginViewModel.otp(this.mViewDataBinding.accMobNum.text.toString(),dialog.findViewById<OtpView>(R.id.otp).text.toString())


            }


        }
        dialog.show()


    }

}