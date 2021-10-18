package com.example.ktshw1

import com.example.ktshw1.networking.FeedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import studio.kts.android.school.lection4.networking.data.FeedRepository
import studio.kts.android.school.lection4.networking.data.FeedRepositoryInterface

val appModule = module {
    single<FeedRepositoryInterface> { FeedRepository() }
    viewModel { FeedViewModel(get()) }
}