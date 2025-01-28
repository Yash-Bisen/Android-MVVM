package com.example.retrofitapp

import android.app.Application
import com.example.retrofitapp.di.component.*
import com.example.retrofitapp.di.module.*


class RetrofitApp : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .dataBaseModule(DataBaseModule(this))
            .repositoryModule(RepositoryModule ())
            .viewModelModule(ViewModelModule())
            .sessionManagerModule(SessionManagerModule(applicationContext))
            .build()

    }
}