package com.foodpartner.app.view.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.ProducteditdetailfragmentBinding
import com.foodpartner.app.network.Constant
import com.foodpartner.app.view.bottomsheetfragment.ScheduleBottomsheetfragment
import com.foodpartner.app.view.eventmodel.FoodEditEvent
import com.foodpartner.app.view.eventmodel.Timepickermodel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ProductEditDetailFragment(private val foodId: String) :
    BaseFragment<ProducteditdetailfragmentBinding>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference
    private val auth = FirebaseAuth.getInstance()
    private var isSpinnerInitialized = false

    private var imageUri: Uri? = null
    private var existingImageUrl: String = ""
    private var selectedCategoryId: String? = null
    private var selectedCategoryName: String? = null

    private var itemStatus: String = "1"
    private var type: String = "veg"
    private var openCloseTime: String? = null

    private var categories = mutableListOf<String>()
    private var categoryDocs = mutableListOf<com.google.firebase.firestore.DocumentSnapshot>()

    private var addCategoryDialog: AlertDialog? = null

    private val imagePickerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val data = result.data
                val uri = data?.data
                if (uri != null) {
                    imageUri = uri
                    mViewDataBinding.foodimg.setImageURI(uri)
                }
            } else if (result.resultCode == ImagePicker.RESULT_ERROR) {
                showToast("Image pick error: ${ImagePicker.getError(result.data)}")
            } else {
                showToast("Image selection cancelled")
            }
        }

    override fun getLayoutId(): Int = R.layout.producteditdetailfragment

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        setupUi()
        loadFoodDetails()       // Load food first to get selectedCategoryId
        loadCategoriesFromFirestore()
    }

    private fun setupUi() {
        mViewDataBinding.apply {
            backBtn.setOnClickListener { requireActivity().supportFragmentManager.popBackStackImmediate() }

            foodimg.setOnClickListener {
                ImagePicker.with(activitys).cropSquare().createIntent { intent ->
                    imagePickerResult.launch(intent)
                }
            }

            veg.setOnClickListener { type = "veg" }
            nonveg.setOnClickListener { type = "nonveg" }

            instock.setOnClickListener { itemStatus = "1" }
            outstock.setOnClickListener { itemStatus = "0" }

            starttime.setOnClickListener {
                openCloseTime = "opentime"
                ScheduleBottomsheetfragment("opentime").show(childFragmentManager, "schedule")
            }
            endtime.setOnClickListener {
                openCloseTime = "closetime"
                ScheduleBottomsheetfragment("closetime").show(childFragmentManager, "schedule")
            }

            categories.clear()
            categories.add("Add Category")
            val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            shopcatspinner.adapter = spinnerAdapter

            shopcatspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    hideLoader()
                    if (!isSpinnerInitialized) {
                        isSpinnerInitialized = true
                        return
                    }

                    val selected = categories[position]
                    if (selected == "Add Category") {
                        showAddCategoryDialog()
                    } else if (position - 1 >= 0 && position - 1 < categoryDocs.size) {
                        val doc = categoryDocs[position - 1]
                        selectedCategoryId = doc.id
                        selectedCategoryName = doc.getString("restaurantCatagory") ?: ""
                        sharedHelper.putInUser("restaurantCatagoryId", selectedCategoryId ?: "")
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            save.setOnClickListener { onSaveClicked() }
        }
    }

    private fun loadCategoriesFromFirestore() {
        val uid = sharedHelper.getFromUser("userid")
        if (uid.isNullOrEmpty()) return

        println("print")

        firestore.collection("shops")
            .document(uid)
            .collection("categories")
            .orderBy("createdAt")
            .get()
            .addOnSuccessListener { query ->
                hideLoader()
                categoryDocs.clear()
                categories.clear()
                categories.add("Add Category")

                for (doc in query.documents) {
                    categoryDocs.add(doc)
                    val name = doc.getString("restaurantCatagory") ?: ""
                    categories.add(name)
                }

                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                mViewDataBinding.shopcatspinner.adapter = adapter

                selectCategoryInSpinner(selectedCategoryId)
            }
            .addOnFailureListener { e ->
                hideLoader()
                showToast("Failed to load categories: ${e.message}")
            }
    }

    private fun selectCategoryInSpinner(categoryId: String?) {

        if (categoryId.isNullOrEmpty() || categoryDocs.isEmpty()) return
        val index = categoryDocs.indexOfFirst { it.id == categoryId }
        if (index >= 0) {
            mViewDataBinding.shopcatspinner.setSelection(index + 1)
        }
    }

    private fun showAddCategoryDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val v = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_category, null)
        val et = v.findViewById<EditText>(R.id.etCategory)
        val btn = v.findViewById<Button>(R.id.btnSubmit)
        builder.setView(v)
        builder.setCancelable(true)
        addCategoryDialog = builder.create()
        addCategoryDialog?.show()

        btn.setOnClickListener {
            val cat = et.text.toString().trim()
            if (cat.isEmpty()) {
                showToast("Please add the category")
                return@setOnClickListener
            }
            addCategoryDialog?.dismiss()
            saveCategoryToFirestore(cat)
        }
    }

    private fun saveCategoryToFirestore(categoryName: String) {
        val uid = sharedHelper.getFromUser("userid") ?: return showToast("User not logged in")
        showLoader()
        println("print1")
        val newDoc = firestore.collection("shops").document(uid).collection("categories").document()
        val data = hashMapOf(
            "restaurantCatagoryId" to newDoc.id,
            "restaurantCatagory" to categoryName,
            "createdAt" to System.currentTimeMillis()
        )

        newDoc.set(data)
            .addOnSuccessListener {
                hideLoader()
                showToast("Category added")
                loadCategoriesFromFirestore()
            }
            .addOnFailureListener { e ->
                hideLoader()
                showToast("Failed to add category: ${e.message}")
            }
    }

    private fun loadFoodDetails() {
        val uid = sharedHelper.getFromUser("userid") ?: return showToast("User not logged in")
        println("print2")

        firestore.collection("shops")
            .document(uid)
            .collection("foods")
            .document(foodId)
            .get()
            .addOnSuccessListener { doc ->
                hideLoader()
                if (doc != null && doc.exists()) {
                    mViewDataBinding.foodname.setText(doc.getString("foodName") ?: "")
                    mViewDataBinding.price.setText(doc.getString("price") ?: "")
                    mViewDataBinding.description.setText(doc.getString("description") ?: "")
                    mViewDataBinding.briefdescription.setText(doc.getString("briefDescription") ?: "")
                    mViewDataBinding.starttime.text = doc.getString("startTime") ?: ""
                    mViewDataBinding.endtime.text = doc.getString("endTime") ?: ""
                    existingImageUrl = doc.getString("imageUrl") ?: ""
                    type = doc.getString("type") ?: "veg"
                    itemStatus = doc.getString("status") ?: "1"
                    selectedCategoryId = doc.getString("categoryId")

                    if (type == "veg") {
                        mViewDataBinding.veg.isChecked = true
                        mViewDataBinding.nonveg.isChecked = false
                    } else {
                        mViewDataBinding.veg.isChecked = false
                        mViewDataBinding.nonveg.isChecked = true
                    }

                    if (itemStatus == "1") {
                        mViewDataBinding.instock.isChecked = true
                        mViewDataBinding.outstock.isChecked = false
                    } else {
                        mViewDataBinding.instock.isChecked = false
                        mViewDataBinding.outstock.isChecked = true
                    }

                    if (!existingImageUrl.isNullOrEmpty()) {
                        Glide.with(this).load(existingImageUrl).into(mViewDataBinding.foodimg)
                    }
                } else {
                    showToast("Food not found")
                }
            }
            .addOnFailureListener { e ->
                hideLoader()
                showToast("Failed to load food: ${e.message}")
            }
    }

    private fun onSaveClicked() {
        val name = mViewDataBinding.foodname.text.toString().trim()
        val price = mViewDataBinding.price.text.toString().trim()
        val description = mViewDataBinding.description.text.toString().trim()
        val brief = mViewDataBinding.briefdescription.text.toString().trim()
        val start = mViewDataBinding.starttime.text.toString().trim()
        val end = mViewDataBinding.endtime.text.toString().trim()
        val uid = sharedHelper.getFromUser("userid") ?: return showToast("User not logged in")

        if (name.isEmpty()) { showToast("please enter your food name"); return }
        if (price.isEmpty()) { showToast("please enter your food price"); return }
        if (type.isEmpty()) { showToast("please select veg or non veg"); return }
        if (description.isEmpty()) { showToast("please enter description"); return }
        if (brief.isEmpty()) { showToast("please enter brief description"); return }
        if (selectedCategoryId.isNullOrEmpty()) { showToast("please select a category"); return }

        showLoader()
        println("print3")

        if (imageUri != null) {
            uploadImageToStorage(uid, imageUri!!) { imageUrl ->
                updateFoodDocument(uid, imageUrl, name, price, description, brief, start, end)
            }
        } else {
            updateFoodDocument(uid, existingImageUrl, name, price, description, brief, start, end)
        }
    }

    private fun uploadImageToStorage(uid: String, uri: Uri, onComplete: (String) -> Unit) {
        val path = "shops/$uid/foods/${System.currentTimeMillis()}.jpg"
        val ref = storageRef.child(path)

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

    private fun updateFoodDocument(
        uid: String,
        imageUrl: String,
        name: String,
        price: String,
        description: String,
        brief: String,
        start: String,
        end: String
    ) {
        val docRef = firestore.collection("shops").document(uid).collection("foods").document(foodId)
        val data = hashMapOf<String, Any>(
            "foodName" to name,
            "price" to price,
            "description" to description,
            "briefDescription" to brief,
            "type" to type,
            "status" to itemStatus,
            "startTime" to start,
            "endTime" to end,
            "categoryId" to selectedCategoryId.orEmpty(),
            "imageUrl" to imageUrl,
            "updatedAt" to System.currentTimeMillis()
        )

        docRef.update(data)
            .addOnSuccessListener {
                hideLoader()
                showToast("Food updated successfully")
                EventBus.getDefault().postSticky(FoodEditEvent("edit"))
                requireActivity().supportFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                docRef.set(data)
                    .addOnSuccessListener {
                        hideLoader()
                        showToast("Food updated successfully")
                        EventBus.getDefault().postSticky(FoodEditEvent("edit"))
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                    .addOnFailureListener { e ->
                        hideLoader()
                        showToast("Failed to update food: ${e.message}")
                    }
            }
    }

    @SuppressLint("DefaultLocale")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onTimeEvent(model: Timepickermodel) {
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

}
