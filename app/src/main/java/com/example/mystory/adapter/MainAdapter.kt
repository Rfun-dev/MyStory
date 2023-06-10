package com.example.mystory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mystory.data.database.entity.StoryEntity
import com.example.mystory.databinding.MainItemBinding
import com.example.mystory.ui.main.MainFragmentDirections

class MainAdapter : PagingDataAdapter<StoryEntity, MainAdapter.ViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickListener: OnItemClickCallback
    private lateinit var binding: MainItemBinding

    class ViewHolder(binding: MainItemBinding) : RecyclerView.ViewHolder(binding.root as View)

    interface OnItemClickCallback {
        fun onItemClicked(data: Int)
    }

    fun setOnItemClickCallback(onItemClickListener: OnItemClickCallback) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = MainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        snapshot()[position].let { list ->
            with(binding) {
                Glide.with(root.context)
                    .load(list?.photoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivMainItem)
                tvName.text = list?.name
                cvMainItem.setOnClickListener {
                    onItemClickListener.onItemClicked(position)
                    val navigation = MainFragmentDirections.actionMainFragmentToDetailFragment()
                    navigation.name = list?.name.toString()
                    navigation.description = list?.description.toString()
                    navigation.imageUrl = list?.photoUrl.toString()
                    navigation.createdAt = list?.createdAt.toString()
                    navigation.lat = list?.lat as Float
                    navigation.lon = list.lon as Float
                    root.findNavController().navigate(navigation)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean =
                oldItem.name == newItem.name ||
                        oldItem.lat == newItem.lat ||
                        oldItem.lon == newItem.lon ||
                        oldItem.createdAt == newItem.createdAt ||
                        oldItem.description == newItem.description ||
                        oldItem.photoUrl == newItem.photoUrl

            override fun areContentsTheSame(
                oldItem: StoryEntity,
                newItem: StoryEntity
            ): Boolean =
                oldItem.name == newItem.id
        }


    }

}