package com.foodpartner.app.view.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.foodpartner.app.databinding.FragmentReelsBinding
import com.foodpartner.app.view.adapter.ReelsAdapter
import com.foodpartner.app.view.responsemodel.ReelModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ReelsFragment : Fragment() {

    private lateinit var binding: FragmentReelsBinding
    private var videoUri: Uri? = null

    private val PICK_VIDEO_REQUEST = 1001

    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var adapter: ReelsAdapter
    private val reelList = mutableListOf<ReelModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentReelsBinding.inflate(inflater, container, false)

        setupRecyclerView()

        binding.btnPickVideo.setOnClickListener {
            pickVideoFromGallery()
        }

        loadReels()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = ReelsAdapter(reelList)
        binding.rvReels.adapter = adapter
    }

    private fun pickVideoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "video/*"
        startActivityForResult(intent, PICK_VIDEO_REQUEST)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_VIDEO_REQUEST &&
            resultCode == Activity.RESULT_OK &&
            data != null
        ) {
            videoUri = data.data
            uploadVideo()
        }
    }

    private fun uploadVideo() {

        val uri = videoUri ?: return
        val shopId = auth.currentUser?.uid ?: return

        binding.progressBar.visibility = View.VISIBLE

        val fileName = "${System.currentTimeMillis()}.mp4"

        val storageRef = storage.reference
            .child("reels")
            .child(shopId)
            .child(fileName)

        storageRef.putFile(uri)
            .addOnSuccessListener {

                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->

                    saveReelToFirestore(shopId, downloadUri.toString())
                }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Upload Failed: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun saveReelToFirestore(shopId: String, videoUrl: String) {

        val reelData = hashMapOf(
            "shopId" to shopId,
            "videoUrl" to videoUrl,
            "createdAt" to System.currentTimeMillis()
        )

        firestore.collection("reels")
            .add(reelData)
            .addOnSuccessListener {

                binding.progressBar.visibility = View.GONE

                Toast.makeText(
                    requireContext(),
                    "Reel Uploaded Successfully 🎉",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Firestore Error: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun loadReels() {

        val shopId = auth.currentUser?.uid ?: return

        firestore.collection("reels")
            .whereEqualTo("shopId", shopId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) return@addSnapshotListener

                reelList.clear()

                snapshot?.documents?.forEach { doc ->

                    val reel = doc.toObject(ReelModel::class.java)
                    reel?.id = doc.id
                    reel?.let { reelList.add(it) }
                }

                adapter.notifyDataSetChanged()
            }
    }
}