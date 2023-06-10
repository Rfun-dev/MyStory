package com.example.mystory.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mystory.data.api.respone.LoginResult
import com.example.mystory.data.preference.UserPreference
import kotlinx.coroutines.launch

class PrefViewModel(private val pref : UserPreference) : ViewModel() {
    fun loginUser(loginResult : LoginResult) = viewModelScope.launch { pref.saveUser(loginResult.name,loginResult.userId,loginResult.token)}
    fun getUser() : LiveData<LoginResult> = pref.getUser().asLiveData()
    fun signoutUser() = viewModelScope.launch { pref.signout() }
}