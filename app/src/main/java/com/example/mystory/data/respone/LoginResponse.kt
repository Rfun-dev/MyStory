package com.example.mystory.data.respone

data class LoginResponse(
    val loginResult: LoginResult,

    val error: Boolean,

    val message: String
)

data class LoginResult(
    val name: String,

    val userId: String,

    val token: String
)