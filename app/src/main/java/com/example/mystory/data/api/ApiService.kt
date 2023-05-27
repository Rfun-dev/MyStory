package com.example.mystory.data.api

import com.example.mystory.data.respone.LoginResponse
import com.example.mystory.data.respone.MessageResponse
import com.example.mystory.data.respone.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @GET("stories")
    fun getListStory(
        @Header("authorization") bearer : String?
    ) : Call<StoryResponse>

    @FormUrlEncoded
    @POST("register")
    fun registerStory(
        @Field("name") name : String,
        @Field("email") email : String,
        @Field("password") password : String,
    ) : Call<MessageResponse>

    @FormUrlEncoded
    @POST("login")
    fun loginStory(
        @Field("email") email : String,
        @Field("password") password: String
    ) : Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun addStory(
        @Header("Authorization") bearer: String?,
        @Part file : MultipartBody.Part,
        @Part("description") description : RequestBody?
    ) : Call<MessageResponse>
}