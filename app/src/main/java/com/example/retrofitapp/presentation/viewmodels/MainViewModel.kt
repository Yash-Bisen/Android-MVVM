package com.example.retrofitapp.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofitapp.data.repository.ServerRepository
import com.example.retrofitapp.data.repository.UserRepository
import com.example.retrofitapp.domain.model.DataSource
import com.example.retrofitapp.domain.model.LoginRequest
import com.example.retrofitapp.domain.model.LoginResp
import com.example.retrofitapp.domain.model.RegisterRequest
import com.example.retrofitapp.domain.model.RegisterResponse
import com.example.retrofitapp.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class MainViewModel @Inject constructor(private val userRepository: UserRepository, private val serverRepository: ServerRepository): ViewModel() {
    private val _usersLiveData = MutableLiveData<MutableList<DataSource.Data>>()
    val usersLiveData: LiveData<MutableList<DataSource.Data>> = _usersLiveData

    fun insert(user : User) {
        viewModelScope.launch ( Dispatchers.IO ){
            userRepository.insert(user)
        }
    }

    fun getUserByEmail(email: String): User {
        return userRepository.getUserByEmail(email)
    }


    fun selectAllUser(email: String): LiveData<MutableList<User>> {
        return userRepository.selectAllUser(email)
    }

    fun deleteUser(email: String){
        return userRepository.deleteUser(email)
    }

    fun deleteAllUser(email: String){
        return userRepository.deleteAllUser(email )
    }

    fun selectFavouriteUser(): LiveData<MutableList<User>> {
        return  userRepository.selectFavouriteUser()
    }

    fun updateUser(name:String, mobile: String, email: String, id :Int){
        return userRepository.updateUser(name, mobile, email, id )
    }

    fun updateFavourite(favourite:Boolean, email: String){
        return userRepository.updateFavourite(favourite, email)
    }

    fun updateFavouriteAll(favourite : Boolean){
        return userRepository.updateFavouriteAll(favourite)
    }

    fun updateImagePath(imageUrl: String?, email: String){
        return userRepository.updateImagePath(imageUrl, email)
    }
    fun updateMobileNo(mobile: String, email: String){
        return userRepository.updateMobileNo(mobile, email)
    }

    fun checkEmailOfUser(email :String, id:Int):Int{
        return userRepository.checkEmailOfUser(email,id)
    }
    fun getUsers()  {
        viewModelScope.launch {
            serverRepository.getUserApiList().enqueue(object : Callback<DataSource> {
                override fun onResponse(call: Call<DataSource>, response: Response<DataSource>) {
                    if (response.isSuccessful) {
                        _usersLiveData.value = response.body()?.data as MutableList<DataSource.Data>?
                    } else {
                        // Handle unsuccessful response
                    }
                }

                override fun onFailure(call: Call<DataSource>, t: Throwable) {
                    // Handle network failure
                }
            })
        }
    }


    fun registerUser(data: RegisterRequest) {
        viewModelScope.launch {
           try{
               val response = withContext(Dispatchers.IO) {
                   serverRepository.registerUser(data)
               }
                   if (response.isSuccessful) {
                       val responses: RegisterResponse? = response.body()
                        Log.i("MyResponse", responses.toString())
                        Log.i("MyActivity", "Passed to get response")
                    } else {
                        Log.i("MyActivity", "Failed to get response")
                    }
           }catch (e: Exception) {
                    Log.i("YourApiClient", "Failed to send data to server", e)
           }
        }
    }


    fun loginUser(data: LoginRequest) {
        viewModelScope.launch {
            try{
                val response = withContext(Dispatchers.IO) {
                    serverRepository.loginUser(data)
                }
                if (response.isSuccessful) {
                    val responses: LoginResp? = response.body()
                    Log.i("MyResponse", responses.toString())
                    Log.i("MyActivity", "Passed to get response")
                } else {
                    Log.i("MyActivity", "Failed to get response")
                }
            }catch (e: Exception) {
                Log.i("YourApiClient", "Failed to send data to server", e)
            }
        }
    }
}