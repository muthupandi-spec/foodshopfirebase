package com.foodpartner.app.view.fragment

import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.ViewDataBinding
import com.app.washeruser.repository.Status
import com.foodpartner.app.R
import com.foodpartner.app.ResponseMOdel.FoodItemResponemodel
import com.foodpartner.app.ResponseMOdel.FoodItemResponemodelItem
import com.foodpartner.app.ResponseMOdel.GetallCategoryResponseModel
import com.foodpartner.app.ResponseMOdel.Orderresponsenmodel
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentLoginBinding
import com.foodpartner.app.databinding.FragmentOtpBinding
import com.foodpartner.app.databinding.HomefragmentBinding
import com.foodpartner.app.databinding.SearchfoodfragmentBinding
import com.foodpartner.app.network.Constant
import com.foodpartner.app.view.adapter.Activeadapter
import com.foodpartner.app.view.adapter.CancelledAdapter
import com.foodpartner.app.view.adapter.ChipAdapter
import com.foodpartner.app.view.adapter.FoodAdapter
import com.foodpartner.app.view.adapter.FoodItemsearchAdapter
import com.foodpartner.app.view.bottomsheetfragment.OrderdetailBottomsheetFragment
import com.foodpartner.app.view.responsemodel.RestuarantModel
import com.foodpartner.app.viewModel.HomeViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.kotlintest.app.utility.interFace.CommonInterface
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale
import java.util.Timer
import kotlin.concurrent.schedule
import kotlin.getValue

class SearchFragment : BaseFragment<SearchfoodfragmentBinding>() {
    private var adapter: FoodAdapter? = null
    var foodlist: ArrayList<FoodItemResponemodelItem> = ArrayList()
    private val db = FirebaseFirestore.getInstance()

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        this.mViewDataBinding.apply {

            backBtn.setOnClickListener {
                fragmentManagers!!.popBackStackImmediate()
            }

            search.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    filterlist(s.toString())
                }
            })
        }

        // 🔥 Load all food items without category filter
        getAllFoods()
    }

    override fun getLayoutId(): Int = R.layout.searchfoodfragment
    private fun filterlist(query: String) {
        val filteredList = foodlist.filter {
            it.foodName.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT))
        }

        if (filteredList.isEmpty()) {
            mViewDataBinding.filterrecycler.visibility = View.GONE
            mViewDataBinding.notfoundgroup.visibility = View.VISIBLE
        } else {
            mViewDataBinding.filterrecycler.visibility = View.VISIBLE
            mViewDataBinding.notfoundgroup.visibility = View.GONE
            adapter?.filterList(ArrayList(filteredList))
        }
    }


    private fun getAllFoods() {

        val restaurantId = sharedHelper.getFromUser("userid") ?: return

        db.collection("shops")
            .document(restaurantId)
            .collection("foods")
            .get()
            .addOnSuccessListener { querySnapshot ->
                foodlist.clear()

                for (doc in querySnapshot.documents) {

                    val item = FoodItemResponemodelItem(
                        foodId = doc.id,
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

                // Set adapter
                adapter = FoodAdapter(foodlist, object : CommonInterface {
                    override fun commonCallback(any: Any) {}
                })

                mViewDataBinding.filterrecycler.adapter = adapter
            }
            .addOnFailureListener {
            }
    }


}