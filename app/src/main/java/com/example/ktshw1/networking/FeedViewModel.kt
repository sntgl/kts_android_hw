package com.example.ktshw1.networking

import androidx.lifecycle.*
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

    private var after: String = ""

    val feedList: LiveData<List<*>>
        get() = feedLiveData

    val isLoading: LiveData<Boolean>
        get() = isLoadingLiveData

    fun getBestFeed() {
        if (isLoadingLiveData.value == true) return
        isLoadingLiveData.value = true
        currentSearchJob?.cancel()
        val timber = Timber.tag("LOAD")
        currentSearchJob = viewModelScope.launch {
            runCatching {
                Timber.tag("LOAD").d("Request sent, after $after")
                repository.getBestFeed(after)
            }.onSuccess {
                isLoadingLiveData.postValue(false)
                Timber.tag("LOAD").d("Success")
                after = it.after ?: ""
                appendToFeed(unwrap(it))
            }.onFailure {
                isLoadingLiveData.postValue(false)
                Timber.tag("LOAD").d("Error")
                timber.e(it)
            }
        }
    }

    private fun unwrap(wrapped: ServerListingWrapper<ServerResponseWrapper<Subreddit>>): List<Subreddit> {
        val unwrappedList = mutableListOf<Subreddit>()
        wrapped.children.forEach { unwrappedList.add(it.data) }
        return unwrappedList
    }

    private fun appendToFeed(l: List<*>?) {
        //добавляет l к feedLiveData и перемещает FeedLoading в конец
        var dropLast = if (feedLiveData.value?.last() is FeedLoading)
            feedLiveData.value?.dropLast(1) ?: emptyList() else emptyList()
        dropLast = dropLast.plus(l?.plus(FeedLoading()) ?: listOf(FeedLoading()))
        feedLiveData.postValue(dropLast)
    }

    fun vote(sr: Subreddit, newVote: Boolean?) {
        currentSearchJob = viewModelScope.launch {
            runCatching {
                Timber.tag("Vote").d("Request sent, id ${sr.id}, new_vote $newVote")
                repository.vote(sr.id, newVote)
            }.onSuccess {
                val fLD = feedLiveData.value
                if (it is Subreddit && fLD != null) {
                    val index = fLD.indexOf(sr)
                    Timber.tag("Vote").d("Subreddit updated!")
                    val list = fLD.toMutableList()
                    list[index] = it
                    feedLiveData.postValue(list)
                }
                Timber.tag("Vote").d("Success")
            }.onFailure {
                Timber.tag("Vote").d("Error")
                Timber.tag("Vote").e(it)
            }
        }
    }


}