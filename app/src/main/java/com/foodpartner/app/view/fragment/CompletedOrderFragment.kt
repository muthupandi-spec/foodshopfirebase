package com.foodpartner.app.view.fragment

import android.view.View
import androidx.databinding.ViewDataBinding
import com.app.washeruser.repository.Status
import com.foodpartner.app.R
import com.foodpartner.app.ResponseMOdel.CompletedOrderResponseModel
import com.foodpartner.app.ResponseMOdel.CompleteddOrderResponModelItem
import com.foodpartner.app.ResponseMOdel.Orderresponsenmodel
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentCompletedBinding
import com.foodpartner.app.network.OrderStatus
import com.foodpartner.app.view.adapter.Activeadapter
import com.foodpartner.app.view.adapter.CompletedAdapter
import com.foodpartner.app.view.bottomsheetfragment.OrderDetailFragment
import com.foodpartner.app.viewModel.HomeViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.kotlintest.app.utility.interFace.CommonInterface
import org.koin.androidx.viewmodel.ext.android.viewModel

class CompletedOrderFragment : BaseFragment<FragmentCompletedBinding>() {

    private val db = FirebaseFirestore.getInstance()
    private val orders = ArrayList<Map<String, Any>>()
    override fun initView(mViewDataBinding: ViewDataBinding?) {
        this.mViewDataBinding.apply {
            val restId = sharedHelper.getFromUser("userid") ?: return
listenDeliveredOrders(restId)
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_completed
    private fun listenDeliveredOrders(myId: String) {
        db.collection("orders")
            .whereEqualTo("restaurantId", myId)
            .whereEqualTo("orderStatus", OrderStatus.DELIVERED)
            .addSnapshotListener { snaps, _ ->

                val deliveredList = ArrayList<Map<String, Any>>()
                snaps?.documents?.forEach { deliveredList.add(it.data ?: emptyMap()) }

                if (deliveredList.isEmpty()) {
                    mViewDataBinding.emptytxt.visibility= View.VISIBLE
                    mViewDataBinding.emptyimg.visibility= View.VISIBLE
                    mViewDataBinding?.activeRV?.visibility = View.GONE
                } else {
                    mViewDataBinding?.emptytxt?.visibility = View.GONE
                    mViewDataBinding?.emptyimg?.visibility = View.GONE
                    mViewDataBinding?.activeRV?.visibility = View.VISIBLE
                    mViewDataBinding?.activeRV?.adapter =
                        Activeadapter(deliveredList, object : CommonInterface {
                            override fun commonCallback(any: Any) {}
                        })
                }
            }
    }

}