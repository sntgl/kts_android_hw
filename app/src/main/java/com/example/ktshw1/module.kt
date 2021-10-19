package com.example.ktshw1

import com.example.ktshw1.auth.AuthViewModel
import com.example.ktshw1.connection.ConnectionRepository
import com.example.ktshw1.connection.ConnectionViewModel
import com.example.ktshw1.datastore.DatastoreRepository
import com.example.ktshw1.datastore.DatastoreViewModel
import com.example.ktshw1.networking.FeedViewModel
import com.example.ktshw1.repository.AuthRepository
import net.openid.appauth.AuthorizationService
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import studio.kts.android.school.lection4.networking.data.FeedRepository

val appModule = module {
    single { FeedRepository() }
    viewModel { FeedViewModel(get()) }

    single { ConnectionRepository(androidContext()) }
    viewModel { ConnectionViewModel(get()) }

    single { DatastoreRepository(androidContext()) }
    viewModel { DatastoreViewModel(get()) }

    single { AuthRepository() }
    single { AuthorizationService(androidContext()) }
    viewModel { AuthViewModel(get(), get()) }
}