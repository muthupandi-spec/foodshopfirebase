package com.foodpartner.app.view.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentCouponBinding
import com.foodpartner.app.view.adapter.CouponAdapter
import com.foodpartner.app.view.responsemodel.CouponModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.foodpartner.app.R

import java.util.Calendar
import kotlin.apply
import kotlin.collections.forEach
import kotlin.text.format
import kotlin.text.isEmpty
import kotlin.text.toDoubleOrNull
import kotlin.text.trim
import kotlin.text.uppercase
import kotlin.to
import kotlin.toString

class CouponFragment : BaseFragment<FragmentCouponBinding>() {

    private val db = FirebaseFirestore.getInstance()
    private val shopId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private var expiryTimestamp: Timestamp? = null
    private lateinit var couponAdapter: CouponAdapter
    private val couponList = ArrayList<CouponModel>()

    override fun getLayoutId(): Int = R.layout.fragment_coupon

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        mViewDataBinding ?: return

        setupRecyclerView()
        loadCoupons()

        this.mViewDataBinding.apply {

            btnBack.setOnClickListener { fragmentManagers?.popBackStack() }

            // Discount type toggle
            rgDiscountType.setOnCheckedChangeListener { _, checkedId ->
                if (checkedId == R.id.rbPercentage) {
                    tilDiscount.hint = "Discount %"
                } else {
                    tilDiscount.hint = "Flat Amount (AED)"
                }
            }

            // Expiry date picker
            edtExpiry.setOnClickListener {
                val cal = Calendar.getInstance()
                DatePickerDialog(requireContext(), { _, year, month, day ->
                    val pickedCal = Calendar.getInstance().apply {
                        set(year, month, day, 23, 59, 59)
                    }
                    expiryTimestamp = Timestamp(pickedCal.time)
                    edtExpiry.setText(String.format("%02d-%02d-%04d", day, month + 1, year))
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .apply { datePicker.minDate = System.currentTimeMillis() }
                    .show()
            }

            // Add coupon
            btnAddCoupon.setOnClickListener {
                val code = edtCouponCode.text.toString().trim().uppercase()
                val discountStr = edtDiscount.text.toString().trim()
                val minOrderStr = edtMinOrder.text.toString().trim()
                val discountType = if (rbPercentage.isChecked) "percentage" else "flat"

                if (code.isEmpty()) {
                    edtCouponCode.error = "Enter coupon code"; return@setOnClickListener
                }
                if (discountStr.isEmpty()) {
                    edtDiscount.error = "Enter discount value"; return@setOnClickListener
                }
                if (minOrderStr.isEmpty()) {
                    edtMinOrder.error = "Enter minimum order amount"; return@setOnClickListener
                }
                if (expiryTimestamp == null) {
                    Toast.makeText(requireContext(), "Select expiry date", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val discountValue = discountStr.toDoubleOrNull() ?: 0.0
                if (discountType == "percentage" && discountValue > 100) {
                    edtDiscount.error = "Percentage cannot exceed 100"; return@setOnClickListener
                }

                val couponData = kotlin.collections.hashMapOf(
                    "code" to code,
                    "discountType" to discountType,
                    "discountValue" to discountValue,
                    "minOrderAmount" to (minOrderStr.toDoubleOrNull() ?: 0.0),
                    "expiryDate" to expiryTimestamp!!,
                    "isActive" to true,
                    "createdAt" to Timestamp.now()
                )

                showLoader()
                db.collection("shops").document(shopId)
                    .collection("coupons").document(code)
                    .set(couponData)
                    .addOnSuccessListener {
                        hideLoader()
                        clearForm()
                        Toast.makeText(requireContext(), "Coupon added!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        hideLoader()
                        Toast.makeText(requireContext(), "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun setupRecyclerView() {
        couponAdapter = CouponAdapter(couponList) { coupon, action ->
            when (action) {
                "toggle" -> toggleCoupon(coupon)
                "delete" -> deleteCoupon(coupon)
            }
        }
        mViewDataBinding.rvCoupons.layoutManager = LinearLayoutManager(activitys)
        mViewDataBinding.rvCoupons.adapter = couponAdapter
    }

    private fun loadCoupons() {
        db.collection("shops").document(shopId)
            .collection("coupons")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                couponList.clear()
                snapshot?.documents?.forEach { doc ->
                    val model = CouponModel(
                        id            = doc.id,
                        code          = doc.getString("code") ?: "",
                        discountType  = doc.getString("discountType") ?: "flat",
                        discountValue = doc.getDouble("discountValue") ?: 0.0,
                        minOrderAmount= doc.getDouble("minOrderAmount") ?: 0.0,
                        expiryDate    = doc.getTimestamp("expiryDate"),
                        isActive      = doc.getBoolean("isActive") ?: true
                    )
                    couponList.add(model)
                }
                couponAdapter.notifyDataSetChanged()

                mViewDataBinding.tvNoCoupons.visibility =
                    if (couponList.isEmpty()) View.VISIBLE else View.GONE
            }
    }

    private fun toggleCoupon(coupon: CouponModel) {
        db.collection("shops").document(shopId)
            .collection("coupons").document(coupon.id)
            .update("isActive", !coupon.isActive)
    }

    private fun deleteCoupon(coupon: CouponModel) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Coupon")
            .setMessage("Delete coupon '${coupon.code}'?")
            .setPositiveButton("Delete") { _, _ ->
                db.collection("shops").document(shopId)
                    .collection("coupons").document(coupon.id)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Deleted!", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun clearForm() {
        mViewDataBinding.edtCouponCode.text?.clear()
        mViewDataBinding.edtDiscount.text?.clear()
        mViewDataBinding.edtMinOrder.text?.clear()
        mViewDataBinding.edtExpiry.text?.clear()
        mViewDataBinding.rbPercentage.isChecked = true
        expiryTimestamp = null
    }
}