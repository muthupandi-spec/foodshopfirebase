package com.foodpartner.app.view.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.BitmapFactory
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
import com.mukesh.OtpView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.*

class ProductDetailFragment : BaseFragment<ProductdetailfragmentBinding>() {
    var stockStatus = ""
    var type = ""

    // Firebase
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    // UI / state
    private var imageUri: Uri? = null
    private var uploadedImageUrl: String = ""
    private var selectedCategoryId: String? = null
    private var selectedCategoryName: String? = null

    // status: "add" or "edit"
    private var status: String = "add"
    private var editingFoodId: String? = null

    // local lists
    private val categoryNames = ArrayList<String>()
    private val categoryDocs = ArrayList<QueryDocumentSnapshot>()

    // Dialog references
    private var addCategoryDialog: AlertDialog? = null
    private val categoryList = ArrayList<String>()
    private lateinit var categoryAdapter: ArrayAdapter<String>
    // Image picker result
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    imageUri = uri
                    this.mViewDataBinding.foodimg.setImageURI(uri)
                }
            } else if (result.resultCode == ImagePicker.RESULT_ERROR) {
                showToast("ImagePicker error: ${ImagePicker.getError(result.data)}")
            } else {
                showToast("Image selection cancelled")
            }
        }

    override fun getLayoutId(): Int = R.layout.productdetailfragment

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        // Initialize spinner and listeners
        setupUi()
        initCategorySpinner()
        loadCategoriesFromFirestore()
    }

    private fun setupUi() {
        this.mViewDataBinding.apply {

            // Image click -> pick image
            foodimg.setOnClickListener {
                ImagePicker.with(requireActivity()).cropSquare().createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
            }

            // Veg / Nonveg toggles (simple)
            veg.setOnClickListener { type = "veg" }
            nonveg.setOnClickListener {type = "nonveg" }

            // Stock toggles

            mViewDataBinding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
                stockStatus = when (checkedId) {
                    R.id.instock -> "1"
                    R.id.outstock -> "0"
                    else -> ""
                }
            }


            // Start / End time will open your existing bottom sheet (keeps same behavior)
            starttime.setOnClickListener {
                Constant.scheduletime = "starttime"
                val bs = ScheduleBottomsheetfragment("opentime")
                bs.show(childFragmentManager, "schedule")
            }
            endtime.setOnClickListener {
                Constant.scheduletime = "endtime"
                val bs = ScheduleBottomsheetfragment("closetime")
                bs.show(childFragmentManager, "schedule")
            }

            // Add Category manually button (if present)


            // Save button
            save.setOnClickListener {
                onSaveClick()
            }

            // Back
            backBtn.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
        }
    }

    // Validate inputs and save (create or update)
    private fun onSaveClick() {
        val foodName = mViewDataBinding.foodname.text.toString().trim()
        val price = mViewDataBinding.price.text.toString().trim()
        val description = mViewDataBinding.description.text.toString().trim()
        val brief = mViewDataBinding.briefdescription.text.toString().trim()
        val type = type
        val startTime = mViewDataBinding.starttime.text.toString().trim()
        val endTime = mViewDataBinding.endtime.text.toString().trim()

        if (foodName.isEmpty()) { showToast("Please enter your food name"); return }
        if (price.isEmpty()) { showToast("Please enter your food price"); return }
        if (type.isEmpty()) { showToast("Please select veg or non veg"); return }
        if (description.isEmpty()) { showToast("Please enter description"); return }
        if (brief.isEmpty()) { showToast("Please enter brief description"); return }
        if (selectedCategoryId.isNullOrEmpty()) { showToast("Please select a category"); return }

        showLoader()

        // Upload image first if there is one
        if (imageUri != null) {
            uploadImageThenSave(imageUri!!) { imageUrl ->
                uploadedImageUrl = imageUrl
                saveFoodToFirestore(
                    foodName,
                    price,
                    description,
                    brief,
                    type,
                    stockStatus,
                    selectedCategoryId!!,
                    uploadedImageUrl
                )
            }
        } else {
            // No image selected — proceed (imageUrl empty)
            saveFoodToFirestore(
                foodName,
                price,
                description,
                brief,
                type,
                stockStatus,
                selectedCategoryId!!,
                ""
            )
        }
    }

    // Upload image to Firebase Storage, then return download URL via callback
    private fun uploadImageThenSave(uri: Uri, onComplete: (String) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrEmpty()) {
            hideLoader()
            showToast("User not logged in")
            return
        }

        val fileName = "shops/$uid/foods/${System.currentTimeMillis()}.jpg"
        val ref = storage.child(fileName)

        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    onComplete(downloadUri.toString())
                }.addOnFailureListener { e ->
                    hideLoader()
                    showToast("Failed to get image URL: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                hideLoader()
                showToast("Image upload failed: ${e.message}")
            }
    }

    // Save food document under shops/{uid}/foods/{foodId}
    private fun saveFoodToFirestore(
        foodName: String,
        price: String,
        description: String,
        brief: String,
        type: String,
        stockStatus: String,

        categoryId: String,
        imageUrl: String
    ) {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrEmpty()) {
            hideLoader()
            showToast("User not logged in")
            return
        }

        val foodData = hashMapOf(
            "foodName" to foodName,
            "price" to price,
            "description" to description,
            "briefDescription" to brief,
            "type" to type,
          //  "status" to stockStatus,          // "1" or "0"

            "categoryId" to categoryId,
            "imageUrl" to imageUrl,
            "createdAt" to System.currentTimeMillis(),
            "isActive" to true
        )

        val foodsRef = firestore.collection("shops").document(uid).collection("foods")

        if (status == "add") {
            // create new food
            val newDoc = foodsRef.document() // auto id
            foodData["foodId"] = newDoc.id
            newDoc.set(foodData)
                .addOnSuccessListener {
                    hideLoader()
                    showToast("Food created successfully")
                    EventBus.getDefault().postSticky(com.foodpartner.app.view.eventmodel.FoodCreateEvent("create"))
                    requireActivity().supportFragmentManager.popBackStack()
                }
                .addOnFailureListener { e ->
                    hideLoader()
                    showToast("Failed to create food: ${e.message}")
                }
        } else {
            // update existing food
            if (editingFoodId.isNullOrEmpty()) {
                hideLoader()
                showToast("No food selected to update")
                return
            }
            foodsRef.document(editingFoodId!!)
                .update(foodData as Map<String, Any>)
                .addOnSuccessListener {
                    hideLoader()
                    showToast("Food updated successfully")
                    EventBus.getDefault().postSticky(com.foodpartner.app.view.eventmodel.FoodCreateEvent("update"))
                    requireActivity().supportFragmentManager.popBackStack()
                }
                .addOnFailureListener { e ->
                    hideLoader()
                    showToast("Failed to update food: ${e.message}")
                }
        }
    }

    // Load categories from shops/{uid}/categories and populate spinner
    private fun loadCategoriesFromFirestore() {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrEmpty()) {
            showToast("User not logged in")
            return
        }

        showLoader()

        firestore.collection("shops")
            .document(uid)
            .collection("categories")
            .orderBy("createdAt")
            .get()
            .addOnSuccessListener { query ->
                hideLoader()
                categoryNames.clear()
                categoryDocs.clear()

                for (doc in query.documents) {
                    categoryDocs.add(doc as QueryDocumentSnapshot)
                    val name = doc.getString("restaurantCatagory") ?: ""
                    categoryNames.add(name)
                }

                // Append Add Category option
                categoryNames.add("Add Category")

                // Build spinner adapter
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                mViewDataBinding.shopcatspinner.adapter = adapter

                mViewDataBinding.shopcatspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selected = categoryNames[position]
                        if (selected == "Add Category") {
                            // show dialog to add
                            showAddCategoryDialog()
                        } else {
                            // user selected an existing category
                            val doc = query.documents[position]
                            selectedCategoryId = doc.id
                            selectedCategoryName = doc.getString("restaurantCatagory")
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

            }
            .addOnFailureListener { e ->
                hideLoader()
                showToast("Failed to load categories: ${e.message}")
            }
    }

    // Show a dialog to add category and save to shops/{uid}/categories
    private fun showAddCategoryDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_category, null)
        val etCategory = view.findViewById<EditText>(R.id.etCategory)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)
        builder.setView(view)
        builder.setCancelable(true)
        addCategoryDialog = builder.create()
        addCategoryDialog?.show()

        btnSubmit.setOnClickListener {
            val category = etCategory.text.toString().trim()
            if (category.isEmpty()) {
                showToast("Please add the category")
                return@setOnClickListener
            }
            addCategoryDialog?.dismiss()
            saveCategoryToFirestore(category)
        }
    }

    // Save category
    private fun saveCategoryToFirestore(categoryName: String) {
        val uid = auth.currentUser?.uid ?: return showToast("User not logged in")
        showLoader()

        val catRef = firestore.collection("shops").document(uid).collection("categories")
        val newDoc = catRef.document()
        val data = hashMapOf(
            "restaurantCatagoryId" to newDoc.id,
            "restaurantCatagory" to categoryName,
            "shopid" to sharedHelper.getFromUser("userid"),
            "createdAt" to System.currentTimeMillis()
        )

        newDoc.set(data)
            .addOnSuccessListener {
                hideLoader()
                showToast("Category added")
                loadCategoriesFromFirestore() // reload spinner
            }
            .addOnFailureListener { e ->
                hideLoader()
                showToast("Failed to add category: ${e.message}")
            }
    }

    // If your bottomsheet posts start/end times via EventBus
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onTimeEvent(model: com.foodpartner.app.view.eventmodel.Timepickermodel) {
        if (Constant.scheduletime == "starttime" || Constant.scheduletime == "opentime") {
            mViewDataBinding.starttime.text = model.starttime
        } else if (Constant.scheduletime == "endtime" || Constant.scheduletime == "closetime") {
            mViewDataBinding.endtime.text = model.endtime
        }
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


    private fun initCategorySpinner() {

        categoryList.clear()

        categoryList.add("Select Category")
        categoryList.add("Add New Category") // <-- This will open dialog

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

                    if (selected == "Add New Category") {
                        showAddCategoryDialog()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

}
