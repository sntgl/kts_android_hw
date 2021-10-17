package com.example.ktshw1.networking

import androidx.lifecycle.*
import com.example.ktshw1.model.FeedLoading
import kotlinx.coroutines.*
import studio.kts.android.school.lection4.networking.data.FeedRepository
import timber.log.Timber


class FeedViewModel : ViewModel() {
    private val repository = FeedRepository()

    private val feedLiveData = MutableLiveData<List<*>>(listOf(FeedLoading()))
    private val isLoadingLiveData = MutableLiveData(false)

    private val voteErrorMutable = MutableLiveData(false)
    val voteError: LiveData<Boolean>
        get() = voteErrorMutable

    private val feedErrorMutable = MutableLiveData(false)
    private val feedError: LiveData<Boolean>
        get() = feedErrorMutable
    var feedErrorOld = false

    private var currentSearchJob: Job? = null

    private var after: String = ""

    val feedList: LiveData<List<*>>
        get() = feedLiveData

    val isLoading: LiveData<Boolean>
        get() = isLoadingLiveData

    private val feedErrorObserver: Observer<Boolean> by lazy {
        Observer<Boolean> { if (it != feedErrorOld) errorToFeed(it) }
    }

    private val handler = CoroutineExceptionHandler { _, exception ->
        Timber.d("Network caught $exception")
    }

    init {
        feedError.observeForever(feedErrorObserver)
        getBestFeed()
    }


    fun getBestFeed() {
        if (isLoadingLiveData.value == true) return
        isLoadingLiveData.value = true
        feedErrorMutable.postValue(false)
        currentSearchJob?.cancel()
        val timber = Timber.tag("LOAD")
        currentSearchJob = viewModelScope.launch(handler) {
            runCatching {
                Timber.tag("LOAD").d("Request sent, after $after")
                repository.getBestFeed(after)
            }.onSuccess {
                isLoadingLiveData.postValue(false)
                Timber.tag("LOAD").d("Success")
                appendToFeed(it.first)
                after = it.second ?: ""
            }.onFailure {
                feedErrorMutable.postValue(true)
                isLoadingLiveData.postValue(false)
                Timber.tag("LOAD").d("Error")
                timber.e(it)
            }
        }
    }

    private fun appendToFeed(l: List<*>?) {
        //добавляет l к feedLiveData и перемещает FeedLoading в конец
        var dropLast = if (feedLiveData.value?.last() is FeedLoading)
            feedLiveData.value?.dropLast(1) ?: emptyList() else emptyList()
        dropLast = dropLast.plus(l?.plus(FeedLoading()) ?: listOf(FeedLoading()))
        feedLiveData.postValue(dropLast)
    }

    private fun errorToFeed(error: Boolean = true) {
        feedErrorOld = error
        Timber.d("errorToFeed($error)")
        var fLD = feedLiveData.value?.toMutableList() ?: emptyList()
        if (fLD.last() is FeedLoading) {
            fLD = fLD.dropLast(1)
        }
        feedLiveData.postValue(fLD.plus(FeedLoading(isError = error)))
    }

    fun vote(sr: Subreddit, newVote: Boolean?) {
        currentSearchJob = viewModelScope.launch(handler) {
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
                voteErrorMutable.value = true
                Timber.tag("Vote").d("Error")
                Timber.tag("Vote").e(it)
            }
        }
    }

    fun retry() {
        feedErrorOld = false
        feedErrorMutable.value = false
        appendToFeed(emptyList<FeedLoading>())
        getBestFeed()
    }

    fun onHandledVoteError() {
        voteErrorMutable.value = false
    }

    override fun onCleared() {
        super.onCleared()
        feedError.removeObserver(feedErrorObserver)
    }

}