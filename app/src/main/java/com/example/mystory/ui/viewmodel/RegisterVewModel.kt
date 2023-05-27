package com.example.mystory.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mystory.data.api.ApiConfig
import com.example.mystory.data.respone.MessageResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterVewModel : ViewModel() {
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    var error = MutableLiveData("")
    var message = MutableLiveData("")

    fun register(name : String, email : String, password : String){
        _isLoading.value = true
        val client = ApiConfig().getApiService().registerStory(name, email, password)
        client.enqueue(object : Callback<MessageResponse>{
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                when(response.code()){
                    201 -> message.postValue("201")
                    400 -> error.postValue("error")
                    else -> error.postValue("${response.errorBody()}")
                }
                _isLoading.value = false
            }
            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                _isLoading.value = false
                error.postValue(t.message)
            }
        })
    }
}