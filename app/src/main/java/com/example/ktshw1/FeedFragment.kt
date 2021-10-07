package com.example.ktshw1

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.ktshw1.databinding.FragmentFeedBinding
import com.example.ktshw1.feed.ListDelegatesAdapter
import com.example.ktshw1.model.*
import timber.log.Timber
import java.util.*
import com.example.ktshw1.utils.autoCleared

class FeedFragment : Fragment(R.layout.fragment_feed) {
    private val binding: FragmentFeedBinding by viewBinding(FragmentFeedBinding::bind)
    private var feedAdapter: ListDelegatesAdapter by autoCleared()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createRecycler()
        loadMoreItems()
        Timber.d("Auth token is ${UserInfo.authToken}")
    }

    private fun createRecycler() {
        feedAdapter = ListDelegatesAdapter()
        with(binding.feed) {
            adapter = feedAdapter
            val orientation = RecyclerView.VERTICAL
            layoutManager = LinearLayoutManager(context, orientation, false)
            addOnScrollListener(
                FeedPagination(
                    layoutManager as LinearLayoutManager,
                    ::loadMoreItems
                )
            )
            addItemDecoration(DividerItemDecoration(context, orientation))
            setHasFixedSize(true)
        }
    }

    private fun getRandomItems() = List(10) {
        FeedItem(
            uuid = UUID.randomUUID(),
            datePublished = (1000689510..1632689510).random(),
            subredditName = "r/SubredditName",
            subredditImage = "",
            userName = "RedditerUserName",
            voteCounter = (0..10000).random(),
            title = "Title here",
            content = when ((1..2).random()) {
                1 -> FeedContent.ImageType(
                    image = "IMAGE HERE"
                )
                2 -> FeedContent.OnlyTitleType
                else -> error("Randomizer broken")
            }
        )
    }

    private fun loadMoreItems() {
        val newItems = feedAdapter.items.toMutableList().apply {
            if (lastOrNull() is FeedLoading) {
                removeLastOrNull()
            }
        } + getRandomItems() + FeedLoading()
        feedAdapter.items = newItems
        Timber.d("Pagination ${newItems.size}")
    }
}