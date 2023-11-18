package com.db.apps

import android.view.LayoutInflater
import android.view.ViewGroup
import com.db.apps.databinding.AttractionItemBinding
import com.db.apps.domain.usecases.AddToFavouritesUseCase
import com.db.apps.model.ResultAttraction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AttractionsAdapter(
    private val list: List<ResultAttraction>,

): RvAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AttractionItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var photoUrl = ""
        if(list[position].photos.size>0) {
            photoUrl = getPhotoUrl(list[position].photos[0])
        }
        holder.ivPhoto.loadUrl(holder.itemView.context, photoUrl)
        holder.tvName.text = list[position].name
        holder.tvDescription.text = list[position].formattedAddress
        //holder.layout.setOnClickListener { listener?.onLayoutClick(list[position]) }

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