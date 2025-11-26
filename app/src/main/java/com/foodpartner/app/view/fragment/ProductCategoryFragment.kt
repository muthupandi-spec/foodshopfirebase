package com.foodpartner.app.view.fragment

import android.view.View
import androidx.databinding.ViewDataBinding
import com.app.washeruser.repository.Status
import com.foodpartner.app.R
import com.foodpartner.app.ResponseMOdel.FoodItemResponemodelItem
import com.foodpartner.app.ResponseMOdel.GetallCategoryResponseModel
import com.foodpartner.app.ResponseMOdel.GetallCategoryResponseModelItem
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.ProductfragmentBinding
import com.foodpartner.app.network.Constant
import com.foodpartner.app.view.adapter.ChipAdapter
import com.foodpartner.app.view.adapter.FoodAdapter
import com.foodpartner.app.view.eventmodel.FoodCreateEvent
import com.foodpartner.app.view.eventmodel.FoodEditEvent
import com.kotlintest.app.utility.interFace.CommonInterface
import com.google.firebase.firestore.FirebaseFirestore
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductCategoryFragment : BaseFragment<ProductfragmentBinding>() {

    private val db = FirebaseFirestore.getInstance()

    var categorylist: ArrayList<GetallCategoryResponseModelItem> = ArrayList()
    var foodlist: ArrayList<FoodItemResponemodelItem> = ArrayList()
    var foodtype: String = "true"   // true = active, false = inactive

    override fun initView(mViewDataBinding: ViewDataBinding?) {

        this.mViewDataBinding.apply {

            // Active items
            active.setOnClickListener {
                foodtype = "true"
                if (Constant.restaurantcategory.isNotEmpty())
                    getFoods(Constant.restaurantcategory)
            }

            // Inactive items
            inactive.setOnClickListener {
                foodtype = "false"
                if (Constant.restaurantcategory.isNotEmpty())
                    getFoods(Constant.restaurantcategory)
            }

            // Load categories
            loadCategories()

            backBtn.setOnClickListener {
                fragmentManagers!!.popBackStackImmediate()
            }

            plus.setOnClickListener {
                loadFragment(ProductDetailFragment(), android.R.id.content, "produ", true)
            }

            search.setOnClickListener {
                loadFragment(SearchFragment(), android.R.id.content, "search", true)
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.productfragment


    // ------------------------------
    // 🔥 LOAD CATEGORIES FROM FIREBASE
    // ------------------------------
    private fun loadCategories() {

        val restaurantId = sharedHelper.getFromUser("userid")

        db.collection("shops")
            .document(restaurantId)
            .collection("categories")
            .get()
            .addOnSuccessListener { result ->

                categorylist.clear()

                for (doc in result) {
                    categorylist.add(
                        GetallCategoryResponseModelItem(
                            categoryId = doc.id,
                            categoryName = doc.getString("restaurantCatagory") ?: "",
                            )
                    )
                }

                    val chipAdapter = ChipAdapter(categorylist, object : CommonInterface {
                        override fun commonCallback(any: Any) {
                            val categoryId = any.toString()  // convert to string if needed
                            Constant.restaurantcategory = categoryId
                            getFoods(categoryId)
                        }
                    })

                mViewDataBinding.recommendChipRC.adapter = chipAdapter
            }
    }


    // ------------------------------
    // 🔥 FETCH FOOD BASED ON CATEGORY
    // ------------------------------
    private fun getFoods(categoryId: String) {

        val restaurantId = sharedHelper.getFromUser("userid")


        val activeStatus = foodtype == "true"    // convert to boolean

        db.collection("shops")
            .document(restaurantId)
            .collection("foods")
            .whereEqualTo("categoryId", categoryId)
            .whereEqualTo("isActive", activeStatus)  // correct boolean filter
            .get()
            .addOnSuccessListener { querySnapshot ->
                foodlist.clear()

                for (doc in querySnapshot.documents) {

                    val item = FoodItemResponemodelItem(
                        foodId = doc.getString("foodId") ?: "",
                        foodName = doc.getString("foodName") ?: "",
                        categoryId = doc.getString("categoryId") ?: "",
                        description = doc.getString("description") ?: "",
                        briefDescription = doc.getString("briefDescription") ?: "",
                        price = doc.getString("price") ?: "",
                        type = doc.getString("type") ?: "",
                        isActive = doc.getBoolean("isActive") ?: false,
                        imageUrl = doc.getString("imageUrl") ?: ""
                    )

                    foodlist.add(item)
                }

                handleFoodUI()
            }
            .addOnFailureListener {
                
            }
    }


    // ------------------------------
    // 🔥 HANDLE EMPTY / NOT EMPTY UI
    // ------------------------------
    private fun handleFoodUI() {

        if (foodlist.isEmpty()) {
            mViewDataBinding.emptyimg.visibility = View.VISIBLE
            mViewDataBinding.emptytxt.visibility = View.VISIBLE
            mViewDataBinding.productrecyclerview.visibility = View.GONE
        } else {
            mViewDataBinding.emptyimg.visibility = View.GONE
            mViewDataBinding.emptytxt.visibility = View.GONE
            mViewDataBinding.productrecyclerview.visibility = View.VISIBLE

            val foodAdapter = FoodAdapter(foodlist, object : CommonInterface {
                override fun commonCallback(any: Any) {

                    if (any is HashMap<*, *>) {

                        when (any["click"].toString()) {

                            "edit" -> loadFragment(
                                ProductEditDetailFragment(any["foodid"].toString()),
                                android.R.id.content,
                                "product", true
                            )

                            "update" -> {
                                
                                updateFoodStatus(
                                    any["foodid"].toString(),
                                    any["isActive"].toString()
                                )
                            }
                        }
                    }
                }
            })

            mViewDataBinding.productrecyclerview.adapter = foodAdapter
        }
    }


    // ------------------------------
    // 🔥 UPDATE FOOD ACTIVE/INACTIVE STATE
    // ------------------------------


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun foodcreate(event: FoodCreateEvent) {
        loadCategories()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun foodedit(event: FoodEditEvent) {
        loadCategories()
    }
    private fun updateFoodStatus(foodId: String, isActive: String) {

        val restaurantId = sharedHelper.getFromUser("userid")

        val status = isActive == "true"  // convert string → boolean

        db.collection("shops")
            .document(restaurantId)
            .collection("foods")
            .document(foodId)
            .update("isActive", status)
            .addOnSuccessListener {
                
                getFoods(Constant.restaurantcategory)
            }.addOnFailureListener {
                
            }
    }

}
