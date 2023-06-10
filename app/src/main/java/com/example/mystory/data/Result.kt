package com.example.mystory.data

sealed class Result<out R> private constructor(){
    data class Success<out T>(val data : T) : Result<T>()

    data class Error(val message : String) : Result<Nothing>()

    object Loading : Result<Nothing>()

    data class Authorized(val message : String) : Result<Nothing>()

    data class ServerError(val message : String) : Result<Nothing>()
}