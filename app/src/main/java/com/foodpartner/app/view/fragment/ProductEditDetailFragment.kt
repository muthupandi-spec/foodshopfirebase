package com.foodpartner.app.view.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.app.washeruser.repository.Status
import com.foodpartner.app.R
import com.foodpartner.app.ResponseMOdel.CreateFoodResponseModel
import com.foodpartner.app.ResponseMOdel.GetallCategoryResponseModel
import com.foodpartner.app.ResponseMOdel.UpdateFoodResponseModel
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.ProducteditdetailfragmentBinding
import com.foodpartner.app.network.Constant
import com.foodpartner.app.network.Response
import com.foodpartner.app.view.bottomsheetfragment.ScheduleBottomsheetfragment
import com.foodpartner.app.view.eventmodel.FoodEditEvent
import com.foodpartner.app.view.eventmodel.Timepickermodel
import com.foodpartner.app.view.requestmodel.CreateCategoryRequest
import com.foodpartner.app.view.requestmodel.RestaurantBo
import com.foodpartner.app.view.responsemodel.CreateCategoryResponseModel
import com.foodpartner.app.viewModel.HomeViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class ProductEditDetailFragment(var foodidd: String) :
    BaseFragment<ProducteditdetailfragmentBinding>() {
    private var filebody: MultipartBody.Part? = null
    private var openclosetime: String? = null
    var status: String? = "add"
    private var itemStatus: String? = null
    var vegg: String? = "veg"
    private val homeViewModel by viewModel<HomeViewModel>()
    private lateinit var categoryList: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var bottomSheetFragment: ScheduleBottomsheetfragment
    private lateinit var dialogBuilder: AlertDialog.Builder
    private var alertDialog: AlertDialog? = null


    override fun initView(mViewDataBinding: ViewDataBinding?) {
        this.mViewDataBinding.apply {
            homeViewModel.getallcategory(sharedHelper.getFromUser("userid"))
            showBase64Image(Constant.image, foodimg)
            foodname.setText(Constant.foodname.toString())
            price.setText(Constant.price.toString())
            description.setText(Constant.desc.toString())
            briefdescription.setText(Constant.desc1.toString())
            if (Constant.type.equals("veg")) {
                veg.isChecked = true
                nonveg.isChecked = false

            } else {
                veg.isChecked = false
                nonveg.isChecked = true
            }


            homeViewModel.response().observe(viewLifecycleOwner, Observer {
                processResponse(it)
            })
            veg.setOnClickListener {
                vegg = "veg"
            }
            nonveg.setOnClickListener {
                vegg = "nonveg"
            }

            instock.setOnClickListener {
                itemStatus = "1"
            }
            outstock.setOnClickListener {
                itemStatus = "0"
            }
            foodimg.setOnClickListener {
                ImagePicker.with(activitys).cropSquare().createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
            }
            backBtn.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
            starttime.setOnClickListener {
                openclosetime = "opentime"
                bottomSheetFragment = ScheduleBottomsheetfragment(openclosetime!!)
                bottomSheetFragment.show(childFragmentManager, "productdetailfragment")
            }
            endtime.setOnClickListener {
                openclosetime = "closetime"
                bottomSheetFragment = ScheduleBottomsheetfragment(openclosetime!!)
                bottomSheetFragment.show(childFragmentManager, "productdetailfragment")
            }
            categoryList = mutableListOf()
            categoryList.add("Add Category")
            adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            shopcatspinner.adapter = adapter
            shopcatspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    println("clickkkkkk" + categoryList[position])
                    val selectedItem = categoryList[position]
                    if (selectedItem == "Add Category") {
                        showAddCategoryDialog()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }



            save.setOnClickListener {
                if (TextUtils.isEmpty(foodname.text.toString())) {
                    showToast("please enter your food name")
                } else if (TextUtils.isEmpty(price.text.toString())) {
                    showToast("please enter your food price")
                } else if (vegg!!.isEmpty()) {
                    showToast("please select veg or non veg")
                } else if (TextUtils.isEmpty(description.text.toString())) {
                    showToast("please enter your food description ")
                } else if (TextUtils.isEmpty(briefdescription.text.toString())) {
                    showToast("please enter your food brief description ")
                }

                /*  else if (TextUtils.isEmpty(sellingprice.text.toString())) {
                      showToast("please enter your food selling price")
                  } else if (TextUtils.isEmpty(strikeprice.text.toString())) {
                      showToast("please enter your food strike price")
                  } else if (TextUtils.isEmpty(gstin.text.toString())) {
                      showToast("please enter your gstin nunber")
                  } else if (TextUtils.isEmpty(starttime.text.toString())) {
                      showToast("please select start time")
                  } else if (TextUtils.isEmpty(endtime.text.toString())) {
                      showToast("please select end time")
                  }  else if (itemStatus!!.isEmpty()) {
                      showToast("please select stock or out of stock")
                  }*/
                else {
                    loader.visibility = View.VISIBLE
                    val foodJson = JSONObject().apply {
                        put("foodName", foodname.text.toString())
                        put("price", price.text.toString())
                        put("decription", description.text.toString())
                        put("decription1", briefdescription.text.toString())
                        put("type", vegg.toString())
                        put("decription2", description.text.toString())
                        put("restaurantCatagoryBO", JSONObject().apply {
                            put(
                                "restaurantCatagoryId",
                                sharedHelper.getFromUser("restaurantCatagoryId")
                            )
                        })
                    }

                    val foodBody = foodJson.toString()
                        .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                    if (filebody != null) {
                        homeViewModel.updatefooditem(foodidd, filebody!!, foodBody)
                    } else if (!Constant.image.isNullOrEmpty()) {
                        // ✅ Case 2: we have a base64 image string (convert and send)
                        try {
                            val decodedBytes = android.util.Base64.decode(
                                Constant.image,
                                android.util.Base64.DEFAULT
                            )
                            val file = File(requireActivity().cacheDir, "existing_image.jpg")
                            file.outputStream().use { it.write(decodedBytes) }

                            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                            val base64FileBody =
                                MultipartBody.Part.createFormData("file", file.name, requestFile)

                            homeViewModel.updatefooditem(foodidd, base64FileBody, foodBody)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            showToast("Error processing existing image")
                        }

                    } else {
                        // ✅ Case 3: no image at all
                        val emptyFileBody = MultipartBody.Part.createFormData(
                            "file", "",
                            "".toRequestBody("text/plain".toMediaTypeOrNull())
                        )
                        homeViewModel.updatefooditem(foodidd, emptyFileBody, foodBody)
                    }
                }
            }
        }


    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!
                    this.mViewDataBinding.foodimg.setImageURI(fileUri)
                    val inputStream = requireActivity().contentResolver.openInputStream(fileUri)
                    val file = File(requireActivity().cacheDir, "temp_image.jpg")
                    inputStream?.use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    val requestFile =
                        file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    filebody = MultipartBody.Part.createFormData("file", file.name, requestFile)
                    println("filepart$filebody")

                }

                ImagePicker.RESULT_ERROR -> {

                    showToast("img set error" + ImagePicker.getError(data))

                }

                else -> {
                    showToast("Task Cancelled")
                }
            }
        }

    override fun getLayoutId(): Int = R.layout.producteditdetailfragment

    @SuppressLint("DefaultLocale")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onEvent(model: Timepickermodel) {

        if (Constant.scheduletime == "starttime") {
            this.mViewDataBinding.starttime.text = model.starttime

        } else if (Constant.scheduletime == "endtime") {
            this.mViewDataBinding.endtime.text = model.endtime
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

    private fun showAddCategoryDialog() {
        // ✅ Initialize dialogBuilder before using it
        dialogBuilder = AlertDialog.Builder(requireContext())

        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_category, null)
        val etCategory = dialogView.findViewById<EditText>(R.id.etCategory)
        val btnSubmit = dialogView.findViewById<Button>(R.id.btnSubmit)

        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(true)

        // ✅ Create the dialog
        alertDialog = dialogBuilder.create()
        alertDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog?.show()

        // ✅ Handle submit click
        btnSubmit.setOnClickListener {
            val category = etCategory.text.toString().trim()

            if (category.isEmpty()) {
                showToast("Please add the category")
                return@setOnClickListener
            }

            val requestBody = CreateCategoryRequest(
                restaurantCatagory = category,
                restaurantBo = RestaurantBo(
                    restaurantId = sharedHelper.getFromUser("userid")?.toIntOrNull() ?: 0
                )
            )

            homeViewModel.createcategory(requestBody)
            alertDialog?.dismiss()
        }
    }

    private fun processResponse(response: Response) {
        when (response.status) {
            Status.SUCCESS -> {
                mViewDataBinding.loader.visibility = View.GONE
                when (response.data) {

                    is CreateCategoryResponseModel -> {
                        alertDialog?.dismiss()
                        homeViewModel.getallcategory(sharedHelper.getFromUser("userid"))
                    }

                    is UpdateFoodResponseModel -> {
                        showToast("Food Update successfully")
                        EventBus.getDefault().postSticky(FoodEditEvent("edit"))
                        fragmentManagers!!.popBackStack()
                    }

                    is GetallCategoryResponseModel -> {
                        // Clear old data
                        categoryList.clear()

                        // ✅ Add all category names from response
                        categoryList.addAll(response.data.map { it.restaurantCatagory })

                        // ✅ Add "Add Category" option at the end
                        categoryList.add("Add Category")

                        // ✅ Rebuild adapter
                        adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            categoryList
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        mViewDataBinding.shopcatspinner.adapter = adapter
                        val position = categoryList.indexOf(Constant.category) // find the index of the value
                        if (position >= 0) {
                            mViewDataBinding.shopcatspinner.setSelection(position)
                        }

                        // ✅ Spinner selection listener
                        mViewDataBinding.shopcatspinner.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    val selectedItem = categoryList[position]
                                    if (selectedItem == "Add Category") {
                                        showAddCategoryDialog()
                                    } else {

                                        // ✅ Optionally, store selected categoryId for later use
                                        val selectedCategory = response.data[position]
                                        sharedHelper.putInUser(
                                            "restaurantCatagoryId",
                                            selectedCategory.restaurantCatagoryId.toString()
                                        )
                                    }
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {}
                            }

                    }


                }
            }

            Status.ERROR -> {
                mViewDataBinding.loader.visibility = View.GONE

            }

            Status.LOADING -> {}
            Status.SECONDLOADING -> {}
            Status.DISMISS -> {}
        }
    }

}


