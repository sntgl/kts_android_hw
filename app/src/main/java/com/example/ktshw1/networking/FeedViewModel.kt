package com.example.ktshw1.networking

import androidx.lifecycle.*
import com.example.ktshw1.SubredditParser
import com.example.ktshw1.connection.ConnectionRepository
import com.example.ktshw1.connection.ConnectionViewModel
import com.example.ktshw1.model.FeedLoading
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber


class FeedViewModel(
    private val repository: FeedRepository,
    private val parser: SubredditParser,
    private val connectionViewModel: ConnectionViewModel
) : ViewModel() {
    private val db = Database.instance.feedItemDao()

    private val isLoadingFeedMutable = MutableStateFlow(false)
    private val isLoadingFeed: StateFlow<Boolean>
        get() = isLoadingFeedMutable

    private val isRefreshingFeedMutable = MutableStateFlow(false)
    val isRefreshingFeed: StateFlow<Boolean>
        get() = isRefreshingFeedMutable

    private val voteErrorMutable = MutableStateFlow(false)
    val voteError: StateFlow<Boolean>
        get() = voteErrorMutable

    private var feedErrorMutable = MutableStateFlow(false)
    private val feedError: StateFlow<Boolean>
        get() = feedErrorMutable

    private var currentFeedJob: Job? = null
    private var currentVoteJobs: MutableMap<String, Job> = emptyMap<String, Job>().toMutableMap()

    private var after: String = ""

    private val isCachedMutableFlow = MutableStateFlow<Boolean>(false)
    val isCachedFlow: StateFlow<Boolean>
        get() = isCachedMutableFlow

    private val feedMutableFlow = MutableStateFlow<List<*>>(listOf(FeedLoading()))
    val feedFlow: StateFlow<List<*>>
        get() = feedMutableFlow

    private val internalFeedFlow = MutableStateFlow<List<Subreddit>>(emptyList())


    fun vote(sr: Subreddit, newVote: Boolean?) {
        if (!currentVoteJobs.containsKey(sr.id)) {
            currentVoteJobs[sr.id]?.cancel()
            currentVoteJobs[sr.id] = viewModelScope.launch {
                runCatching {
                    repository.vote(sr.id, newVote)
                }.onSuccess {
                    val fLD = feedFlow.value
                    if (it is Subreddit) {
                        db.insertFeedItem(it.toSubredditT())
                        val index = fLD.indexOf(sr)
                        val list = fLD.toMutableList()
                        list[index] = it
                        feedMutableFlow.emit(list)
                    }
                    currentVoteJobs.remove(sr.id)
                }.onFailure {
                    voteErrorMutable.emit(true)
                    currentVoteJobs.remove(sr.id)
                }
            }
        }
    }

    fun retry() {
        viewModelScope.launch { feedErrorMutable.emit(false) }
        errorToFeed(false)
        getMoreFeed()
    }

    fun onHandledVoteError() {
        viewModelScope.launch { voteErrorMutable.emit(false) }
    }




    private suspend fun addToFeed(it: List<*>): List<*> {
        var dropLast =
            if (!isRefreshingFeed.value) {
                if (feedFlow.value.last() is FeedLoading)
                    feedFlow.value.dropLast(1)
                else
                    emptyList()
            } else {emptyList()}
        dropLast = dropLast.plus(it.plus(FeedLoading()))
        Timber.i("Current list size is ${dropLast.size}")
//        if (isRefreshingFeed.value)
//            isRefreshingFeedMutable.emit(false)
        return dropLast
    }

    fun getMoreFeed() {
        if (isLoadingFeed.value) return
        viewModelScope.launch {
            isLoadingFeedMutable.emit(true)
            feedErrorMutable.emit(false)
            currentFeedJob?.cancel()
            currentFeedJob = viewModelScope.launch {
                viewModelScope.launch {
                    runCatching {
                        repository.getBestFeed(after)
                    }.onSuccess {
                        internalFeedFlow.emit(it.first ?: emptyList<Subreddit>())
                        after = it.second ?: after
                        isCachedMutableFlow.emit(false)
                        isLoadingFeedMutable.emit(false)
                    }.onFailure {
                        feedErrorMutable.emit(true)
                        isLoadingFeedMutable.emit(false)
                    }
                }
            }
        }

    }

    fun refreshFeed() {
        if (isRefreshingFeed.value) return
        viewModelScope.launch {
            isRefreshingFeedMutable.emit(true)
            feedErrorMutable.emit(false)
            currentFeedJob?.cancel()
            currentFeedJob = viewModelScope.launch {
                viewModelScope.launch {
                    runCatching {
                        Timber.d("Trying to refresh..")
                        repository.getBestFeed("")
                    }.onSuccess {
                        Timber.d("Refresh success..")
                        feedMutableFlow.emit(addToFeed(emptyList<Subreddit>()))
                        val list = it.first
                        if (list != null)
                            internalFeedFlow.emit(list)
                        after = it.second ?: after
                        isCachedMutableFlow.emit(false)
                        isRefreshingFeedMutable.emit(false)
                    }.onFailure {
                        Timber.d("Refresh fail..")
                        feedErrorMutable.emit(true)
                        isRefreshingFeedMutable.emit(false)
                    }
                }
            }
        }

    }

    private fun errorToFeed(error: Boolean = true) {
        viewModelScope.launch {
            var fLD = feedFlow.value.toMutableList()
            if (fLD.last() is FeedLoading) {
                fLD = fLD.dropLast(1).toMutableList()
            }
            feedMutableFlow.emit(fLD.plus(FeedLoading(isError = error)))
        }
    }

    init {
        viewModelScope.launch {
            db.observeSubreddits().take(1).collect {
                isCachedMutableFlow.emit(true)
                Timber.d("Filled via DB")
                val list = parser.fromDataBase(it)
                after = list.lastOrNull()?.id ?: after
                internalFeedFlow.emit(list)
            }
        }
        viewModelScope.launch {
            internalFeedFlow
//                .flowOn(Dispatchers.IO) //TODO
                .onEach {
                    Timber.d("Got ${it.size} items")
                    val l = parser.toDataBase(it)
                    Timber.d("Result have ${l.size} items")
                    db.insertFeedItems(l)
                }
//                .map { inList -> //unique check TODO delete
//                    val list = emptyList<Subreddit>().toMutableList()
//                    val ids = List<String>(feedFlow.value.size) {
//                        if (feedFlow.value[it] is Subreddit)
//                            (feedFlow.value[it] as Subreddit).id
//                        else
//                            ""
//                    }
//                    inList.forEach {
//                        if (!ids.contains(it.id))
//                            list.add(it)
//                    }
//                    list
//                }
                .map { addToFeed(it) }
                .collect { feedMutableFlow.emit(it) }
        }
        viewModelScope.launch {
            feedError
                .onEach { Timber.d("FeedError is $it") }
                .collect { errorToFeed(it) }
        }
    }

}