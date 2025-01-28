package com.example.retrofitapp.domain.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface UserDao {
    @Insert
    suspend fun insert(user : User)

    @Query("Select * from UserDetails where email = :email")
     fun getUserByEmail(email: String): User

    @Query("SELECT * FROM UserDetails where  email<> :email")
    fun selectAllUser(email:String) : LiveData<MutableList<User>>

    @Query("Delete from UserDetails where email =:email")
    fun deleteUser(email:String)

    @Query("Delete From UserDetails where  email<> :email")
    fun deleteAllUser(email:String)

    @Query("Select * from UserDetails where favourite=1")
    fun selectFavouriteUser(): LiveData<MutableList<User>>

    @Query("Update UserDetails set name = :name , mobile= :mobile, email =:email where id = :id")
    fun updateUser(name:String, mobile: String, email: String, id :Int)

    @Query("Update UserDetails set favourite = :favourite where  email =:email ")
    fun updateFavourite(favourite:Boolean, email: String)

    @Query("Update UserDetails set favourite = :favourite ")
    fun updateFavouriteAll(favourite : Boolean)

    @Query("Update UserDetails set imageUrl = :imageUrl where  email =:email ")
    fun updateImagePath(imageUrl: String?, email: String)

    @Query("Update UserDetails set mobile = :mobile where email= :email  ")
    fun updateMobileNo(mobile: String, email: String)

    @Query("SELECT count(*) FROM UserDetails WHERE id != :id AND email = :email")
    fun checkEmailOfUser(email :String, id:Int):Int


}