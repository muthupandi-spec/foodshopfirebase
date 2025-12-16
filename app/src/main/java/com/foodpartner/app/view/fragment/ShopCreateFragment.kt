package com.foodpartner.app.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.ViewDataBinding
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentShopcreateBinding
import com.foodpartner.app.view.requestmodel.LocationEvent
import com.github.dhaval2404.imagepicker.ImagePicker
import com.mukesh.OtpView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ShopCreateFragment : BaseFragment<FragmentShopcreateBinding>() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = com.google.firebase.storage.FirebaseStorage.getInstance().reference
    private var shopStartTime: String = ""
    private var shopEndTime: String = ""

    // NOTIFICATION CONSTANTS
    private val CHANNEL_ID = "manual_otp_channel"
    private val NOTIFICATION_ID = 999
    private val NOTIFICATION_REQUEST_CODE = 1001

    private var generatedOtp = ""

    var imagePath: String = ""
    var categorytype: String = "nonveg"
    var preorder: String = "yes"
    var mode: String = "active"
    var gstapplicable: String = ""

    var latitute: String? = null
    var longitute: String? = null

    lateinit var dialog: Dialog

    override fun initView(mViewDataBinding: ViewDataBinding?) {

        // Create notification channel once
        createNotificationChannel()
        this.mViewDataBinding.restaurantLandMark.setOnClickListener {
            loadFragment(PlaceSearchFragment(), android.R.id.content, "placeserach", true)
        }
        this.mViewDataBinding.backBtn.setOnClickListener {
            fragmentManagers!!.popBackStack()
        }
        this.mViewDataBinding.preorderyes.setOnClickListener {
            preorder = "yes"
            this.mViewDataBinding.preorderyes.isChecked = true
            this.mViewDataBinding.preorderno.isChecked = false
        }
        this.mViewDataBinding.preorderno.setOnClickListener {
            preorder = "no"
            this.mViewDataBinding.preorderyes.isChecked = false
            this.mViewDataBinding.preorderno.isChecked = true
        }
        this.mViewDataBinding.active.setOnClickListener {
            mode = "active"
            this.mViewDataBinding.inactive.isChecked = false
            this.mViewDataBinding.active.isChecked = true
        }
        this.mViewDataBinding.inactive.setOnClickListener {
            mode = "inactive"
            this.mViewDataBinding.inactive.isChecked = true
            this.mViewDataBinding.active.isChecked = false
        }
        this.mViewDataBinding.gstapplicalbleyes.setOnClickListener {
            gstapplicable = "yes"
            this.mViewDataBinding.gstapplicalbleno.isChecked = false
            this.mViewDataBinding.gstapplicalbleyes.isChecked = true
        }
        this.mViewDataBinding.gstapplicalbleno.setOnClickListener {
            gstapplicable = "no"
            this.mViewDataBinding.gstapplicalbleyes.isChecked = false
            this.mViewDataBinding.gstapplicalbleno.isChecked = true
        }
        this.mViewDataBinding.veg.setOnClickListener {
            categorytype = "veg"
            this.mViewDataBinding.veg.isChecked = true
            this.mViewDataBinding.nonveg.isChecked = false
            this.mViewDataBinding.both.isChecked = false
        }
        this.mViewDataBinding.nonveg.setOnClickListener {
            categorytype = "nonveg"
            this.mViewDataBinding.veg.isChecked = false
            this.mViewDataBinding.nonveg.isChecked = true
            this.mViewDataBinding.both.isChecked = false
        }
        this.mViewDataBinding.both.setOnClickListener {
            categorytype = "both"
            this.mViewDataBinding.veg.isChecked = false
            this.mViewDataBinding.nonveg.isChecked = false
            this.mViewDataBinding.both.isChecked = true
        }
        this.mViewDataBinding.shopStartTime.setOnClickListener {
            showTimePicker(true)
        }

       this. mViewDataBinding.shopEndTime.setOnClickListener {
            showTimePicker(false)
        }

        // ============================================================
        //                     BUTTON CLICK → GENERATE OTP
        // ============================================================
        this.mViewDataBinding.accConBtn.setOnClickListener {

            if (TextUtils.isEmpty(this.mViewDataBinding.restaurantname.text)) {
                showToast("Please enter your Restaurant name")
            } else if (TextUtils.isEmpty(this.mViewDataBinding.restaurantemail.text)) {
                showToast("Please enter your Restaurant Email")
            } else if (TextUtils.isEmpty(this.mViewDataBinding.restaurantmobilenon.text)) {
                showToast("Please enter your Restaurant Mobile Number")
            } else if (TextUtils.isEmpty(this.mViewDataBinding.password.text)) {
                showToast("Please enter your Restaurant Password")
            } else if (TextUtils.isEmpty(this.mViewDataBinding.restaurantStreet.text)) {
                showToast("Please enter your Restaurant Street")
            } else if (TextUtils.isEmpty(this.mViewDataBinding.restaurantCity.text)) {
                showToast("Please enter your Restaurant City")
            } else if (TextUtils.isEmpty(this.mViewDataBinding.restaurantPinCode.text)) {
                showToast("Please enter your Restaurant Pin code")
            } else if (TextUtils.isEmpty(this.mViewDataBinding.restaurantLandMark.text)) {
                showToast("Please enter your Restaurant LandMark")
            } else if (TextUtils.isEmpty(this.mViewDataBinding.tradeId.text)) {
                showToast("Please enter your Restaurant Trade Id")
            }else  if (shopStartTime.isEmpty()) {
                showToast("Please select shop start time")
            }

           else if (shopEndTime.isEmpty()) {
                showToast("Please select shop end time")
            }
            else {
                // Generate OTP
                generatedOtp = (100000..999999).random().toString()

                showOtpNotification(generatedOtp.toInt())    // 🔔 Show notification
                showOtpDialog()
            }


        }

        // IMAGE PICKER
        this.mViewDataBinding.accProfileEdit.setOnClickListener {
            ImagePicker.with(requireActivity()).cropSquare().createIntent {
                startForProfileImageResult.launch(it)
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_shopcreate





    // ============================================================
    //                    LOCAL OTP NOTIFICATION
    // ============================================================
    private fun showOtpNotification(otp: Int) {

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_logo)

        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle("OTP Verification")
            .setContentText("Your OTP is: $otp")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setLargeIcon(bitmap)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(requireContext())) {

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                notify(NOTIFICATION_ID, builder.build())
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_REQUEST_CODE
                )
            }
        }
    }


    // ============================================================
    //            CREATE NOTIFICATION CHANNEL (OREO+)
    // ============================================================
    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Manual OTP Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Channel for manual OTP notifications"

            val manager =
                requireContext().getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }
    }


    // ============================================================
    //                        OTP DIALOG
    // ============================================================
    @SuppressLint("SetTextI18n")
    private fun showOtpDialog() {

        dialog = Dialog(activitys)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.otp_popup)

        val otpText = dialog.findViewById<TextView>(R.id.otp_details)
        val otpView = dialog.findViewById<OtpView>(R.id.otp)
        val backBtn = dialog.findViewById<ImageView>(R.id.backBtn)
        val submit = dialog.findViewById<Button>(R.id.submit_otp)

        otpText.text =
            "Enter OTP sent to ${mViewDataBinding.restaurantmobilenon.text.toString()}"

        backBtn.setOnClickListener {
            dialog.dismiss()
        }

        submit.setOnClickListener {
            val userOtp = otpView.text.toString()
showLoader()
            if (userOtp != generatedOtp) {
                hideLoader()
                showToast("Invalid OTP")
            } else {
                dialog.dismiss()
                registerToFirebaseAuth()
            }
        }

        dialog.show()
    }


    // ============================================================
    //              FIREBASE AUTH CREATE USER
    // ============================================================
    private fun registerToFirebaseAuth() {

        val email = mViewDataBinding.restaurantemail.text.toString()
        val password = mViewDataBinding.password.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                uploadImageToStorage()
            }
            .addOnFailureListener {
                hideLoader()
                showToast("Auth error: ${it.message}")
            }
    }


    // ============================================================
    //              SAVE SHOP IN FIRESTORE
    // ============================================================
    private fun saveShopToFirestore(imageUrl: String) {

        val map = HashMap<String, Any>()

        map["restaurantName"] = mViewDataBinding.restaurantname.text.toString()
        map["restaurantEmail"] = mViewDataBinding.restaurantemail.text.toString()
        map["mobileNumber"] = mViewDataBinding.restaurantmobilenon.text.toString()
        map["restaurantStreet"] = mViewDataBinding.restaurantStreet.text.toString()
        map["restaurantCity"] = mViewDataBinding.restaurantCity.text.toString()
        map["restaurantPinCode"] = mViewDataBinding.restaurantPinCode.text.toString()
        map["restaurantLandMark"] = mViewDataBinding.restaurantLandMark.text.toString()
        map["tradeId"] = mViewDataBinding.tradeId.text.toString()
        map["restaurantDescreption"] = mViewDataBinding.restaurantDescreption.text.toString()
        map["restaurantType"] = categorytype
        map["restaurantLat"] = latitute.toString()
        map["restaurantLng"] = longitute.toString()
        map["mode"] = mode
        map["preorder"] = preorder
        map["gstapplicable"] = gstapplicable
        map["otp"] = generatedOtp
        map["verify"] = false
        map["shopStartTime"] = shopStartTime
        map["shopEndTime"] = shopEndTime
        // ✅ ADD CREATED AT (SERVER TIME)
        map["createdAt"] = FieldValue.serverTimestamp()

// Optional but useful
        map["updatedAt"] = FieldValue.serverTimestamp()
        map["profileImage"] = imageUrl       // 👈 Store URL
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        db.collection("shops")
            .document(uid)   // <<-- IMPORTANT
            .set(map)
            .addOnSuccessListener {
                hideLoader()
                loadFragment(LoginFragment(), android.R.id.content, "login", false)
                showToast("Shop Created Successfully")
            }
            .addOnFailureListener {
                hideLoader()
                showToast("Error saving shop: ${it.message}")
            }
    }



    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data!!
                this.mViewDataBinding.accProfile.setImageURI(uri)
                imagePath = uri.toString()
            }
        }

    private fun uploadImageToStorage() {

        if (imagePath.isEmpty()) {
            showToast("please select image")
            return
        }

        val fileUri = android.net.Uri.parse(imagePath)

        val fileName = "shops/${System.currentTimeMillis()}.jpg"
        val imageRef = storage.child(fileName)

        imageRef.putFile(fileUri)
            .addOnSuccessListener {

                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    saveShopToFirestore(downloadUrl.toString())  // 👈 send URL
                }

            }.addOnFailureListener {
                hideLoader()
                showToast("Image upload failed: ${it.message}")
            }
    }

    // ============================================================
    //               VALIDATION
    // ============================================================


    // ============================================================
    //              EVENTBUS → LOCATION UPDATE
    // ============================================================
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFoodEvent(event: LocationEvent) {

        mViewDataBinding.restaurantLandMark.text = event.landmark
        val cleaned =
            event.latlonng.replace("lat/lng: (", "").replace(")", "")
        val parts = cleaned.split(",")
        latitute = parts[0]
        longitute = parts[1]
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
    @SuppressLint("SetTextI18n")
    private fun showTimePicker(isStart: Boolean) {

        val calendar = java.util.Calendar.getInstance()
        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = calendar.get(java.util.Calendar.MINUTE)

        val dialog = android.app.TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->

                val formatted =
                    String.format("%02d:%02d", selectedHour, selectedMinute)

                if (isStart) {
                    shopStartTime = formatted
                    mViewDataBinding.shopStartTime.text = "Start Time : $formatted"
                } else {
                    shopEndTime = formatted
                    mViewDataBinding.shopEndTime.text = "End Time : $formatted"
                }
            },
            hour,
            minute,
            true
        )

        dialog.show()
    }

}
