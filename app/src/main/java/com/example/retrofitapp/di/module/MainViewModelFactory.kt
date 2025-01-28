package com.example.retrofitapp.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.retrofitapp.presentation.viewmodels.MainViewModel
import javax.inject.Inject

class MainViewModelFactory @Inject constructor(private val mainViewModel: MainViewModel): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return mainViewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

