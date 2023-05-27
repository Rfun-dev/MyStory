package com.example.mystory.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mystory.data.preference.UserPreference
import com.example.mystory.ui.viewmodel.LoginViewModel
import com.example.mystory.ui.viewmodel.MainViewModel
import com.example.mystory.ui.viewmodel.RegisterVewModel

class ViewModelFactory(private val pref: UserPreference) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                    LoginViewModel(pref) as T
                }
                modelClass.isAssignableFrom(RegisterVewModel::class.java) -> {
                    RegisterVewModel() as T
                }
                modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                    MainViewModel() as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
            }
        }
}