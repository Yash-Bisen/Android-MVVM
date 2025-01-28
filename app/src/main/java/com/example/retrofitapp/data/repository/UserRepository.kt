package com.example.retrofitapp.data.repository

import androidx.lifecycle.LiveData

import com.example.retrofitapp.domain.model.User
import com.example.retrofitapp.domain.model.UserDao

import javax.inject.Inject


class UserRepository @Inject constructor(private val userDao: UserDao) {

    suspend fun insert(user : User) {
       return userDao.insert(user)
    }

    fun getUserByEmail(email: String): User {
        return userDao.getUserByEmail(email)
    }


    fun selectAllUser(email: String): LiveData<MutableList<User>>{
        return userDao.selectAllUser(email)
    }

    fun deleteUser(email: String){
        return userDao.deleteUser(email)
    }

    fun deleteAllUser(email:String){
        return userDao.deleteAllUser(email)
    }

    fun selectFavouriteUser():LiveData<MutableList<User>>{
        return  userDao.selectFavouriteUser()
    }

    fun updateUser(name:String, mobile: String, email: String, id :Int){
        return userDao.updateUser(name, mobile, email, id )
    }

    fun updateFavourite(favourite:Boolean, email: String){
        return userDao.updateFavourite(favourite, email)
    }

    fun updateFavouriteAll(favourite : Boolean){
        return userDao.updateFavouriteAll(favourite)
    }

    fun updateImagePath(imageUrl: String?, email: String){
        return userDao.updateImagePath(imageUrl, email)
    }
    fun updateMobileNo(mobile: String, email: String){
        return userDao.updateMobileNo(mobile, email)
    }
    fun checkEmailOfUser(email :String, id:Int):Int{
        return userDao.checkEmailOfUser(email,id)
    }



//    companion object {
//        private var instance: UserRepository? = null
//
//        fun getInstance(application: Application): UserRepository {
//            return instance ?: synchronized(this) {
//                instance ?: UserRepository(application ).also { instance = it }
//            }
//        }
//    }

}