package com.db.apps

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.db.apps.databinding.AttractionItemBinding
import com.db.apps.model.ResultAttraction

class RvAdapter(
    private val list: List<ResultAttraction>
): RecyclerView.Adapter<RvAdapter.MyViewHolder>() {
    private var listener: PlaceListener?=null

    class MyViewHolder(binding: AttractionItemBinding): RecyclerView.ViewHolder(binding.root){
        val ivPhoto = binding.ivPlacePhoto
        val tvName = binding.tvPlaceName
        val tvDescription = binding.tvPlaceDescription
        val layout = binding.layout
    }

    fun setListener(l: PlaceListener){
        listener = l
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
        var photoUrl = ""
        if(list[position].photos.size>0) {
            photoUrl = getPhotoUrl(list[position].photos[0])
        }
        holder.ivPhoto.loadUrl(holder.itemView.context, photoUrl)
        holder.tvName.text = list[position].name
        holder.tvDescription.text = list[position].formattedAddress
        holder.layout.setOnClickListener { listener?.onLayoutClick(list[position]) }
    }
}