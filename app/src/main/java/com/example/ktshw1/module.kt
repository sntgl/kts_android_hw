package com.example.ktshw1

import com.example.ktshw1.auth.AuthViewModel
import com.example.ktshw1.connection.ConnectionRepository
import com.example.ktshw1.connection.ConnectionViewModel
import com.example.ktshw1.datastore.DatastoreRepository
import com.example.ktshw1.datastore.DatastoreViewModel
import com.example.ktshw1.networking.FeedRepository
import com.example.ktshw1.networking.FeedViewModel
import com.example.ktshw1.networking.ProfileRepository
import com.example.ktshw1.networking.ProfileViewModel
import com.example.ktshw1.repository.AuthRepository
import com.example.ktshw1.utils.SubredditParser
import net.openid.appauth.AuthorizationService
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { FeedRepository() }
    single { SubredditParser() }
    viewModel { FeedViewModel(get(), get(), get()) }

    single { ConnectionRepository(androidContext()) }
    viewModel { ConnectionViewModel(get()) }

    single { DatastoreRepository(androidContext()) }
    viewModel { DatastoreViewModel(get()) }

    single { AuthRepository() }
    factory { AuthorizationService(androidContext()) }
    viewModel { AuthViewModel(get(), get()) }

    single { ProfileRepository() }
    viewModel { ProfileViewModel(get()) }
}