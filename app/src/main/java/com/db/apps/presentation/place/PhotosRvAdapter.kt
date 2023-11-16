package com.db.apps.presentation.place

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.db.apps.databinding.ItemPhotoBinding
import com.db.apps.getPhotoUrl
import com.db.apps.loadUrl
import com.db.apps.model.Photo

class PhotosRvAdapter(
    private val photos: List<Photo>
)
    :RecyclerView.Adapter<PhotosRvAdapter.PhotosViewHolder>() {
        class PhotosViewHolder(binding: ItemPhotoBinding): RecyclerView.ViewHolder(binding.root){
            val ivPhoto = binding.ivPhoto
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        val binding = ItemPhotoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PhotosViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        holder.ivPhoto.loadUrl(holder.ivPhoto.context, getPhotoUrl(photos[position]))
        Log.e("onBindViewHolder", "url: ${getPhotoUrl(photos[position])}")
    }
}