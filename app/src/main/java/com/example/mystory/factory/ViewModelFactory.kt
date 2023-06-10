package com.example.mystory.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mystory.data.repository.StoryRepository
import com.example.mystory.di.Injection
import com.example.mystory.ui.viewmodel.AddViewModel
import com.example.mystory.ui.viewmodel.LoginViewModel
import com.example.mystory.ui.viewmodel.MainViewModel
import com.example.mystory.ui.viewmodel.MapsViewModel
import com.example.mystory.ui.viewmodel.RegisterVewModel

class ViewModelFactory(private val repository: StoryRepository) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(RegisterVewModel::class.java) -> {
                    RegisterVewModel(repository) as T
                }
                modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                    LoginViewModel(repository) as T
                }
                modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                    MainViewModel(repository) as T
                }
                modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                    MapsViewModel(repository) as T
                }
                modelClass.isAssignableFrom(AddViewModel::class.java) -> {
                    AddViewModel(repository) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
            }
        }

    companion object{
        @Volatile
        private var instance : ViewModelFactory? = null

        fun getInstance(context : Context) : ViewModelFactory = instance ?: synchronized(this){
            instance ?: ViewModelFactory(Injection.provideInjection(context))
        }.also { instance = it }
    }
}