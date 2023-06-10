package com.example.mystory.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mystory.data.repository.StoryRepository

class LoginViewModel(private val repository: StoryRepository) : ViewModel() {
    private var _isLoading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _isLoading

    val error = MutableLiveData("")
    val message = MutableLiveData("")

    fun login(email : String, password : String) = repository.loginUser(email, password)

}