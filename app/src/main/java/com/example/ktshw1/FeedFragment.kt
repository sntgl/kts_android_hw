package com.example.ktshw1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.ktshw1.databinding.FragmentFeedBinding
import com.example.ktshw1.feed.ListDelegatesAdapter
import com.example.ktshw1.model.*
import com.example.ktshw1.networking.FeedViewModel
import timber.log.Timber
import com.example.ktshw1.utils.autoCleared

class FeedFragment : Fragment(R.layout.fragment_feed) {
    private val binding: FragmentFeedBinding by viewBinding(FragmentFeedBinding::bind)
    private var feedAdapter: ListDelegatesAdapter by autoCleared()
    private val feedViewModel: FeedViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createRecycler()
        Timber.d("Auth token is ${UserInfo.authToken}")
        feedViewModel.voteError.observe(viewLifecycleOwner, {
            if (it == true) {
                feedViewModel.voteError.value = false
                Toast.makeText(context, getString(R.string.vote_error), Toast.LENGTH_SHORT).show()
            }
        })

        feedViewModel.feedError.observe(viewLifecycleOwner, {
            feedAdapter.notifyItemChanged(feedAdapter.itemCount - 1)
        })
        feedViewModel.getBestFeed()
        feedAdapter.items = listOf(FeedLoading())
        feedViewModel.feedList.observe(viewLifecycleOwner, {
            feedAdapter.items = it
        })
    }

    private fun createRecycler() {

        feedAdapter = ListDelegatesAdapter(feedViewModel) { url ->
            val intent = Intent(Intent.ACTION_SEND).apply {
//                    data = Uri.parse("mailto:${complexItem.email}")
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT,
                    getString(R.string.see_this_post)+"\n"+getString(R.string.reddit_base_url)+ url
                )
//                data = Uri.parse(url)
            }
            startActivity(Intent.createChooser(intent, null))
        }
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

    private fun loadMoreItems() {
        feedViewModel.getBestFeed()
    }
}