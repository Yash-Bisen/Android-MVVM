package com.example.retrofitapp.di.component

import com.example.retrofitapp.di.module.DataBaseModule
import com.example.retrofitapp.di.module.RepositoryModule
import com.example.retrofitapp.di.module.SessionManagerModule
import com.example.retrofitapp.di.module.ViewModelModule
import com.example.retrofitapp.presentation.adapter.FavouriteUserAdapter
import com.example.retrofitapp.presentation.adapter.UserListAdapter
import com.example.retrofitapp.presentation.views.activity.MainActivity
import com.example.retrofitapp.presentation.views.fragments.FavouriteUserFragment
import com.example.retrofitapp.presentation.views.fragments.LoginFragment
import com.example.retrofitapp.presentation.views.fragments.ProfileFragment
import com.example.retrofitapp.presentation.views.fragments.RegistrationFragment
import com.example.retrofitapp.presentation.views.fragments.UpdateUserFragment
import com.example.retrofitapp.presentation.views.fragments.UsersListFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DataBaseModule::class, RepositoryModule::class, ViewModelModule::class, SessionManagerModule:: class])
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(fragment: LoginFragment)
    fun inject(fragment: ProfileFragment)
    fun inject(fragment: RegistrationFragment)
    fun inject(fragment: UsersListFragment)
    fun inject(fragment: UpdateUserFragment)
    fun inject(fragment: FavouriteUserFragment)
    fun inject(adapter: FavouriteUserAdapter)
    fun inject(adapter: UserListAdapter)
}