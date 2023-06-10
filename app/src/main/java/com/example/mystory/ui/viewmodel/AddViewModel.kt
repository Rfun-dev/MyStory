package com.example.mystory.ui.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mystory.data.repository.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddViewModel(private val repo: StoryRepository) : ViewModel() {
    fun addStory(token : String, file : MultipartBody.Part, description : RequestBody, latitude : Float, longitude : Float)=
        repo.uploadStory(token, file, description, latitude, longitude)

    private val currentLocation = MutableLiveData<Location>()

    fun getCurrentLocation() : LiveData<Location> = currentLocation

    fun setCurrentLocation(location: Location) { currentLocation.value = location }
}