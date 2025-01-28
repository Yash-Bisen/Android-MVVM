package com.example.retrofitapp.di.module

import android.content.Context
import com.example.retrofitapp.utils.manager.SessionManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module

class SessionManagerModule(private val context: Context) {
    @Singleton
    @Provides
    fun provideContext(): Context {
        return context
    }
    @Singleton
    @Provides
    fun provideSessionManager(context: Context): SessionManager {
        return SessionManager(context)
    }
}