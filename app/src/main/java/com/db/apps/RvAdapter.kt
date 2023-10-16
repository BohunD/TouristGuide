package com.db.apps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.db.apps.databinding.AttractionItemBinding
import com.db.apps.model.PlaceItemRv

class RvAdapter(
    private val list: List<PlaceItemRv>
): RecyclerView.Adapter<RvAdapter.MyViewHolder>() {

    class MyViewHolder(binding: AttractionItemBinding): RecyclerView.ViewHolder(binding.root){
        val ivPhoto = binding.ivPlacePhoto
        val tvName = binding.tvPlaceName
        val tvDescription = binding.tvPlaceDescription
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = AttractionItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.ivPhoto.loadUrl(holder.itemView.context, list[position].photoUrl)
        holder.tvName.text = list[position].placeName
        holder.tvDescription.text = list[position].placeDescription
    }
}