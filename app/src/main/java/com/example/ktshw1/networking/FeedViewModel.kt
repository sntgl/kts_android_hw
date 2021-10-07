package com.example.ktshw1.networking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.kts.android.school.lection4.networking.data.FeedRepository
import timber.log.Timber

class FeedViewModel: ViewModel() {
    private val repository = FeedRepository()

    private val feedLiveData = MutableLiveData<List<Subreddit>>(emptyList())
    private val isLoadingLiveData = MutableLiveData(false)

    private var currentSearchJob: Job? = null

    val feedList: LiveData<List<Subreddit>>
        get() = feedLiveData

    val isLoading: LiveData<Boolean>
        get() = isLoadingLiveData

    fun firstBestFeed() {
        isLoadingLiveData.postValue(true)
        currentSearchJob?.cancel()
        val timber = Timber.tag("LOAD")
        currentSearchJob = viewModelScope.launch {
            runCatching {
                Timber.tag("LOAD").d("Request sent")
                repository.getBestFeed()
            }.onSuccess { value ->
                Timber.tag("LOAD").d("Success")
                isLoadingLiveData.postValue(false)
                feedLiveData.postValue(value)
            }.onFailure {
                Timber.tag("LOAD").d("Error")
                timber.e(it)
                isLoadingLiveData.postValue(false)
                feedLiveData.postValue(emptyList())
            }
        }
    }

    fun nextBestFeed() {
        if (isLoading.value == true) return
        isLoadingLiveData.postValue(true)
        currentSearchJob?.cancel()
        currentSearchJob = viewModelScope.launch {
            runCatching {
                Timber.tag("LOAD next").d("Request sent")
                repository.getNextBestFeed(feedLiveData.value?.last()?.id ?: "")
            }.onSuccess {
                Timber.tag("LOAD next").d("Success")
                isLoadingLiveData.postValue(false)
                feedLiveData.postValue(feedLiveData.value?.plus(it))
            }.onFailure {
                Timber.tag("LOAD next").d("Error")
                Timber.tag("LOAD next").e(it)
                isLoadingLiveData.postValue(false)
                feedLiveData.postValue(emptyList())
            }
        }
    }
}