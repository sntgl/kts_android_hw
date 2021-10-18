package com.example.ktshw1.datastore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber

class DatastoreViewModel(
    private val repository: DatastoreRepositoryInterface
) : ViewModel() {

    val onBoardingPassedFlow: Flow<Boolean?>
        get() = repository.isOnBoardingPassed()

    fun passOnBoarding() {
        viewModelScope.launch {
            repository.passOnBoarding()
        }
    }

    val getApiKeyFlow: Flow<String?>
        get() = repository.getRedditToken()

    fun onReceivedApiKey(key: String) {
        viewModelScope.launch {
            repository.redditTokenReceived(key)
        }
    }
}