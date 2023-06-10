package com.example.mystory.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mystory.data.repository.StoryRepository

class MapsViewModel(private val repo : StoryRepository) : ViewModel() {
    fun getLocation(token : String) = repo.getLocation(token)
}