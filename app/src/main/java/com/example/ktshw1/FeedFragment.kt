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
import com.example.ktshw1.model.FeedBase
import com.example.ktshw1.model.FeedImage
import com.example.ktshw1.model.FeedText
import timber.log.Timber
import java.util.*

class FeedFragment : Fragment(R.layout.fragment_feed) {
    private val binding: FragmentFeedBinding by viewBinding(FragmentFeedBinding::bind)
    private var feedAdapter: ListDelegatesAdapter by autoCleared()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createRecycler()
        loadMoreItems()
    }

    private fun createRecycler() {
        feedAdapter = ListDelegatesAdapter()
        with (binding.feed) {
            adapter = feedAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
            setHasFixedSize(true)
        }
        feedAdapter.items = getRandomItems()
    }


    private fun getRandomItems() = List(20) {

        when ((1..2).random()) {
            1 -> FeedText(
                base = createBase(),
                title = "A lot of text here.. A lot of text here.. A lot of text here.. ",
            )
            2 -> FeedImage(
                base = createBase(),
                title = "Image title here",
                image = "IMAGE HERE"
            )
            else -> error("Wrong random number")
        }
    }

    private fun createBase(): FeedBase {
        return FeedBase(
            uuid = UUID.randomUUID(),
            datePublished = (1000689510..1632689510).random(),
            subredditName = "r/SubredditName",
            subredditImage = "",
            userName = "RedditerUserName",
            voteCounter = (0..10000).random()
        )
    }

    private fun loadMoreItems() {
        val newItems = feedAdapter.items.toMutableList() + getRandomItems()
        feedAdapter.items = newItems
        Timber.d("Pagination ${newItems.size}")
    }
}