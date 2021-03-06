package com.example.okrapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.okrapp.data.models.Photo
import com.example.okrapp.databinding.PopularPhotosItemBinding
import kotlin.properties.Delegates

class PopularPhotosAdapter(
    private val itemClickListener: ListItemClickListener<Photo>
) :
    RecyclerView.Adapter<PopularPhotosAdapter.PopularPhotosViewHolder>() {

    var itemList: List<Photo> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularPhotosViewHolder =
        PopularPhotosViewHolder(PopularPhotosItemBinding.inflate(LayoutInflater.from(parent.context)))


    override fun onBindViewHolder(holder: PopularPhotosViewHolder, position: Int) {

        val photo = itemList[position]
        holder.bind(photo)
        holder.itemView.setOnClickListener { itemClickListener(photo) }
    }

    override fun getItemCount() = itemList.size

    class PopularPhotosViewHolder(private val binding: PopularPhotosItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo) {
            binding.popularPhoto.loadUrl(photo.images[0].httpsUrl)
        }
    }
}