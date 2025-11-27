package com.foodpartner.app.view.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentOfferBinding
import com.foodpartner.app.view.adapter.OfferAdapter
import com.foodpartner.app.view.responsemodel.OfferModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class Offerfragment : BaseFragment<FragmentOfferBinding>() {

    private lateinit var bindingList: FragmentOfferBinding
    private lateinit var adapter: OfferAdapter
    private val offerList = ArrayList<OfferModel>()

    private var selectedImage: Uri? = null

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun getLayoutId(): Int = R.layout.fragment_offer

    override fun initView(mViewDataBinding: ViewDataBinding?) {

        bindingList = mViewDataBinding as FragmentOfferBinding

        adapter = OfferAdapter(offerList, object : OfferAdapter.OfferClick {
            override fun onDelete(offer: OfferModel) {
                deleteOffer(offer)
            }
        })

        bindingList.rvOffers.layoutManager = LinearLayoutManager(requireContext())
        bindingList.rvOffers.adapter = adapter

        loadOffers()

        bindingList.btnPick.setOnClickListener {
            pickImage()
        }

        bindingList.btnUpload.setOnClickListener {
            uploadBanner()
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(req: Int, res: Int, data: Intent?) {
        super.onActivityResult(req, res, data)

        if (req == 1001 && res == Activity.RESULT_OK) {
            selectedImage = data?.data
            Glide.with(requireContext()).load(selectedImage).into(bindingList.bannerPreview)
        }
    }

    private fun uploadBanner() {
        val shopId = sharedHelper.getFromUser("userid")

        if (selectedImage == null) {
            showToast("Select a banner image")
            return
        }

        val offerId = UUID.randomUUID().toString()
        val ref = storage.getReference("offers/$shopId/$offerId.jpg")

        ref.putFile(selectedImage!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { url ->

                    val data = hashMapOf(
                        "offerId" to offerId,
                        "bannerUrl" to url.toString(),
                        "timestamp" to System.currentTimeMillis()
                    )

                    db.collection("shops")
                        .document(shopId)
                        .collection("offers")
                        .document(offerId)
                        .set(data)
                        .addOnSuccessListener {
                            showToast("Banner uploaded")
                            loadOffers()
                        }
                }
            }
    }

    private fun loadOffers() {
        val shopId = sharedHelper.getFromUser("userid")

        db.collection("shops")
            .document(shopId)
            .collection("offers")
            .get()
            .addOnSuccessListener { snap ->

                offerList.clear()

                for (doc in snap) {
                    offerList.add(
                        OfferModel(
                            offerId = doc.getString("offerId") ?: "",
                            bannerUrl = doc.getString("bannerUrl") ?: ""
                        )
                    )
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun deleteOffer(offer: OfferModel) {
        val shopId = sharedHelper.getFromUser("shopid")

        db.collection("shops")
            .document(shopId)
            .collection("offers")
            .document(offer.offerId)
            .delete()
            .addOnSuccessListener {
                showToast("Deleted")
                offerList.remove(offer)
                adapter.notifyDataSetChanged()
            }
    }
}





///*
//class Offerfragment : BaseFragment<FragmentOfferBinding>() {
//
//    private lateinit var bindingList: FragmentOfferBinding
//    private lateinit var adapter: OfferAdapter
//    private val offerList = ArrayList<OfferModel>()
//
//    private var selectedImage: Uri? = null
//
//    private val db = FirebaseFirestore.getInstance()
//    private val storage = FirebaseStorage.getInstance()
//
//    override fun getLayoutId(): Int = R.layout.fragment_offer
//
//    override fun initView(mViewDataBinding: ViewDataBinding?) {
//
//        bindingList = mViewDataBinding as FragmentOfferBinding
//
//        adapter = OfferAdapter(offerList, object : OfferAdapter.OfferClick {
//            override fun onDelete(offer: OfferModel) {
//                deleteBanner(offer)
//            }
//        })
//
//        bindingList.rvOffers.layoutManager = LinearLayoutManager(requireContext())
//        bindingList.rvOffers.adapter = adapter
//
//        loadBanners()
//
//        bindingList.btnPick.setOnClickListener { pickImage() }
//        bindingList.btnUpload.setOnClickListener { uploadBanner() }
//    }
//
//    // ------------------ PICK IMAGE ------------------
//
//    private fun pickImage() {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        startActivityForResult(intent, 1001)
//    }
//
//    override fun onActivityResult(req: Int, res: Int, data: Intent?) {
//        super.onActivityResult(req, res, data)
//
//        if (req == 1001 && res == Activity.RESULT_OK) {
//            selectedImage = data?.data
//            Glide.with(requireContext())
//                .load(selectedImage)
//                .into(bindingList.bannerPreview)
//        }
//    }
//
//    // ------------------ UPLOAD BANNER ------------------
//
//    private fun uploadBanner() {
//
//        if (selectedImage == null) {
//            showToast("Select a banner image")
//            return
//        }
//
//        val bannerId = UUID.randomUUID().toString()
//        val ref = storage.getReference("banners/$bannerId.jpg")
//
//        ref.putFile(selectedImage!!)
//            .addOnSuccessListener {
//
//                ref.downloadUrl.addOnSuccessListener { url ->
//
//                    val data = hashMapOf(
//                        "bannerId" to bannerId,
//                        "bannerUrl" to url.toString(),
//                        "timestamp" to System.currentTimeMillis()
//                    )
//
//                    db.collection("banners")
//                        .document(bannerId)
//                        .set(data)
//                        .addOnSuccessListener {
//                            showToast("Banner Uploaded Successfully")
//
//                            selectedImage = null
//                            bindingList.bannerPreview.setImageDrawable(null)
//
//                            loadBanners()
//                        }
//                }
//            }
//            .addOnFailureListener {
//                showToast("Upload Failed: ${it.message}")
//            }
//    }
//
//    // ------------------ LOAD ALL BANNERS ------------------
//
//    private fun loadBanners() {
//
//        db.collection("banners")
//            .orderBy("timestamp")
//            .get()
//            .addOnSuccessListener { snap ->
//
//                offerList.clear()
//
//                for (doc in snap) {
//                    offerList.add(
//                        OfferModel(
//                            offerId = doc.getString("bannerId") ?: "",
//                            bannerUrl = doc.getString("bannerUrl") ?: ""
//                        )
//                    )
//                }
//
//                adapter.notifyDataSetChanged()
//            }
//            .addOnFailureListener {
//                showToast("Failed to load banners")
//            }
//    }
//
//    // ------------------ DELETE BANNER ------------------
//
//    private fun deleteBanner(offer: OfferModel) {
//
//        val imageRef = storage.getReference("banners/${offer.offerId}.jpg")
//
//        // delete from storage
//        imageRef.delete()
//
//        db.collection("banners")
//            .document(offer.offerId)
//            .delete()
//            .addOnSuccessListener {
//                showToast("Deleted")
//
//                offerList.remove(offer)
//                adapter.notifyDataSetChanged()
//            }
//            .addOnFailureListener {
//                showToast("Failed: ${it.message}")
//            }
//    }
//}
//*/
