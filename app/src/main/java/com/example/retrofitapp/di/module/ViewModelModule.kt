package com.example.retrofitapp.di.module

import androidx.lifecycle.ViewModelProvider
import com.example.retrofitapp.data.repository.ServerRepository
import com.example.retrofitapp.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import com.example.retrofitapp.presentation.viewmodels.MainViewModel
import javax.inject.Singleton

@Module
 class ViewModelModule {
     @Singleton
    @Provides
    fun provideMainViewModel(userRepository: UserRepository, serverRepository: ServerRepository): MainViewModel {
        return MainViewModel(userRepository,serverRepository )
    }
    @Singleton
    @Provides
    fun provideMainViewModelFactory(mainViewModel: MainViewModel):ViewModelProvider.Factory{
        return MainViewModelFactory(mainViewModel)
    }

}