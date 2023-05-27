package com.example.mystory.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mystory.data.api.ApiConfig
import com.example.mystory.data.preference.UserPreference
import com.example.mystory.data.respone.LoginResponse
import com.example.mystory.data.respone.LoginResult
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref : UserPreference) : ViewModel() {
    private var _isLoading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _isLoading

    val error = MutableLiveData("")
    val message = MutableLiveData("")

    val loginResult = MutableLiveData<LoginResponse?>()

    fun login(email : String, password : String, context: Context){
        _isLoading.value = true
        val client = ApiConfig().getApiService().loginStory(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val result = response.body()
                when (response.code()) {
                    200 -> {
                        loginResult.postValue(result)
                        message.postValue("200")
                    }
                    400,401 -> {
                        error.postValue("error")
                    }
                    else -> {
                        error.postValue("${response.errorBody()}")
                    }
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Toast.makeText(context,"Error ${t.message}",Toast.LENGTH_SHORT).show()
            }

        })

    }

    fun getUser() : LiveData<LoginResult>{
        return pref.getUser().asLiveData()
    }

    fun logout(){
        viewModelScope.launch {
            pref.signout()
        }
    }

    fun saveUser(username : String?, userId : String?, token : String?){
        viewModelScope.launch {
            pref.saveUser(username,userId,token)
        }
    }
}