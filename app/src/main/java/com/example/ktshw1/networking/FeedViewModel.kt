package com.example.ktshw1.networking

import androidx.lifecycle.*
import com.example.ktshw1.SubredditParser
import com.example.ktshw1.db.SubredditT
import com.example.ktshw1.model.FeedLoading
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber


class FeedViewModel(
    private val repository: FeedRepository
) : ViewModel() {

    private val isLoadingFeedMutable = MutableStateFlow(false)
    private val isLoadingFeed: StateFlow<Boolean>
        get() = isLoadingFeedMutable

    private val voteErrorMutable = MutableStateFlow(false)
    val voteError: StateFlow<Boolean>
        get() = voteErrorMutable

    private var feedErrorMutable = MutableStateFlow(false)
    private val feedError: StateFlow<Boolean>
        get() = feedErrorMutable

    private var currentFeedJob: Job? = null
    private var currentVoteJobs: MutableMap<String, Job> = emptyMap<String, Job>().toMutableMap()

    private var after: String = ""

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
                        d.insertFeedItem(it.toSubredditT())
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

    private val d = Database.instance.feedItemDao()

    init {
        viewModelScope.launch {
            internalFeedFlow
//                .flowOn(Dispatchers.IO) //TODO
                .onEach {
                    Timber.d("Got ${it.size} items")
                    val l = SubredditParser().toDataBase(it)
                    Timber.d("Result have ${l.size} items")
                    d.insertFeedItems(l)
//                    val s: MutableList<SubredditT> = emptyList<SubredditT>().toMutableList()
//                    it.forEach { item ->
//                        if (item is Subreddit)
//                            s.add(SubredditT(item.id))
//                    }
//                    d.insertFeedItems(s)
                }
                .map { addToFeed(it) }
                .collect { feedMutableFlow.emit(it) }
        }
        viewModelScope.launch {
            feedError
                .onEach { Timber.d("FeedError is $it") }
                .collect { errorToFeed(it) }
        }
    }

    private fun addToFeed(it: List<*>): List<*> {
        var dropLast = if (feedFlow.value.last() is FeedLoading)
            feedFlow.value.dropLast(1) else emptyList()
        dropLast = dropLast.plus(it.plus(FeedLoading()))
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
                        isLoadingFeedMutable.emit(false)
                    }.onFailure {
                        feedErrorMutable.emit(true)
                        isLoadingFeedMutable.emit(false)
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
}