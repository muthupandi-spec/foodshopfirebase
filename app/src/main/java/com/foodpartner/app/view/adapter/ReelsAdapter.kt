package com.foodpartner.app.view.adapter

import android.app.AlertDialog
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.foodpartner.app.databinding.ItemReelBinding
import com.foodpartner.app.view.responsemodel.ReelModel

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ReelsAdapter(
    private val list: MutableList<ReelModel>
) : RecyclerView.Adapter<ReelsAdapter.ReelViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    inner class ReelViewHolder(val binding: ItemReelBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {
        val binding = ItemReelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReelViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {

        val reel = list[position]

        holder.binding.videoView.setVideoURI(Uri.parse(reel.videoUrl))
        holder.binding.videoView.setOnPreparedListener {
            it.isLooping = true
            holder.binding.videoView.start()
        }

        holder.binding.btnDelete.setOnClickListener {

            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Delete Reel")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes") { _, _ ->

                    // Delete from storage
                    storage.getReferenceFromUrl(reel.videoUrl)
                        .delete()

                    // Delete from Firestore
                    firestore.collection("reels")
                        .document(reel.id)
                        .delete()

                    list.removeAt(position)
                    notifyDataSetChanged()

                    Toast.makeText(
                        holder.itemView.context,
                        "Reel Deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }
}