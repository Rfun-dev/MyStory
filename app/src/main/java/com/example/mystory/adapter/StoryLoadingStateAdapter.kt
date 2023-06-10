package com.example.mystory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mystory.databinding.ItemLoadingBinding

class StoryLoadingStateAdapter(private val retry : () -> Unit) : LoadStateAdapter<StoryLoadingStateAdapter.LoadViewHolder>() {
    class LoadViewHolder(private val binding : ItemLoadingBinding, retry: () -> Unit) : RecyclerView.ViewHolder(binding.root){

        init {
            binding.btnRetry.setOnClickListener { retry.invoke() }
        }

        fun onBind(loadState : LoadState) = with(binding){
            if(loadState is LoadState.Error){
                errorMsg.text = loadState.error.localizedMessage
            }

            progressBar.isVisible = loadState is LoadState.Loading
            btnRetry.isVisible = loadState is LoadState.Error
            btnRetry.isVisible = loadState is LoadState.Error
        }

    }

    override fun onBindViewHolder(holder: LoadViewHolder, loadState: LoadState){
        holder.onBind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadViewHolder {
        return LoadViewHolder(
            ItemLoadingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),retry
        )
    }
}