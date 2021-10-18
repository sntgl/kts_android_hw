package com.example.ktshw1

import com.example.ktshw1.auth.AuthViewModel
import com.example.ktshw1.connection.ConnectionRepository
import com.example.ktshw1.connection.ConnectionRepositoryInterface
import com.example.ktshw1.connection.ConnectionViewModel
import com.example.ktshw1.datastore.DatastoreRepository
import com.example.ktshw1.datastore.DatastoreRepositoryInterface
import com.example.ktshw1.datastore.DatastoreViewModel
import com.example.ktshw1.networking.FeedViewModel
import com.example.ktshw1.repository.AuthRepository
import com.example.ktshw1.repository.AuthRepositoryInterface
import net.openid.appauth.AuthorizationService
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import studio.kts.android.school.lection4.networking.data.FeedRepository
import studio.kts.android.school.lection4.networking.data.FeedRepositoryInterface

val appModule = module {
    single<FeedRepositoryInterface> { FeedRepository() }
    viewModel { FeedViewModel(get(), get()) }
    single<ConnectionRepositoryInterface> { ConnectionRepository(androidContext()) }
    viewModel { ConnectionViewModel(get()) }

    single<DatastoreRepositoryInterface> { DatastoreRepository(androidContext()) }
    viewModel { DatastoreViewModel(get()) }

    single<AuthRepositoryInterface> { AuthRepository() }
    single { AuthorizationService(androidContext()) }
    viewModel { AuthViewModel(get(), get()) }
}