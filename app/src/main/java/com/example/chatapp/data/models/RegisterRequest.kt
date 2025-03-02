package com.example.chatapp.data.models

data class RegisterRequest(
    val login: String,
    val password: String,
    val name: String
)