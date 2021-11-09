package com.example.ktshw1.datastore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DatastoreViewModel(
    private val repository: DatastoreRepository
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

    fun onReceivedApiKey(key: String?, expires: Long?, refresh: String? = null) {
        viewModelScope.launch {
            repository.redditTokenReceived(key, refresh, expires)
        }
    }

    fun clear() {
        viewModelScope.launch {
            repository.clear()
        }
    }
}