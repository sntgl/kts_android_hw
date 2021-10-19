package com.example.ktshw1.networking

import androidx.lifecycle.*
import com.example.ktshw1.SubredditParser
import com.example.ktshw1.db.SubredditT
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktshw1.connection.ConnectionViewModel
import com.example.ktshw1.model.FeedError
import com.example.ktshw1.model.FeedLastItem
import com.example.ktshw1.model.FeedLoading
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import timber.log.Timber
import kotlinx.coroutines.launch
import studio.kts.android.school.lection4.networking.data.FeedRepositoryInterface
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.dropLast
import kotlin.collections.emptyList
import kotlin.collections.emptyMap
import kotlin.collections.indexOf
import kotlin.collections.last
import kotlin.collections.mutableListOf
import kotlin.collections.plus
import kotlin.collections.set
import kotlin.collections.toMutableList
import kotlin.collections.toMutableMap


class FeedViewModel(
    private val repository: FeedRepository,
    private val connectionViewModel: ConnectionViewModel
) : ViewModel() {

    private val isLoadingFeedMutable = MutableStateFlow(false)

    private val voteErrorMutable = MutableStateFlow(false)
    val voteError: StateFlow<Boolean>
        get() = voteErrorMutable

    private var feedErrorMutable = MutableStateFlow(false)

    private var currentFeedJob: Job? = null
    private var currentVoteJobs: MutableMap<String, Job> = emptyMap<String, Job>().toMutableMap()

    private var after: String = ""

    private val feedMutableFlow = MutableStateFlow<MutableList<*>>(mutableListOf(FeedLoading()))
    val feedFlow: StateFlow<MutableList<*>>
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
                }
                .map { addToFeed(it) }
                .collect { feedMutableFlow.emit(it.toMutableList()) }
        }
        viewModelScope.launch {
            feedErrorMutable
                .onEach { Timber.d("FeedError is $it") }
                .distinctUntilChanged()
                .collect { errorToFeed(it) }
        }
        viewModelScope.launch {
            connectionViewModel.connectionFlow
                .filter { it && feedErrorMutable.value }
                .collect {
                    Timber.d("Connection returned, retrying...")
                    getMoreFeed()
                }
        }
    }

    private fun addToFeed(it: List<*>): List<*> {
        var dropLast = if (feedMutableFlow.value.last() is FeedLastItem)
            feedMutableFlow.value.dropLast(1) else feedMutableFlow.value
        dropLast = dropLast.plus(it.plus(FeedLoading()))
        return dropLast
    }

    fun getMoreFeed() {
        if (isLoadingFeedMutable.value) return
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

    private fun errorToFeed(isError: Boolean) {
        viewModelScope.launch {
            var feedLocal = feedMutableFlow.value
            if (feedLocal.last() is FeedLastItem) {
                if (feedLocal.last() is FeedError && isError) return@launch
                if (feedLocal.last() is FeedLoading && !isError) return@launch
                feedLocal = feedLocal.dropLast(1).toMutableList()
            }
            feedMutableFlow.emit(
                feedLocal.plus(if (isError) FeedError() else FeedLoading()).toMutableList()
            )
        }
    }
}