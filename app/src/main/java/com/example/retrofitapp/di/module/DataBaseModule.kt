package com.example.retrofitapp.di.module

import android.app.Application
import androidx.room.Room
import com.example.retrofitapp.domain.model.UserDao
import com.example.retrofitapp.domain.model.UserDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataBaseModule(application: Application) {
    private val userDataBase: UserDatabase = Room.databaseBuilder(
     application, UserDatabase::class.java, "User Database"
    )
        .fallbackToDestructiveMigration()
    .allowMainThreadQueries()
        .build()

    @Provides
    @Singleton
    fun provideUserDatabase(): UserDatabase {
        return userDataBase
    }

    @Provides
    @Singleton
    fun provideUserDao(): UserDao {
        return userDataBase.userdao()
    }

}