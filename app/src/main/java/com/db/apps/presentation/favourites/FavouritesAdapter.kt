package com.db.apps.presentation.favourites

import android.util.Log
import com.db.apps.loadUrl


import android.view.LayoutInflater
import android.view.ViewGroup
import com.db.apps.R
import com.db.apps.RvAdapter
import com.db.apps.databinding.AttractionItemBinding
import com.db.apps.model.PlaceEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavouritesAdapter(

) : RvAdapter() {
    private var list: List<PlaceEntity> = listOf()

    fun setList(l: List<PlaceEntity>){
        list = l
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AttractionItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var photoUrl = ""
        if (list[position].photos?.isNotEmpty() == true) {
            photoUrl = (list[position].photos!!)
        }
        holder.ivPhoto.loadUrl(holder.itemView.context, photoUrl)
        holder.tvName.text = list[position].place_name
        holder.tvDescription.text = list[position].formattedAddress
        holder.layout.setOnClickListener { listener?.onLayoutClick(list[position]) }
        Log.d("MMMY_LOG", list[position].toString())
        // Set the appropriate drawable based on the liked state
        selectIcon(holder,position)

        holder.ivLike.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                listener?.onLikeClick(list[position])

                // Notify the adapter that the item at this position has changed
                withContext(Dispatchers.Main){
                    list[position].isLiked = !list[position].isLiked
                    selectIcon(holder,position)
                    notifyItemChanged(position)
                }
            }
        }
    }

    private fun selectIcon(holder: ViewHolder, position: Int){
        if (list[position].isLiked) {
            holder.ivLike.setImageResource(R.drawable.like_active)
        } else {
            holder.ivLike.setImageResource(R.drawable.like_inactive)
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }


}