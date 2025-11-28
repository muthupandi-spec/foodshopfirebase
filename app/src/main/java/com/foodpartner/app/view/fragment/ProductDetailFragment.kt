package com.foodpartner.app.view.fragment

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.ViewDataBinding
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.ProductdetailfragmentBinding
import com.foodpartner.app.network.Constant
import com.foodpartner.app.view.bottomsheetfragment.ScheduleBottomsheetfragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ProductDetailFragment : BaseFragment<ProductdetailfragmentBinding>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private var imageUri: Uri? = null
    private var selectedCategoryId: String? = null
    private var stockStatus: String = ""
    private var type: String = ""

    private var addCategoryDialog: AlertDialog? = null
    private val categoryList = ArrayList<String>()
    private lateinit var categoryAdapter: ArrayAdapter<String>

    override fun getLayoutId(): Int = R.layout.productdetailfragment

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        setupUI()
        initCategorySpinner()
        loadCategoriesFromFirestore()
    }

    private fun setupUI() {
        this.mViewDataBinding.apply {

            // Image picker
            foodimg.setOnClickListener {
                ImagePicker.with(requireActivity())
                    .cropSquare()
                    .compress(512)
                    .createIntent { intent ->
                        startForImageResult.launch(intent)
                    }
            }

            // Veg / Nonveg
            veg.setOnClickListener { type = "veg" }
            nonveg.setOnClickListener { type = "nonveg" }

            // Stock
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                stockStatus = when (checkedId) {
                    R.id.instock -> "1"
                    R.id.outstock -> "0"
                    else -> ""
                }
            }

            // Start/End time
            starttime.setOnClickListener {
                Constant.scheduletime = "starttime"
                ScheduleBottomsheetfragment("opentime")
                    .show(childFragmentManager, "schedule")
            }

            endtime.setOnClickListener {
                Constant.scheduletime = "endtime"
                ScheduleBottomsheetfragment("closetime")
                    .show(childFragmentManager, "schedule")
            }

            // Save button
            save.setOnClickListener { validateInputs() }

            // Back
            backBtn.setOnClickListener { requireActivity().onBackPressed() }
        }
    }

    // Image picker result
    private val startForImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val uri = result.data?.data
                imageUri = uri
                mViewDataBinding.foodimg.setImageURI(uri)
            } else {
                showToast("Image selection cancelled")
            }
        }

    private fun validateInputs() {

        val name = mViewDataBinding.foodname.text.toString().trim()
        val price = mViewDataBinding.price.text.toString().trim()
        val description = mViewDataBinding.description.text.toString().trim()
        val brief = mViewDataBinding.briefdescription.text.toString().trim()
        val startTime = mViewDataBinding.starttime.text.toString().trim()
        val endTime = mViewDataBinding.endtime.text.toString().trim()

        when {
            name.isEmpty() -> { showToast("Enter food name"); return }
            price.isEmpty() -> { showToast("Enter price"); return }
            type.isEmpty() -> { showToast("Select veg/non-veg"); return }
            description.isEmpty() -> { showToast("Enter description"); return }
            brief.isEmpty() -> { showToast("Enter brief description"); return }
            selectedCategoryId.isNullOrEmpty() -> { showToast("Select category"); return }
        }

        if (imageUri == null) {
            showToast("Please select an image")
            return
        }

        showLoader()
        uploadImageThenSave()
    }

    private fun uploadImageThenSave() {
        val uid = auth.currentUser?.uid ?: return showToast("User missing")

        // 🔥 FIX: reduce Firebase upload retry timeout to 5 seconds
        storage.maxUploadRetryTimeMillis = 5000

        val fileName = "${System.currentTimeMillis()}.jpg"
        val ref = storage.getReference("shops/$uid/foods/$fileName")

        ref.putFile(imageUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { url ->
                    saveFood(url.toString())
                }
            }
            .addOnFailureListener {
                hideLoader()
                showToast("Upload failed: ${it.message}")
            }
    }

    private fun saveFood(imageUrl: String) {
        val uid = auth.currentUser?.uid ?: return showToast("User missing")

        val foodData = hashMapOf(
            "foodName" to mViewDataBinding.foodname.text.toString().trim(),
            "price" to mViewDataBinding.price.text.toString().trim(),
            "description" to mViewDataBinding.description.text.toString().trim(),
            "briefDescription" to mViewDataBinding.briefdescription.text.toString().trim(),
            "type" to type,
            "stockStatus" to stockStatus,
            "categoryId" to selectedCategoryId,
            "imageUrl" to imageUrl,
            "createdAt" to System.currentTimeMillis(),
            "isActive" to true
        )

        firestore.collection("shops")
            .document(uid)
            .collection("foods")
            .document()
            .set(foodData)
            .addOnSuccessListener {
                hideLoader()
                showToast("Food added successfully")
                requireActivity().onBackPressed()
            }
            .addOnFailureListener {
                hideLoader()
                showToast("Error: ${it.message}")
            }
    }

    // Load categories to spinner
    private fun loadCategoriesFromFirestore() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("shops")
            .document(uid)
            .collection("categories")
            .orderBy("createdAt")
            .get()
            .addOnSuccessListener { query ->
                categoryList.clear()
                categoryList.add("Select Category")

                for (doc in query.documents) {
                    categoryList.add(doc.getString("restaurantCatagory") ?: "")
                }

                categoryList.add("Add New Category")

                categoryAdapter.notifyDataSetChanged()
            }
    }

    private fun initCategorySpinner() {
        categoryAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categoryList
        )

        mViewDataBinding.shopcatspinner.adapter = categoryAdapter

        mViewDataBinding.shopcatspinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selected = categoryList[position]

                    when (selected) {
                        "Select Category" -> selectedCategoryId = null
                        "Add New Category" -> showAddCategoryDialog()
                        else -> fetchCategoryIdByName(selected)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun fetchCategoryIdByName(name: String) {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("shops")
            .document(uid)
            .collection("categories")
            .whereEqualTo("restaurantCatagory", name)
            .get()
            .addOnSuccessListener { query ->
                if (!query.isEmpty) {
                    selectedCategoryId = query.documents[0].id
                }
            }
    }

    private fun showAddCategoryDialog() {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_category, null)

        val etCategory = view.findViewById<EditText>(R.id.etCategory)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(view)
        builder.setCancelable(true)

        addCategoryDialog = builder.create()
        addCategoryDialog?.show()

        btnSubmit.setOnClickListener {
            val categoryName = etCategory.text.toString().trim()
            if (categoryName.isEmpty()) {
                showToast("Enter category name")
                return@setOnClickListener
            }

            addCategoryDialog?.dismiss()
            saveCategory(categoryName)
        }
    }

    private fun saveCategory(categoryName: String) {
        val uid = auth.currentUser?.uid ?: return
        showLoader()

        val ref = firestore.collection("shops")
            .document(uid)
            .collection("categories")
            .document()

        val data = hashMapOf(
            "restaurantCatagoryId" to ref.id,
            "restaurantCatagory" to categoryName,
            "createdAt" to System.currentTimeMillis()
        )

        ref.set(data)
            .addOnSuccessListener {
                hideLoader()
                showToast("Category added")
                loadCategoriesFromFirestore()
            }
            .addOnFailureListener {
                hideLoader()
                showToast("Error: ${it.message}")
            }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onTimeEvent(model: com.foodpartner.app.view.eventmodel.Timepickermodel) {
        if (Constant.scheduletime == "starttime")
            mViewDataBinding.starttime.text = model.starttime
        else
            mViewDataBinding.endtime.text = model.endtime

        EventBus.getDefault().removeStickyEvent(model)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
}
