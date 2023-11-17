package com.db.apps

import androidx.recyclerview.widget.RecyclerView
import com.db.apps.databinding.AttractionItemBinding

abstract class RvAdapter: RecyclerView.Adapter<RvAdapter.ViewHolder>() {

    protected var listener: PlaceListener?=null
    fun setClickListener(l: PlaceListener){
        listener = l
    }
    class ViewHolder(binding: AttractionItemBinding): RecyclerView.ViewHolder(binding.root){
        val ivPhoto = binding.ivPlacePhoto
        val tvName = binding.tvPlaceName
        val tvDescription = binding.tvPlaceDescription
        val layout = binding.layout
        val ivLike = binding.ivLike
    }



}