package com.example.ktshw1

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.speech.RecognizerIntent
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.ktshw1.connection.ConnectionViewModel
import com.example.ktshw1.databinding.FragmentFeedBinding
import com.example.ktshw1.feed.FeedPagination
import com.example.ktshw1.feed.ListDelegatesAdapter
import com.example.ktshw1.model.*
import com.example.ktshw1.networking.FeedViewModel
import timber.log.Timber
import com.example.ktshw1.utils.autoCleared
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeedFragment : Fragment(R.layout.fragment_feed) {
    private val binding: FragmentFeedBinding by viewBinding(FragmentFeedBinding::bind)
    private var feedAdapter: ListDelegatesAdapter by autoCleared()
    private val feedViewModel: FeedViewModel by viewModel()
    private val connectionViewModel: ConnectionViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createRecycler()
        Timber.d("Auth token is ${UserInfo.authToken}")
        feedAdapter.items = listOf(FeedLoading())
        viewLifecycleOwner.lifecycleScope.launch {
            feedViewModel.feedFlow.collect {
                feedAdapter.items = it
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            feedViewModel.voteError.filter { it }.collect {
                feedViewModel.onHandledVoteError()
                Toast.makeText(context, getString(R.string.vote_error), Toast.LENGTH_SHORT).show()
            }
        }
        feedViewModel.refreshFeed()
        viewLifecycleOwner.lifecycleScope.launch {

            combine(connectionViewModel.connectionFlow, feedViewModel.isCachedFlow) {
                conn, cached -> conn to cached
            }.collect { (conn, cached) ->
                Timber.d("Plate: cached = $cached, networkError = $conn" )
                if (cached) {
                    showErrorPlate(true, getString(R.string.cached))
                } else if (!conn) {
                    showErrorPlate(true, getString(R.string.network_fail))
                } else {
                    showErrorPlate(false)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            feedViewModel.isRefreshingFeed.filter { !it }.collect {
                binding.feedRefresh.isRefreshing = it
            }
        }


    }


    private fun showErrorPlate(show: Boolean, text: String = "") {
        binding.feedErrorPlate.isGone = !show
        binding.feedErrorPlate.isVisible = show
        if (show) binding.feedErrorPlate.text = text
    }

    private fun createRecycler() {
//        feedAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW

        feedAdapter = ListDelegatesAdapter(feedViewModel) { url ->
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    getString(R.string.see_this_post) + "\n" + getString(R.string.reddit_base_url) + url
                )
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
        binding.feedRefresh.setOnRefreshListener {
            Timber.i("Hey i want to refresh!!")
            feedViewModel.refreshFeed()
        }
    }

    private var managerState: Parcelable? = null

    private fun loadMoreItems() {
        feedViewModel.getMoreFeed()
    }

    override fun onPause() {
        super.onPause()
        managerState = binding.feed.layoutManager?.onSaveInstanceState()
    }

    override fun onResume() {
        super.onResume()
        binding.feed.layoutManager?.onRestoreInstanceState(managerState)
    }
}