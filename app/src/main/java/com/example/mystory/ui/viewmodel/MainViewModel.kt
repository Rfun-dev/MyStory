package com.example.mystory.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mystory.data.database.entity.StoryEntity
import com.example.mystory.data.repository.StoryRepository

class MainViewModel(
    private val repo : StoryRepository
) : ViewModel() {
    fun getListStory(token : String) : LiveData<PagingData<StoryEntity>> = repo.getListStory(token).cachedIn(viewModelScope)

    fun dataResult(combinedLoadStates: CombinedLoadStates) = repo.getDataResult(combinedLoadStates).asLiveData()
}