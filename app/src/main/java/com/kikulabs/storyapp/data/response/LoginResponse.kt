package com.kikulabs.storyapp.data.response

data class LoginResponse (
    val error: Boolean,
    val message: String,
    val loginResult: LoginResult
)