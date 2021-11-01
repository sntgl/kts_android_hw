package com.example.ktshw1.feed

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.ktshw1.R
import com.example.ktshw1.databinding.ItemErrorBinding
import com.example.ktshw1.databinding.ItemFeedMultiBinding
import com.example.ktshw1.databinding.ItemLoadingBinding
import com.example.ktshw1.model.*
import com.example.ktshw1.networking.Subreddit
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import timber.log.Timber
import kotlin.coroutines.coroutineContext


class FeedErrorDelegate (
    private val retry: () -> Any,
    ) : AbsListItemAdapterDelegate<Any, Any, FeedErrorDelegate.FeedErrorDelegateVH>() {

    override fun isForViewType(item: Any, items: MutableList<Any>, position: Int): Boolean {
        return item is FeedError
    }

    override fun onCreateViewHolder(parent: ViewGroup): FeedErrorDelegateVH {
        val binding =
            ItemErrorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedErrorDelegateVH(binding, retry)
    }

    override fun onBindViewHolder(item: Any, holder: FeedErrorDelegateVH, payloads: MutableList<Any>) {
        holder.bind(item as FeedError)
    }

    class FeedErrorDelegateVH(
        private val binding: ItemErrorBinding,
        private val retry: () -> Any,
        ) : RecyclerView.ViewHolder(binding.root) {

        init {
            with(binding) {
                root.setOnClickListener {
                    retry()
                }
            }
        }

        fun bind(item: FeedError) {
            with (binding) {
                Timber.d("Error item is binded!")
                errorText.text = errorText.context.getString(R.string.feed_error)
            }
        }
    }
}
