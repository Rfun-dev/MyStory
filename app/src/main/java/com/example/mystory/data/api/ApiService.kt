package com.example.mystory.data.api

import com.example.mystory.data.api.respone.LoginResponse
import com.example.mystory.data.api.respone.MessageResponse
import com.example.mystory.data.api.respone.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @GET("stories")
    suspend fun getListStory(
        @Header("authorization") bearer : String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ) : StoryResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun registerStory(
        @Field("name") name : String,
        @Field("email") email : String,
        @Field("password") password : String,
    ) : MessageResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun loginStory(
        @Field("email") email : String,
        @Field("password") password: String
    ) : LoginResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") bearer: String?,
        @Part file : MultipartBody.Part,
        @Part("description") description : RequestBody?,
        @Part("lat") lat : Float,
        @Part("lon") long : Float
    ) : MessageResponse

    @GET("stories")
    suspend fun getLocation(
        @Header("Authorization") bearer: String?,
    ) : StoryResponse
}