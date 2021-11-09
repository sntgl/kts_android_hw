package com.example.ktshw1.networking

import Database
import androidx.lifecycle.*
import com.example.ktshw1.connection.ConnectionViewModel
import com.example.ktshw1.model.*
import com.example.ktshw1.utils.SubredditParser
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import kotlin.collections.set


class FeedViewModel(
    private val repository: FeedRepository,
    private val parser: SubredditParser,
    private val connectionViewModel: ConnectionViewModel
) : ViewModel() {
    private val db = Database.instance.feedItemDao()

    private val isLoadingFeedMutable = MutableStateFlow(false)

    private val isRefreshingFeedMutable = MutableStateFlow(false)
    val isRefreshingFeed: StateFlow<Boolean>
        get() = isRefreshingFeedMutable

    private val voteErrorMutable = MutableStateFlow(false)
    val voteError: StateFlow<Boolean>
        get() = voteErrorMutable

    private var feedErrorMutable = MutableStateFlow(false)

    private var currentFeedJob: Job? = null
    private var currentVoteJobs: MutableMap<String, Job> = emptyMap<String, Job>().toMutableMap()
    private var currentSaveJobs: MutableMap<String, Job> = emptyMap<String, Job>().toMutableMap()

    private var after: String = ""

    private val isCachedMutableFlow = MutableStateFlow(false)
    val isCachedFlow: StateFlow<Boolean>
        get() = isCachedMutableFlow

    private val initByNetwork = MutableStateFlow(false)

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
                        db.insertFeedItem(it.toSubredditT())
                        val index = fLD.indexOf(sr)
                        val list = fLD.toMutableList()
                        list[index] = it
                        feedMutableFlow.emit(list)
                    }
                    currentVoteJobs.remove(sr.id)
                }.onFailure {
                    voteErrorMutable.emit(true)
                    val fLD = feedFlow.value
                    val index = fLD.indexOf(sr)
                    val list = fLD.toMutableList()
                    val ss = sr.copy()
                    ss.random_id = UUID.randomUUID()
                    list[index] = ss
                    feedMutableFlow.emit(list)
                    currentVoteJobs.remove(sr.id)
                }
            }
        }
    }

    fun save(sr: Subreddit) {
        if (!currentSaveJobs.containsKey(sr.id)) {
            currentSaveJobs[sr.id]?.cancel()
            currentSaveJobs[sr.id] = viewModelScope.launch {
                runCatching {
                    repository.save(sr.id, !sr.saved)
                }.onSuccess {
                    val fLD = feedFlow.value
                    if (it is Subreddit) {
                        db.insertFeedItem(it.toSubredditT())
                        val index = fLD.indexOf(sr)
                        val list = fLD.toMutableList()
                        list[index] = it
                        feedMutableFlow.emit(list)
                    }
                    currentSaveJobs.remove(sr.id)
                }.onFailure {
                    voteErrorMutable.emit(true)
                    val fLD = feedFlow.value
                    val index = fLD.indexOf(sr)
                    val list = fLD.toMutableList()
                    val ss = sr.copy()
                    ss.random_id = UUID.randomUUID()
                    list[index] = ss
                    feedMutableFlow.emit(list)
                    currentSaveJobs.remove(sr.id)
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

    fun getMoreFeed() {
        Timber.d("Getting more feed...")
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
        Timber.d("Refreshing feed...")
        if (isRefreshingFeedMutable.value) return
        viewModelScope.launch {
            isRefreshingFeedMutable.emit(true)
            feedErrorMutable.emit(false)
            currentFeedJob?.cancel()
            currentFeedJob = viewModelScope.launch {
                viewModelScope.launch {
                    runCatching {
                        repository.getBestFeed("")
                    }.onSuccess {
                        val list = it.first
                        if (list != null) {
                            feedMutableFlow.emit(mutableListOf(FeedLoading()))
                            internalFeedFlow.emit(list)
                            after = it.second ?: after
                        }
                        isCachedMutableFlow.emit(false)
                        isRefreshingFeedMutable.emit(false)
                        initByNetwork.emit(true)
                    }.onFailure {
                        feedErrorMutable.emit(true)
                        isRefreshingFeedMutable.emit(false)
                        if (!initByNetwork.value)
                            loadFromCache()
                    }
                }
            }
        }

    }

    private fun errorToFeed(isError: Boolean) {
        viewModelScope.launch {
            var feedLocal = feedMutableFlow.value
            if (feedLocal.lastOrNull() is FeedLastItem) {
                if (feedLocal.last() is FeedError && isError) return@launch
                if (feedLocal.last() is FeedLoading && !isError) return@launch
                feedLocal = feedLocal.dropLast(1).toMutableList()
            }
            feedMutableFlow.emit(
                feedLocal.plus(if (isError) FeedError() else FeedLoading()).toMutableList()
            )
        }
    }

    private fun addToFeed(it: List<*>): List<*> {
        val value = feedMutableFlow.value
        Timber.i("Previous list size is ${value.size}")
        var dropLast =
            if (value.last() is FeedLastItem) {
                value.dropLast(1)
            } else {
                value
            }
        dropLast = dropLast.plus(it.plus(FeedLoading()))
        Timber.i("Current list size is ${dropLast.size}")
        return dropLast
    }

    private fun loadFromCache() {
        viewModelScope.launch {
            db.observeSubreddits().take(1).collect {
                isCachedMutableFlow.emit(true)
                Timber.d("Filled via DB")
                val list = parser.fromDataBase(it)
                after = list.lastOrNull()?.id ?: after
                internalFeedFlow.emit(list)
            }
        }
    }

    init {
        viewModelScope.launch {
            internalFeedFlow
                .onEach {
                    Timber.d("Got ${it.size} items")
                    val l = parser.toDataBase(it)
                    Timber.d("Result have ${l.size} items")
                    db.insertFeedItems(l)
                }
                .map { inList -> //unique check, может убрать
                    val list = emptyList<Subreddit>().toMutableList()
                    val ids = List(feedFlow.value.size) {
                        if (feedFlow.value[it] is Subreddit)
                            (feedFlow.value[it] as Subreddit).id
                        else
                            ""
                    }
                    inList.forEach {
                        if (!ids.contains(it.id))
                            list.add(it)
                    }
                    list
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

    override fun onCleared() {
        super.onCleared()
    }

}