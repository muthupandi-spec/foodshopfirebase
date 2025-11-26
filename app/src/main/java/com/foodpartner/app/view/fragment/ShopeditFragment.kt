package com.foodpartner.app.view.fragment

import android.app.Activity
import android.app.Dialog
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.ViewDataBinding
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentshopeditBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.mukesh.OtpView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ShopeditFragment : BaseFragment<FragmentshopeditBinding>() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    private var imagePath: String = ""
    private var preorder: String = "yes"
    private var categorytype: String = "nonveg"
    private var mode: String = "active"
    private var gstapplicable: String = ""
    private var latitute: String? = null
    private var longitute: String? = null

    lateinit var dialog: Dialog

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        val binding = mViewDataBinding as FragmentshopeditBinding

        val uid = sharedHelper.getFromUser("userid") ?: return

// Fetch shop data from Firestore
        db.collection("shops").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    binding.restaurantname.setText(document.getString("restaurantName"))
                    binding.restaurantemail.setText(document.getString("restaurantEmail"))
                    binding.restaurantmobilenon.setText(document.getString("mobileNumber"))
                    binding.password.setText(document.getString("password"))
                    binding.restaurantStreet.setText(document.getString("restaurantStreet"))
                    binding.restaurantCity.setText(document.getString("restaurantCity"))
                    binding.restaurantPinCode.setText(document.getString("restaurantPinCode"))
                    binding.restaurantLandMark.setText(document.getString("restaurantLandMark"))
                    binding.tradeId.setText(document.getString("tradeId"))
                    binding.restaurantDescreption.setText(document.getString("restaurantDescreption"))

                    val type = document.getString("restaurantType") ?: "nonveg"
                    categorytype = type
                    binding.veg.isChecked = type == "veg"
                    binding.nonveg.isChecked = type == "nonveg"
                    binding.both.isChecked = type == "both"

                    mode = document.getString("mode") ?: "active"
                    binding.active.isChecked = mode == "active"
                    binding.inactive.isChecked = mode == "inactive"

                    preorder = document.getString("preorder") ?: "yes"
                    binding.preorderyes.isChecked = preorder == "yes"
                    binding.preorderno.isChecked = preorder == "no"

                    gstapplicable = document.getString("gstapplicable") ?: "no"
                    binding.gstapplicalbleyes.isChecked = gstapplicable == "yes"
                    binding.gstapplicalbleno.isChecked = gstapplicable == "no"

                    val imageUrl = document.getString("profileImage")
                    if (!imageUrl.isNullOrEmpty()) {
                        // Load image using Glide or any image loader
                        Glide.with(requireContext()).load(imageUrl).into(binding.accProfile)
                    }
                }
            }
            .addOnFailureListener { e ->
                showToast("Failed to fetch shop data: ${e.message}")
            }

// Existing toggle click listeners and update button
        setupToggleListeners(binding)
        binding.backBtn.setOnClickListener { fragmentManagers?.popBackStack() }
        binding.accProfileEdit.setOnClickListener {
            ImagePicker.with(requireActivity()).cropSquare().createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }
        }
        binding.accConBtn.setOnClickListener { updateShopData(binding) }

    }

    // Extract toggle listeners to a separate function for clarity
    private fun setupToggleListeners(binding: FragmentshopeditBinding) {
        binding.preorderyes.setOnClickListener {
            preorder = "yes"
            binding.preorderyes.isChecked = true
            binding.preorderno.isChecked = false
        }
        binding.preorderno.setOnClickListener {
            preorder = "no"
            binding.preorderno.isChecked = true
            binding.preorderyes.isChecked = false
        }
        binding.active.setOnClickListener {
            mode = "active"
            binding.active.isChecked = true
            binding.inactive.isChecked = false
        }
        binding.inactive.setOnClickListener {
            mode = "inactive"
            binding.inactive.isChecked = true
            binding.active.isChecked = false
        }
        binding.gstapplicalbleyes.setOnClickListener {
            gstapplicable = "yes"
            binding.gstapplicalbleyes.isChecked = true
            binding.gstapplicalbleno.isChecked = false
        }
        binding.gstapplicalbleno.setOnClickListener {
            gstapplicable = "no"
            binding.gstapplicalbleno.isChecked = true
            binding.gstapplicalbleyes.isChecked = false
        }
        binding.veg.setOnClickListener {
            categorytype = "veg"
            binding.veg.isChecked = true
            binding.nonveg.isChecked = false
            binding.both.isChecked = false
        }
        binding.nonveg.setOnClickListener {
            categorytype = "nonveg"
            binding.nonveg.isChecked = true
            binding.veg.isChecked = false
            binding.both.isChecked = false
        }
        binding.both.setOnClickListener {
            categorytype = "both"
            binding.both.isChecked = true
            binding.veg.isChecked = false
            binding.nonveg.isChecked = false
        }
    }

    private fun updateShopData(binding: FragmentshopeditBinding) {

        if (TextUtils.isEmpty(binding.restaurantname.text)) { showToast("Please enter your Restaurant name"); return }
        if (TextUtils.isEmpty(binding.restaurantemail.text)) { showToast("Please enter your Restaurant Email"); return }
        if (TextUtils.isEmpty(binding.restaurantmobilenon.text)) { showToast("Please enter your Restaurant Mobile Number"); return }
        if (TextUtils.isEmpty(binding.password.text)) { showToast("Please enter your Restaurant Password"); return }
        if (TextUtils.isEmpty(binding.restaurantStreet.text)) { showToast("Please enter your Restaurant Street"); return }
        if (TextUtils.isEmpty(binding.restaurantCity.text)) { showToast("Please enter your Restaurant City"); return }
        if (TextUtils.isEmpty(binding.restaurantPinCode.text)) { showToast("Please enter your Restaurant Pin code"); return }
        if (TextUtils.isEmpty(binding.restaurantLandMark.text)) { showToast("Please enter your Restaurant LandMark"); return }
        if (TextUtils.isEmpty(binding.tradeId.text)) { showToast("Please enter your Restaurant Trade Id"); return }

        binding.loader.visibility = View.VISIBLE

        if (imagePath.isNotEmpty()) {
            uploadImageToStorageAndSave(binding)
        } else {
            saveShopToFirestore(binding, sharedHelper.getFromUser("resimage"))
        }
    }

    private fun uploadImageToStorageAndSave(binding: FragmentshopeditBinding) {
        val fileUri = Uri.parse(imagePath)
        val fileName = "shops/${System.currentTimeMillis()}.jpg"
        val imageRef = storage.child(fileName)

        imageRef.putFile(fileUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    saveShopToFirestore(binding, downloadUrl.toString())
                }
            }
            .addOnFailureListener {
                binding.loader.visibility = View.GONE
                showToast("Image upload failed: ${it.message}")
            }
    }

    private fun saveShopToFirestore(binding: FragmentshopeditBinding, imageUrl: String?) {
        val uid = sharedHelper.getFromUser("userid") ?: return
        val map = hashMapOf<String, Any>(
            "restaurantName" to binding.restaurantname.text.toString(),
            "restaurantEmail" to binding.restaurantemail.text.toString(),
            "mobileNumber" to binding.restaurantmobilenon.text.toString(),
            "password" to binding.password.text.toString(),
            "restaurantStreet" to binding.restaurantStreet.text.toString(),
            "restaurantCity" to binding.restaurantCity.text.toString(),
            "restaurantPinCode" to binding.restaurantPinCode.text.toString(),
            "restaurantLandMark" to binding.restaurantLandMark.text.toString(),
            "tradeId" to binding.tradeId.text.toString(),
            "restaurantDescreption" to binding.restaurantDescreption.text.toString(),
            "restaurantType" to categorytype,
            "mode" to mode,
            "preorder" to preorder,
            "gstapplicable" to gstapplicable,
            "profileImage" to (imageUrl ?: "")
        )

        db.collection("shops").document(uid)
            .update(map)
            .addOnSuccessListener {
                binding.loader.visibility = View.GONE
                showToast("Shop updated successfully")
                fragmentManagers!!.popBackStackImmediate()
                // Save locally
                sharedHelper.putInUser("resname", binding.restaurantname.text.toString())
                sharedHelper.putInUser("resemail", binding.restaurantemail.text.toString())
                sharedHelper.putInUser("resmobno", binding.restaurantmobilenon.text.toString())
                sharedHelper.putInUser("resstreet", binding.restaurantStreet.text.toString())
                sharedHelper.putInUser("rescity", binding.restaurantCity.text.toString())
                sharedHelper.putInUser("respincode", binding.restaurantPinCode.text.toString())
                sharedHelper.putInUser("reslandmark", binding.restaurantLandMark.text.toString())
                sharedHelper.putInUser("restradeid", binding.tradeId.text.toString())
                sharedHelper.putInUser("resdesc", binding.restaurantDescreption.text.toString())
                sharedHelper.putInUser("resimage", imageUrl ?: "")
            }
            .addOnFailureListener {
                // If doc doesn't exist, create it
                db.collection("shops").document(uid)
                    .set(map)
                    .addOnSuccessListener {
                        binding.loader.visibility = View.GONE
                        showToast("Shop updated successfully")
                    }
                    .addOnFailureListener { ex ->
                        binding.loader.visibility = View.GONE
                        showToast("Failed to update shop: ${ex.message}")
                    }
            }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = result.data?.data!!
                    imagePath = fileUri.toString()
                    mViewDataBinding.accProfile.setImageURI(fileUri)
                }
                ImagePicker.RESULT_ERROR -> showToast("Image Picker Error: ${ImagePicker.getError(result.data)}")
                else -> showToast("Image selection cancelled")
            }
        }

    override fun getLayoutId(): Int = R.layout.fragmentshopedit

}
