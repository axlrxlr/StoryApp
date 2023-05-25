package com.mizu.submissionstoryapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mizu.submissionstoryapp.api.ListStoryItem
import com.mizu.submissionstoryapp.databinding.StoryListItemBinding

class StoryListAdapter(private val listStory: List<ListStoryItem>): RecyclerView.Adapter<StoryListAdapter.ListViewHolder>()  {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem)
    }
    class ListViewHolder(var binding: StoryListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = StoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int = listStory.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(listStory[position].photoUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .format(DecodeFormat.PREFER_RGB_565)
            .into(holder.binding.ivPostImage)
        holder.binding.tvUserName.text = listStory[position].name
        holder.binding.tvDesc.text = listStory[position].description
        holder.itemView.setOnClickListener{
            onItemClickCallback.onItemClicked(listStory[holder.adapterPosition])
        }
    }


}