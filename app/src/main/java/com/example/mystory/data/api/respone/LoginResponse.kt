package com.example.mystory.data.api.respone

data class LoginResponse(
    val
    loginResult: LoginResult? = null,

    val error: Boolean,

    val message: String
)

data class LoginResult(
    val name: String,

    val userId: String,

    val token: String
)