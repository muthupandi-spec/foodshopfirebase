package com.foodpartner.app.view.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.ViewDataBinding
import com.app.washeruser.repository.Status
import com.foodpartner.app.R
import com.foodpartner.app.ResponseMOdel.CreateFoodResponseModel
import com.foodpartner.app.ResponseMOdel.GetallCategoryResponseModel
import com.foodpartner.app.baseClass.BaseActivity.BaseActivity
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentSampleBinding
import com.foodpartner.app.databinding.FragmentShopfragmentBinding
import com.foodpartner.app.databinding.ProductdetailfragmentBinding
import com.foodpartner.app.databinding.ProductfragmentBinding
import com.foodpartner.app.network.Constant
import com.foodpartner.app.view.adapter.Activeadapter
import com.foodpartner.app.view.adapter.ProductCategoryAdapter
import com.foodpartner.app.view.adapter.ShopAdapter
import com.foodpartner.app.view.bottomsheetfragment.ScheduleBottomsheetfragment
import com.foodpartner.app.view.eventmodel.FoodCreateEvent
import com.foodpartner.app.view.eventmodel.FoodEditEvent
import com.foodpartner.app.view.eventmodel.Timepickermodel
import com.foodpartner.app.view.requestmodel.CreateCategoryRequest
import com.foodpartner.app.view.requestmodel.RestaurantBo
import com.foodpartner.app.view.responsemodel.CreateCategoryResponseModel
import com.foodpartner.app.view.responsemodel.RestuarantModel
import com.foodpartner.app.view.responsemodel.UserregisterResponseModel
import com.foodpartner.app.viewModel.HomeViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.kotlintest.app.utility.interFace.CommonInterface
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.Timer
import kotlin.concurrent.schedule

class ProductDetailFragment : BaseFragment<ProductdetailfragmentBinding>() {
    private var filebody: MultipartBody.Part? = null
    private var openclosetime: String? = null
    var status: String? = "add"
    private var itemStatus: String? = null
    var vegg: String? = null
    var foodid: Int? = 1
    private val homeViewModel by viewModel<HomeViewModel>()
    private lateinit var categoryList: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var bottomSheetFragment: ScheduleBottomsheetfragment
    private lateinit var dialogBuilder: AlertDialog.Builder
    private var alertDialog: AlertDialog? = null


    override fun initView(mViewDataBinding: ViewDataBinding?) {
        this.mViewDataBinding.apply {
homeViewModel.getallcategory(sharedHelper.getFromUser("userid"))
            homeViewModel.response().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
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
                    loader.visibility= View.VISIBLE
                    val foodJson = JSONObject().apply {
                        put("foodName", foodname.text.toString())
                        put("price", price.text.toString())
                        put("decription", description.text.toString())
                        put("decription1", briefdescription.text.toString())
                        put("type", vegg.toString())
                        put("decription2", description.text.toString())
                        put("restaurantCatagoryBO", JSONObject().apply {
                            put("restaurantCatagoryId", sharedHelper.getFromUser("restaurantCatagoryId"))
                        })
                    }

                    val foodBody = foodJson.toString()
                        .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                     if (filebody != null) {
                       homeViewModel.addfooditem(filebody!!,foodBody)
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

    override fun getLayoutId(): Int = R.layout.productdetailfragment

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

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_category, null)
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

    private fun processResponse(response: com.foodpartner.app.network.Response) {
        when (response.status) {
            Status.SUCCESS -> {
               mViewDataBinding. loader.visibility= View.GONE
                when (response.data) {

                    is CreateCategoryResponseModel -> {
                        alertDialog?.dismiss()
                        homeViewModel.getallcategory(sharedHelper.getFromUser("userid"))

                    }
                    is CreateFoodResponseModel->{
                        showToast("Food create successfully")
                        EventBus.getDefault().postSticky(FoodCreateEvent("edit"))
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
                        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryList)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        mViewDataBinding.shopcatspinner.adapter = adapter

                        // ✅ Spinner selection listener
                        mViewDataBinding.shopcatspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                                    sharedHelper.putInUser("restaurantCatagoryId", selectedCategory.restaurantCatagoryId.toString())
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                    }


                }
            }

            Status.ERROR -> {
                mViewDataBinding. loader.visibility= View.GONE

            }

            Status.LOADING -> {}
            Status.SECONDLOADING -> {}
            Status.DISMISS -> {}
        }
    }

}


