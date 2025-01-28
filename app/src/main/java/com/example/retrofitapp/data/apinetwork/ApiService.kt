package com.example.retrofitapp.data.apinetwork

import retrofit2.Call
import com.example.retrofitapp.domain.model.DataSource
import com.example.retrofitapp.domain.model.LoginRequest
import com.example.retrofitapp.domain.model.LoginResp
import com.example.retrofitapp.domain.model.RegisterRequest
import com.example.retrofitapp.domain.model.RegisterResponse
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("users")
    fun getUser():  Call<DataSource>

    @POST("register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResp>

}