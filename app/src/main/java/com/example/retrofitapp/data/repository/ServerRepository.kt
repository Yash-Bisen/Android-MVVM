package com.example.retrofitapp.data.repository

import com.example.retrofitapp.data.apinetwork.ApiService
import com.example.retrofitapp.data.apinetwork.UserRetrofitClient
import com.example.retrofitapp.domain.model.DataSource
import com.example.retrofitapp.domain.model.LoginRequest
import com.example.retrofitapp.domain.model.LoginResp
import com.example.retrofitapp.domain.model.RegisterRequest
import com.example.retrofitapp.domain.model.RegisterResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import retrofit2.Call

import retrofit2.Response
import javax.inject.Inject


class ServerRepository @Inject constructor() {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val apiService: ApiService = UserRetrofitClient.retrofit.create(ApiService::class.java)

    fun  getUserApiList(): Call<DataSource>{
        return apiService.getUser()
    }

    suspend fun registerUser(data: RegisterRequest): Response<RegisterResponse>{
        return apiService.registerUser(data )
    }

    suspend fun loginUser(data: LoginRequest): Response<LoginResp>{
        return apiService.loginUser(data )
    }

}
