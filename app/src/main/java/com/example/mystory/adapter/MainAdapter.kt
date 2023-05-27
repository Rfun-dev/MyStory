package com.example.mystory.adapter

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mystory.data.respone.ListStoryItem
import com.example.mystory.databinding.MainItemBinding
import com.example.mystory.ui.main.MainFragmentDirections

class MainAdapter(private val list : List<ListStoryItem>) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    private lateinit var binding : MainItemBinding

    class ViewHolder(binding : MainItemBinding) : RecyclerView.ViewHolder(binding.root as View)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = MainItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list[position].let{list ->
            with(binding){
                Glide.with(root.context)
                    .load(list.photoUrl)
                    .into(ivMainItem)
                tvName.text = list.name
                cvMainItem.setOnClickListener{
                    val navigation = MainFragmentDirections.actionMainFragmentToDetailFragment()
                    navigation.name = list.name
                    navigation.description = list.description
                    navigation.imageUrl = list.photoUrl

                    val colorMatrix = ColorMatrix()
                    colorMatrix.setSaturation(1f)
                    val filter = ColorMatrixColorFilter(colorMatrix)
                    ivMainItem.colorFilter = filter
                    root.findNavController().navigate(navigation)
                }
            }
        }
    }
}