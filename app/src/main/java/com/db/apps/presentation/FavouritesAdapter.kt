package com.db.apps.presentation

import com.db.apps.PlaceListener
import com.db.apps.loadUrl


import android.view.LayoutInflater
import android.view.ViewGroup
import com.db.apps.RvAdapter
import com.db.apps.databinding.AttractionItemBinding
import com.db.apps.domain.usecases.AddToFavouritesUseCase
import com.db.apps.model.PlaceEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavouritesAdapter(
    private val list: List<PlaceEntity>,
) : RvAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AttractionItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var photoUrl = ""
        if (list[position].photos?.isNotEmpty()!!) {
            photoUrl = (list[position].photos!!)
        }
        holder.ivPhoto.loadUrl(holder.itemView.context, photoUrl)
        holder.tvName.text = list[position].place_name
        holder.tvDescription.text = list[position].formattedAddress
        holder.layout.setOnClickListener { listener?.onLayoutClick(list[position]) }
        holder.ivLike.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                listener?.onLikeClick(list[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}