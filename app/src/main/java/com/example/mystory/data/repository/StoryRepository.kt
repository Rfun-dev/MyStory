package com.example.mystory.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.CombinedLoadStates
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.mystory.data.Result
import com.example.mystory.data.api.ApiService
import com.example.mystory.data.api.respone.LoginResponse
import com.example.mystory.data.api.respone.MessageResponse
import com.example.mystory.data.database.StoryDatabase
import com.example.mystory.data.database.entity.StoryEntity
import com.example.mystory.data.model.StoryItem
import com.example.mystory.data.paging.StoryRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(
    private val apiService: ApiService,
    private val database : StoryDatabase
) {
    fun registerUser(name : String,email : String,password : String) : LiveData<Result<MessageResponse>> = liveData {
        emit(Result.Loading)
        try {
            val registerResponse = apiService.registerStory(name, email, password)
            emit(Result.Success(registerResponse))
        }catch (e : Exception){
            emit(Result.Error(e.message.toString()))
        }
    }

    fun loginUser(email : String, password : String) : LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val loginResponse = apiService.loginStory(email, password)
            emit(Result.Success(loginResponse))
        }catch (e : Exception){
            emit(Result.Error(e.message.toString()))
        }
    }


    @OptIn(ExperimentalPagingApi::class)
    fun getListStory(token : String) : LiveData<PagingData<StoryEntity>>{
           return Pager(
               config = PagingConfig(pageSize = 10, initialLoadSize = 10),
               remoteMediator = StoryRemoteMediator(database, apiService, token),
               pagingSourceFactory = {
                   database.storyDao().getAllStories()
               }
           ).liveData
    }

    fun getDataResult(loadState : CombinedLoadStates) : Flow<Result<Unit>> = flow {
        when(val result = loadState.refresh){
            is LoadState.Loading -> emit(Result.Loading)
            is LoadState.NotLoading -> emit(Result.Success(Unit))
            is LoadState.Error -> {
                try {
                    val error = result.error.message?.split(" - ")
                    val messageCode = error?.get(0)
                    val messageError = error?.get(1)
                    when(messageCode?.toInt()){
                        401 -> emit(Result.Authorized(messageError.toString()))
                        500, 501 -> emit(Result.ServerError(messageError.toString()))
                        else -> emit(Result.Error(messageError.toString()))
                    }
                } catch (e: Exception) {
                    emit(Result.Error(e.localizedMessage ?: " "))
                }

            }
        }
    }

    fun uploadStory(
        token : String,
        file : MultipartBody.Part,
        description: RequestBody,
        latitude : Float,
        longitude : Float
    ) : LiveData<Result<MessageResponse>> = liveData{
        emit(Result.Loading)
        try {
            val response = apiService.addStory("Bearer $token",file,description,latitude,longitude)
            emit(Result.Success(response))
        }catch (e : Exception){
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getLocation(
        token : String
    ) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getLocation("Bearer $token")
            val listStory = response.listStory.map {
                StoryItem(
                    it.photoUrl,
                    it.name,
                    it.description,
                    it.id,
                    it.createdAt,
                    it.lat.toDouble(),
                    it.lon.toDouble(),
                )
            }
            emit(Result.Success(listStory))
        }catch (e : Exception){
            emit(Result.Error(e.message.toString()))
        }
    }
}
