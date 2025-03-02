package com.example.chatapp.data.api;

import com.example.chatapp.data.models.LoginRequest;
import com.example.chatapp.data.models.LoginResponse;
import com.example.chatapp.data.models.RegisterRequest
import com.example.chatapp.data.models.RegisterResponse
import com.example.chatapp.data.models.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

interface AuthApi {
    @POST("/auth/login")
    fun login(@Body request:LoginRequest): Call<LoginResponse>

    @POST("/auth/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("/auth/logout")
    fun logout(): Call<Void>

    @GET("/users/user")
    fun getUser(): Call<UserResponse>
}