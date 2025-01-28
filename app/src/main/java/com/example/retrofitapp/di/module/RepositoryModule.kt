package com.example.retrofitapp.di.module

import com.example.retrofitapp.data.repository.ServerRepository
import com.example.retrofitapp.data.repository.UserRepository
import com.example.retrofitapp.domain.model.UserDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepository(userDao)
    }

    @Provides
    @Singleton
    fun provideServerRepository(): ServerRepository {
        return ServerRepository()
    }
}