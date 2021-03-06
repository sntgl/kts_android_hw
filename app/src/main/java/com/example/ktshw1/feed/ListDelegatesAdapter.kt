package com.example.ktshw1.feed

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.example.ktshw1.model.FeedError
import com.example.ktshw1.model.FeedLoading
import com.example.ktshw1.model.Subreddit
import com.example.ktshw1.networking.FeedViewModel
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

class ListDelegatesAdapter(
    private val feedViewModel: FeedViewModel,
    share: (url: String) -> Any
) : AsyncListDifferDelegationAdapter<Any>(DiffCallback()) {

    init {
        delegatesManager.addDelegate(
            FeedItemDelegate(
                ::notifyItemChanged,
                feedViewModel::vote,
                feedViewModel::save,
                share
            )
        )
        delegatesManager.addDelegate(FeedLoadDelegate())
        delegatesManager.addDelegate(FeedErrorDelegate(feedViewModel::retry))
    }

    class DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem.javaClass == newItem.javaClass && when (newItem) {
                is Subreddit -> newItem.id == (oldItem as Subreddit).id
                is FeedLoading -> newItem.id == (oldItem as FeedLoading).id
                is FeedError -> newItem.id == (oldItem as FeedError).id
                else -> true
            }
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean = oldItem == newItem
    }
}

