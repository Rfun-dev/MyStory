package com.example.mystory.di

import android.content.Context
import com.example.mystory.data.api.ApiConfig
import com.example.mystory.data.database.StoryDatabase
import com.example.mystory.data.repository.StoryRepository

object Injection {
    fun provideInjection(context: Context) : StoryRepository{
        val apiService = ApiConfig().getApiService()
        val database = StoryDatabase.getInstance(context)
        return StoryRepository(apiService,database)
    }
}