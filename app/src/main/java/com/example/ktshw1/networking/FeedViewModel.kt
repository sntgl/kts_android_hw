package com.example.ktshw1.networking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktshw1.model.FeedLoading
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.kts.android.school.lection4.networking.data.FeedRepository
import timber.log.Timber

class FeedViewModel: ViewModel() {
    private val repository = FeedRepository()

    private val feedLiveData = MutableLiveData<List<*>>(listOf(FeedLoading()))
    private val isLoadingLiveData = MutableLiveData(false)

    private var currentSearchJob: Job? = null

    val feedList: LiveData<List<*>>
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
            }.onSuccess {
                Timber.tag("LOAD").d("Success")
                isLoadingLiveData.postValue(false)
                postLoading(it)
//                feedLiveData.postValue(value+FeedLoading())
            }.onFailure {
                Timber.tag("LOAD").d("Error")
                timber.e(it)
                isLoadingLiveData.postValue(false)
                postLoading(null)
            }
        }
    }

    fun nextBestFeed() {
        if (isLoading.value == true) return
        isLoadingLiveData.postValue(true)
        currentSearchJob?.cancel()
        val after: String = getLastSubreddit()
        currentSearchJob = viewModelScope.launch {
            runCatching {
                Timber.tag("LOAD next").d("Request sent, next is $after")
                repository.getNextBestFeed(after)
            }.onSuccess {
                Timber.tag("LOAD next").d("Success")
                isLoadingLiveData.postValue(false)
                postLoading(feedLiveData.value?.plus(it))
            }.onFailure {
                Timber.tag("LOAD next").d("Error")
                Timber.tag("LOAD next").e(it)
                isLoadingLiveData.postValue(false)
            }
        }
    }

    private fun getLastSubreddit(): String {
        val woLast = feedLiveData.value?.dropLast(1) ?: emptyList()
        return if (woLast.isNotEmpty() && woLast.last() is Subreddit)
            (woLast.last() as Subreddit).id else ""
    }

    private fun postLoading(l: List<*>?) {
        var dropLast = feedLiveData.value?.dropLast(1) ?: emptyList()
        dropLast = dropLast.plus(l?.plus(FeedLoading()) ?: listOf(FeedLoading()))
        feedLiveData.postValue(dropLast)
    }
}