package com.example.mystory.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mystory.data.repository.StoryRepository

class RegisterVewModel(private val repository : StoryRepository) : ViewModel() {
    fun registerUser(name : String,email : String,password : String) = repository.registerUser(name, email, password)
}