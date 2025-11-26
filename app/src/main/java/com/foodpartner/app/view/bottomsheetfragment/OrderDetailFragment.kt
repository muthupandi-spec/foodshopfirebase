package com.foodpartner.app.view.bottomsheetfragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.app.washeruser.repository.Status
import com.foodpartner.app.ResponseMOdel.OrderDetailResponsemodel

import com.foodpartner.app.appControl.AppController
import com.foodpartner.app.databinding.FragmentOrderdetailBinding
import com.foodpartner.app.view.adapter.FoodItemAdapter
import com.foodpartner.app.viewModel.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kotlintest.app.utility.interFace.CommonInterface
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrderDetailFragment(var orderid: String) :  BottomSheetDialogFragment() {
    private val homeViewModel by viewModel<HomeViewModel>()
    private lateinit var binding: FragmentOrderdetailBinding
    private var baseApplication: AppController? = null // Replace with your server's port
    var selecteditem: String? = null
    var orderlist: ArrayList<OrderDetailResponsemodel.OrderItem> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderdetailBinding.inflate(layoutInflater, container, false)
        baseApplication = activity?.applicationContext as AppController
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loader.visibility = View.VISIBLE

        homeViewModel.vieworders(orderid)
        homeViewModel.response().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            processResponse(it)
        })



    }

    private fun processResponse(response: com.foodpartner.app.network.Response) {
        when (response.status) {
            Status.SUCCESS -> {
                binding.loader.visibility = View.GONE
                when (response.data) {
                    is OrderDetailResponsemodel -> {
                        binding.help.setOnClickListener {
                            val phoneNumber = response.data.mobileNumber
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse(phoneNumber)
                            }
                            startActivity(intent)
                        }
                        val data = response.data

                        binding.orderid.text = "# Order ID ${data.orderId}"
                        binding.orderid1.text = "# Order ID ${data.orderId}"
                        binding.status1.text = data.orderStatus
                        binding.totalvalue.text = "${data.totalAmount ?: 0} AED"
                        binding.foodCost1.text = "${data.totalAmount ?: 0} AED"
                        binding.location1.text = data.landMark
                        binding.foodCount1.text =  data.orderItems.size.toString()+" Qty"

                        if (!data.orderItems.isNullOrEmpty()) {
                            val firstItem = data.orderItems[0]
                            binding.restaurantname.text = firstItem.foodName
                            showBase64Image(firstItem.product?.image ?: "", binding.foodImgBg1)
                        }

                        orderlist.clear()
                        orderlist.addAll(data.orderItems)
                        val adapter = FoodItemAdapter(orderlist, object : CommonInterface {
                            override fun commonCallback(any: Any) {
                                selecteditem = any.toString()
                            }
                        })
                        binding.foodlist.adapter = adapter
                    }
                }

    }

    Status.LOADING -> {}
    Status.SECONDLOADING -> {}
    Status.DISMISS -> {}
    Status.ERROR -> {
        binding.loader.visibility = View.GONE

    }
}

}
fun showBase64Image(base64String: String, imageView: ImageView) {
// Decode Base64 string into a byte array
val decodedString: ByteArray = Base64.decode(base64String, Base64.DEFAULT)

// Convert byte array to Bitmap
val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

// Set the Bitmap to the ImageView
imageView.setImageBitmap(bitmap)
}

}