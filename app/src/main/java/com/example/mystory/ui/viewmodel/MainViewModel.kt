package com.example.mystory.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mystory.data.api.ApiConfig
import com.example.mystory.data.respone.ListStoryItem
import com.example.mystory.data.respone.MessageResponse
import com.example.mystory.data.respone.StoryResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    private val _storyList = MutableLiveData<List<ListStoryItem>>()
    val storyList : LiveData<List<ListStoryItem>> get() = _storyList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> get() = _isLoading

    private val message = MutableLiveData<String>()

    fun getListStory(token : String,context: Context){
        _isLoading.value = true
        val client = ApiConfig().getApiService().getListStory("Bearer $token")
        client.enqueue(object : Callback<StoryResponse>{
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                _isLoading.value = false
                if(response.isSuccessful){
                    _storyList.postValue(response.body()?.listStory)
                }else{
                    Toast.makeText(context, response.message(),Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun addNewStory(token: String, imageFile : MultipartBody.Part, desc : String){
        _isLoading.value = true
        val description = desc.toRequestBody("text/plain".toMediaType())

        val client = ApiConfig().getApiService().addStory("Bearer $token", imageFile,description)
        client.enqueue(object : Callback<MessageResponse>{
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                _isLoading.value = false
                message.value = response.message()
            }
            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                _isLoading.value = false
                message.value = t.message
            }
        })
    }

}