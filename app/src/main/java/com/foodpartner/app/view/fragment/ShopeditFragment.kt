package com.foodpartner.app.view.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.TimePicker
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.ViewDataBinding
import com.app.washeruser.repository.Status
import com.bumptech.glide.Glide
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentLoginBinding
import com.foodpartner.app.databinding.FragmentOtpBinding
import com.foodpartner.app.databinding.FragmentProfilefragmentBinding
import com.foodpartner.app.databinding.FragmentProfilepagefrgamentBinding
import com.foodpartner.app.databinding.FragmentShopcreateBinding
import com.foodpartner.app.databinding.FragmentshopeditBinding
import com.foodpartner.app.network.Constant
import com.foodpartner.app.view.activity.HomeActivity
import com.foodpartner.app.view.responsemodel.OtpResponseModel
import com.foodpartner.app.view.responsemodel.ShopCreateResponsemodel
import com.foodpartner.app.view.responsemodel.UserregisterResponseModel
import com.foodpartner.app.viewModel.HomeViewModel
import com.foodpartner.app.viewModel.LoginViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.mukesh.OtpView
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Timer
import kotlin.concurrent.schedule

class ShopeditFragment : BaseFragment<FragmentshopeditBinding>() {
    var imagePath: String = ""
    var preorder: String = "yes"
    var categorytype: String = "nonveg"
    var mode: String = "active"
    var gstapplicable: String = ""
    private var monday: String? = null
    private var tuesday: String? = null
    private var wednesday: String? = null
    private var thursday: String? = null
    private var friday: String? = null
    private var saturday: String? = null
    private var sunday: String? = null
    private val homeViewModel by viewModel<HomeViewModel>()
    lateinit var dialog: Dialog  // class-level declaration

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        homeViewModel.response().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            processResponse(it)
        })
        this.mViewDataBinding.apply {
            backBtn.setOnClickListener {
                fragmentManagers!!.popBackStack()
            }
            restaurantname.setText(sharedHelper.getFromUser("resname"))
            restaurantemail.setText(sharedHelper.getFromUser("resemail"))
            restaurantmobilenon.setText(sharedHelper.getFromUser("resmobno"))
            restaurantStreet.setText(sharedHelper.getFromUser("resstreet"))
            restaurantCity.setText(sharedHelper.getFromUser("rescity"))
            restaurantPinCode.setText(sharedHelper.getFromUser("respincode"))
            restaurantLandMark.setText(sharedHelper.getFromUser("reslandmark"))
            restaurantDescreption.setText(sharedHelper.getFromUser("resdesc"))
            tradeId.setText(sharedHelper.getFromUser("restradeid"))
            if(sharedHelper.getFromUser("restype").equals("veg")){
                veg.isChecked=true
                nonveg.isChecked=false
                both.isChecked=false
            }else if(sharedHelper.getFromUser("restype").equals("nonveg")){
                veg.isChecked=false
                nonveg.isChecked=true
                both.isChecked=false
            }else{
                veg.isChecked=false
                nonveg.isChecked=false
                both.isChecked=true
            }
        }

        this.mViewDataBinding.preorderyes.setOnClickListener {
            preorder="yes"
            this.mViewDataBinding.preorderyes.isChecked=true
            this.mViewDataBinding.preorderno.isChecked=false
        }
        this.mViewDataBinding.preorderno.setOnClickListener {
            preorder="no"
            this.mViewDataBinding.preorderyes.isChecked=false
            this.mViewDataBinding.preorderno.isChecked=true
        }
        this.mViewDataBinding.active.setOnClickListener {
            mode="active"
            this.mViewDataBinding.inactive.isChecked=false
            this.mViewDataBinding.active.isChecked=true
        }
        this.mViewDataBinding.inactive.setOnClickListener {
            mode="inactive"
            this.mViewDataBinding.inactive.isChecked=true
            this.mViewDataBinding.active.isChecked=false
        }
        this.mViewDataBinding.gstapplicalbleyes.setOnClickListener {
            gstapplicable="yes"
            this.mViewDataBinding.gstapplicalbleno.isChecked=false
            this.mViewDataBinding.gstapplicalbleyes.isChecked=true
        }
        this.mViewDataBinding.gstapplicalbleno.setOnClickListener {
            gstapplicable="no"
            this.mViewDataBinding.gstapplicalbleyes.isChecked=false
            this.mViewDataBinding.gstapplicalbleno.isChecked=true
        }
        this.mViewDataBinding.veg.setOnClickListener {
            categorytype="veg"
            this.mViewDataBinding.veg.isChecked=true
            this.mViewDataBinding.nonveg.isChecked=false
            this.mViewDataBinding.both.isChecked=false
        }
        this.mViewDataBinding.nonveg.setOnClickListener {
            categorytype="nonveg"
            this.mViewDataBinding.veg.isChecked=false
            this.mViewDataBinding.nonveg.isChecked=true
            this.mViewDataBinding.both.isChecked=false
        }
        this.mViewDataBinding.both.setOnClickListener {
            categorytype="both"
            this.mViewDataBinding.veg.isChecked=false
            this.mViewDataBinding.nonveg.isChecked=false
            this.mViewDataBinding.both.isChecked=true
        }
        this.mViewDataBinding.apply {
            Timer().schedule(5000) {
                Handler(Looper.getMainLooper()).post {
                    loader.visibility = View.GONE // Update UI on the main thread
                }
            }
        }



        this.mViewDataBinding. accConBtn.setOnClickListener {

            /*  val monstart = this.mViewDataBinding.monstarttime.text.toString()
              val tuestar = this.mViewDataBinding.tuesstarttime.text.toString()
              val wedstart = this.mViewDataBinding.wednesstarttime.text.toString()
              val thurstart = this.mViewDataBinding.thursstarttime.text.toString()
              val fristart = this.mViewDataBinding.fridastarttime.text.toString()
              val satstart = this.mViewDataBinding.saturstarttime.text.toString()
              val sunstart = this.mViewDataBinding.sundaystarttime.text.toString()
              val monend = this.mViewDataBinding.monendtime.text.toString()
              val tueend = this.mViewDataBinding.tuesendtime.text.toString()
              val wedend = this.mViewDataBinding.wednesdayendtime.text.toString()
              val thurend = this.mViewDataBinding.thursdayendtime.text.toString()
              val friend = this.mViewDataBinding.fridayendtime.text.toString()
              val satend = this.mViewDataBinding.saturdayendtime.text.toString()
              val sunend = this.mViewDataBinding.sundayendtime.text.toString()*/
            println("cliuckkkkkk")
            /*if (imagePath.isEmpty() || imagePath!=null){
                showToast("Please select restaurant image")
            }else*/ if(TextUtils.isEmpty(this.mViewDataBinding.restaurantname.text)){
            showToast("Please enter your Restaurant name")
        }else if(TextUtils.isEmpty(this.mViewDataBinding.restaurantemail.text)){
            showToast("Please enter your Restaurant Email")
        }else if(TextUtils.isEmpty(this.mViewDataBinding.restaurantmobilenon.text)){
            showToast("Please enter your Restaurant Mobile Number")
        }else if(TextUtils.isEmpty(this.mViewDataBinding.password.text)){
            showToast("Please enter your Restaurant Password")
        }else if(TextUtils.isEmpty(this.mViewDataBinding.restaurantStreet.text)){
            showToast("Please enter your Restaurant Street")
        }
        else if(TextUtils.isEmpty(this.mViewDataBinding.restaurantCity.text)){
            showToast("Please enter your Restaurant City")
        } else if(TextUtils.isEmpty(this.mViewDataBinding.restaurantPinCode.text)){
            showToast("Please enter your Restaurant Pin code")
        } else if(TextUtils.isEmpty(this.mViewDataBinding.restaurantLandMark.text)){
            showToast("Please enter your Restaurant LandMark")
        } else if(TextUtils.isEmpty(this.mViewDataBinding.tradeId.text)){
            showToast("Please enter your Restaurant Trade Id")
        }
        /*
                else if(TextUtils.isEmpty(this.mViewDataBinding.fssainumber.text)){
                    showToast("Please enter your FSSAI number")
                }else if(!isValidFssai(this.mViewDataBinding.fssainumber.text.toString())){
                    showToast("Please enter your valid FSSAI number")
                }else if(TextUtils.isEmpty(this.mViewDataBinding.pannumber.text)){
                    showToast("Please enter your PAN name")
                }else if(!isValidPAN(this.mViewDataBinding.pannumber.text.toString())){
                    showToast("Please enter your valid PAN name")
                }else if(TextUtils.isEmpty(this.mViewDataBinding.gstinnumber.text)){
                    showToast("Please enter your GSTIN number")
                }else if(!isValidGSTIN(this.mViewDataBinding.gstinnumber.text.toString())){
                    showToast("Please enter your valid GSTIN number")
                }else   if (!this.mViewDataBinding.logChkBox.isChecked && !this.mViewDataBinding.tuesdaycheck.isChecked && !this.mViewDataBinding.wednesdaycheck.isChecked &&
                    !this.mViewDataBinding.thursdaycheck.isChecked && !this.mViewDataBinding.fridaycheck.isChecked && !this.mViewDataBinding.saturdaycheck.isChecked && !this.mViewDataBinding.sundaycheck.isChecked
                ) {
                    showToast("Please Choose a Day")
                } else if (this.mViewDataBinding.logChkBox.isChecked &&
                    (TextUtils.isEmpty(monstart) || TextUtils.isEmpty(monend))
                ) {
                    showToast("Please Set a Valid Day / Timing")
                } else if (this.mViewDataBinding.tuesdaycheck.isChecked &&
                    (TextUtils.isEmpty(tuestar) || TextUtils.isEmpty(tueend))
                ) {
                    showToast("Please Set a Valid Day / Timing")
                } else if (this.mViewDataBinding.wednesdaycheck.isChecked &&
                    (TextUtils.isEmpty(wedend) || TextUtils.isEmpty(wedstart))
                ) {
                    showToast("Please Set a Valid Day / Timing")
                } else if (this.mViewDataBinding.thursdaycheck.isChecked &&
                    (TextUtils.isEmpty(thurstart) || TextUtils.isEmpty(thurend))
                ) {
                    showToast("Please Set a Valid Day / Timing")
                } else if (this.mViewDataBinding.fridaycheck.isChecked &&
                    (TextUtils.isEmpty(fristart) || TextUtils.isEmpty(friend))
                ) {
                    showToast("Please Set a Valid Day / Timing")
                } else if (this.mViewDataBinding.saturdaycheck.isChecked &&
                    (TextUtils.isEmpty(satstart) || TextUtils.isEmpty(satend))
                ) {
                    showToast("Please Set a Valid Day / Timing")
                } else if (this.mViewDataBinding.sundaycheck.isChecked &&
                    (TextUtils.isEmpty(sunstart) || TextUtils.isEmpty(sunend))
                ) {
                    showToast("Please Set a Valid Day / Timing")
                }*/
        else{
            this.mViewDataBinding.loader.visibility= View.VISIBLE



            val map :HashMap<String,String> = HashMap()
            map.put("restaurantName",this.mViewDataBinding.restaurantname.text.toString())
            map.put("restaurantEMail",this.mViewDataBinding.restaurantemail.text.toString())
            map.put("mobileNumber",this.mViewDataBinding.restaurantmobilenon.text.toString())
            map.put("password",this.mViewDataBinding.password.text.toString())
            map.put("confirmPassword",this.mViewDataBinding.password.text.toString())
            map.put("restaurantStreet",this.mViewDataBinding.restaurantStreet.text.toString())
            map.put("restaurantCity",this.mViewDataBinding.restaurantCity.text.toString())
            map.put("restaurantStreet",this.mViewDataBinding.restaurantStreet.text.toString())
            map.put("restaurantPinCode",this.mViewDataBinding.restaurantPinCode.text.toString())
            map.put("restaurantLandMark",this.mViewDataBinding.restaurantLandMark.text.toString())
            map.put("restaurantLat",sharedHelper.getFromUser("reslat"))
            map.put("restaurantLng",sharedHelper.getFromUser("reslong"))
            map.put("shopFcmToken",sharedHelper.getFromUser("fcm"))
            map.put("tradeId",this.mViewDataBinding.tradeId.text.toString())
            map.put("restaurantDescreption",this.mViewDataBinding.restaurantDescreption.text.toString())
            map.put("restaurantType",categorytype)
            homeViewModel.updateres(sharedHelper.getFromUser("userid"),map)

        }
        }

        /* this.mViewDataBinding.logChkBox.setOnCheckedChangeListener { _, isChecked ->
             if (isChecked) {
                 this.mViewDataBinding.logChkBox.setBackgroundResource(R.drawable.check_box_active)
                 monday = "monday"
                 this.mViewDataBinding.monstarttime.isEnabled = true
                 this.mViewDataBinding.monendtime.isEnabled = true
             } else {
                 this.mViewDataBinding.logChkBox.setBackgroundResource(R.drawable.check_box_inactive)
                 this.mViewDataBinding.monstarttime.isEnabled = false
                 this.mViewDataBinding.monendtime.isEnabled = false
             }
         }

         this.mViewDataBinding.tuesdaycheck.setOnCheckedChangeListener { _, isChecked ->
             if (isChecked) {
                 this.mViewDataBinding.tuesdaycheck.setBackgroundResource(R.drawable.check_box_active)
                 tuesday = "tuesday"
                 this.mViewDataBinding.tuesstarttime.isEnabled = true
                 this.mViewDataBinding.tuesendtime.isEnabled = true
             } else {
                 this.mViewDataBinding.tuesdaycheck.setBackgroundResource(R.drawable.check_box_inactive)
                 this.mViewDataBinding.tuesstarttime.isEnabled = false
                 this.mViewDataBinding.tuesendtime.isEnabled = false
             }
         }

         this.mViewDataBinding.wednesdaycheck.setOnCheckedChangeListener { _, isChecked ->
             if (isChecked) {
                 this.mViewDataBinding.wednesdaycheck.setBackgroundResource(R.drawable.check_box_active)
                 wednesday = "wednesday"
                 this.mViewDataBinding.wednesstarttime.isEnabled = true
                 this.mViewDataBinding.wednesdayendtime.isEnabled = true
             } else {
                 this.mViewDataBinding.wednesdaycheck.setBackgroundResource(R.drawable.check_box_inactive)
                 this.mViewDataBinding.wednesstarttime.isEnabled = false
                 this.mViewDataBinding.wednesdayendtime.isEnabled = false
             }
         }

         this.mViewDataBinding.thursdaycheck.setOnCheckedChangeListener { _, isChecked ->
             if (isChecked) {
                 this.mViewDataBinding.thursdaycheck.setBackgroundResource(R.drawable.check_box_active)
                 thursday = "thursday"
                 this.mViewDataBinding.thursstarttime.isEnabled = true
                 this.mViewDataBinding.thursdayendtime.isEnabled = true
             } else {
                 this.mViewDataBinding.thursdaycheck.setBackgroundResource(R.drawable.check_box_inactive)
                 this.mViewDataBinding.thursstarttime.isEnabled = false
                 this.mViewDataBinding.thursdayendtime.isEnabled = false
             }
         }

         this.mViewDataBinding.fridaycheck.setOnCheckedChangeListener { _, isChecked ->
             if (isChecked) {
                 this.mViewDataBinding.fridaycheck.setBackgroundResource(R.drawable.check_box_active)
                 friday = "friday"
                 this.mViewDataBinding.fridastarttime.isEnabled = true
                 this.mViewDataBinding.fridayendtime.isEnabled = true
             } else {
                 this.mViewDataBinding.fridaycheck.setBackgroundResource(R.drawable.check_box_inactive)
                 this.mViewDataBinding.fridastarttime.isEnabled = false
                 this.mViewDataBinding.fridayendtime.isEnabled = false
             }
         }

         this.mViewDataBinding.saturdaycheck.setOnCheckedChangeListener { _, isChecked ->
             if (isChecked) {
                 this.mViewDataBinding.saturdaycheck.setBackgroundResource(R.drawable.check_box_active)
                 saturday = "saturday"
                 this.mViewDataBinding.saturstarttime.isEnabled = true
                 this.mViewDataBinding.saturdayendtime.isEnabled = true
             } else {
                 this.mViewDataBinding.saturdaycheck.setBackgroundResource(R.drawable.check_box_inactive)
                 this.mViewDataBinding.saturstarttime.isEnabled = false
                 this.mViewDataBinding.saturdayendtime.isEnabled = false
             }
         }

         this.mViewDataBinding.sundaycheck.setOnCheckedChangeListener { _, isChecked ->
             if (isChecked) {
                 this.mViewDataBinding.sundaycheck.setBackgroundResource(R.drawable.check_box_active)
                 saturday = "sunday"
                 this.mViewDataBinding.sundaystarttime.isEnabled = true
                 this.mViewDataBinding.sundayendtime.isEnabled = true
             } else {
                 this.mViewDataBinding.sundaycheck.setBackgroundResource(R.drawable.check_box_inactive)
                 this.mViewDataBinding.sundaystarttime.isEnabled = false
                 this.mViewDataBinding.sundayendtime.isEnabled = false
             }
         }*/
        this.mViewDataBinding.apply {
            /* monstarttime.isEnabled = false
             monendtime.isEnabled = false
             tuesstarttime.isEnabled = false
             tuesendtime.isEnabled = false
             wednesstarttime.isEnabled = false
             wednesdayendtime.isEnabled = false
             thursstarttime.isEnabled = false
             thursdayendtime.isEnabled = false
             fridastarttime.isEnabled = false
             fridayendtime.isEnabled = false
             saturstarttime.isEnabled = false
             saturdayendtime.isEnabled = false
             sundaystarttime.isEnabled = false
             sundayendtime.isEnabled = false*/
            accProfileEdit.setSafeOnClickListener {
                ImagePicker.with(requireActivity()).cropSquare().createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }

            }
            /*     monstarttime.setOnClickListener {
                     Constant.starttime = "monstarttime"
                     showDialog()
                 }
                 tuesstarttime.setOnClickListener {
                     Constant.starttime = "tuestarttime"
                     showDialog()
                 }
                 wednesstarttime.setOnClickListener {
                     Constant.starttime = "wedstarttime"
                     showDialog()
                 }
                 thursstarttime.setOnClickListener {
                     Constant.starttime = "thustarttime"
                     showDialog()
                 }
                 fridastarttime.setOnClickListener {
                     Constant.starttime = "fristarttime"
                     showDialog()
                 }
                 saturstarttime.setOnClickListener {
                     Constant.starttime = "satstarttime"
                     showDialog()
                 }
                 sundaystarttime.setOnClickListener {
                     Constant.starttime = "sunstarttime"
                     showDialog()
                 }
                 monendtime.setOnClickListener {
                     Constant.endtime = "monendtime"
                     showDialog()
                 }
                 tuesendtime.setOnClickListener {
                     Constant.endtime = "tueendtime"
                     showDialog()
                 }
                 wednesdayendtime.setOnClickListener {
                     Constant.endtime = "wedendtime"
                     showDialog()
                 }
                 thursdayendtime.setOnClickListener {
                     Constant.endtime = "thuendtime"
                     showDialog()
                 }
                 fridayendtime.setOnClickListener {
                     Constant.endtime = "friendtime"
                     showDialog()
                 }
                 saturdayendtime.setOnClickListener {
                     Constant.endtime = "satendtime"
                     showDialog()
                 }
                 sundayendtime.setOnClickListener {
                     Constant.endtime = "sunendtime"
                     showDialog()
                 }*/

        }
    }

    override fun getLayoutId(): Int = R.layout.fragmentshopedit


    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!
                    val mProfileUri: Uri = fileUri
                    println("imgPath:: $fileUri")
                    sharedHelper.putInUser("resimage",mProfileUri.toString())
                    this.mViewDataBinding.accProfile.setImageURI(mProfileUri)
                    if (mProfileUri.path != null) {
                        imagePath = fileUri.path.toString()
                    }
                }

                ImagePicker.RESULT_ERROR -> {
                    showToast("img set error" + ImagePicker.getError(data))
                }

                else -> {
                    showToast("Task Cancelled")
                }
            }
        }


    /*
        private fun showDialog() {
            val dialog = Dialog(requireActivity())
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(R.layout.timepickerdialog)
            dialog.show()

            val timePicker = dialog.findViewById<TimePicker>(R.id.timePicker)
            val ok = dialog.findViewById<TextView>(R.id.ok)
            timePicker.setOnTimeChangedListener { _, hour, minute ->
                var hour = hour
                var am_pm = ""
                // AM_PM decider logic
                when {
                    hour == 0 -> {
                        hour += 12
                        am_pm = "AM"
                    }

                    hour == 12 -> am_pm = "PM"
                    hour > 12 -> {
                        hour -= 12
                        am_pm = "PM"
                    }

                    else -> am_pm = "AM"
                }

                ok.setOnClickListener {
                    val hour1 = if (hour < 10) "0" + hour else hour
                    val min = if (minute < 10) "0" + minute else minute
                    val msg = "$hour1 : $min $am_pm"
                    if (Constant.starttime == "monstarttime") {
                        this.mViewDataBinding.monstarttime.text = msg
                        Constant.starttime = ""
                        dialog.dismiss()
                    } else if (Constant.starttime == "tuestarttime") {
                        this.mViewDataBinding.tuesstarttime.text = msg
                        Constant.starttime = ""
                        dialog.dismiss()
                    } else if (Constant.starttime == "wedstarttime") {
                        this.mViewDataBinding.wednesstarttime.text = msg
                        Constant.starttime = ""
                        dialog.dismiss()
                    } else if (Constant.starttime == "thustarttime") {
                        this.mViewDataBinding.thursstarttime.text = msg
                        Constant.starttime = ""
                        dialog.dismiss()
                    } else if (Constant.starttime == "fristarttime") {
                        this.mViewDataBinding.fridastarttime.text = msg
                        Constant.starttime = ""
                        dialog.dismiss()
                    } else if (Constant.starttime == "satstarttime") {
                        this.mViewDataBinding.saturstarttime.text = msg
                        Constant.starttime = ""
                        dialog.dismiss()
                    } else if (Constant.starttime == "sunstarttime") {
                        this.mViewDataBinding.sundaystarttime.text = msg
                        Constant.starttime = ""
                        dialog.dismiss()
                    } else if (Constant.endtime == "monendtime") {
                        this.mViewDataBinding.monendtime.text = msg
                        Constant.endtime = ""
                        dialog.dismiss()
                    } else if (Constant.endtime == "tueendtime") {
                        this.mViewDataBinding.tuesendtime.text = msg
                        Constant.endtime = ""
                        dialog.dismiss()
                    } else if (Constant.endtime == "wedendtime") {
                        this.mViewDataBinding.wednesdayendtime.text = msg
                        Constant.endtime = ""
                        dialog.dismiss()
                    } else if (Constant.endtime.equals("thuendtime")) {
                        this.mViewDataBinding.thursdayendtime.text = msg
                        Constant.endtime = ""
                        dialog.dismiss()
                    } else if (Constant.endtime == "friendtime") {
                        this.mViewDataBinding.fridayendtime.text = msg
                        Constant.endtime = ""
                        dialog.dismiss()
                    } else if (Constant.endtime == "satendtime") {
                        this.mViewDataBinding.saturdayendtime.text = msg
                        Constant.endtime = ""
                        dialog.dismiss()
                    } else if (Constant.endtime == "sunendtime") {
                        this.mViewDataBinding.sundayendtime.text = msg
                        Constant.endtime = ""
                        dialog.dismiss()
                    }
                }
            }
        }
    */
    private fun processResponse(response: com.foodpartner.app.network.Response) {
        when (response.status) {
            Status.SUCCESS -> {
                this.mViewDataBinding.loader.visibility= View.GONE
                when (response.data) {
                    is ShopCreateResponsemodel -> {
                        showToast("Update Suceesfully")
                        fragmentManagers!!.popBackStack()
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