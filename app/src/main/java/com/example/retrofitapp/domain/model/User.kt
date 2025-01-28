package com.example.retrofitapp.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userDetails")
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("Id")val id : Int,
    @ColumnInfo("Name")val name : String,
    @ColumnInfo("Mobile")val mobileNo: String,
    @ColumnInfo("Email")val email: String,
    @ColumnInfo("Password")val password: String,
    @ColumnInfo("Favourite")val favourite : Boolean,
    @ColumnInfo("ImageUrl")val imageUrl : String?,
    val profileImagePlaceholder: String?
)