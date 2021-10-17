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
import com.example.ktshw1.databinding.ItemFeedMultiBinding
import com.example.ktshw1.databinding.ItemLoadingBinding
import com.example.ktshw1.model.*
import com.example.ktshw1.networking.Subreddit
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import timber.log.Timber
import kotlin.coroutines.coroutineContext

//import kotlinx.android.extensions.LayoutContainer

class FeedLoadDelegate (
    private val retry: () -> Any,
    private val notifyChanged: (position: Int) -> Any,
    ) : AbsListItemAdapterDelegate<Any, Any, FeedLoadDelegate.FeedLoadDelegateVH>() {

    override fun isForViewType(item: Any, items: MutableList<Any>, position: Int): Boolean {
        return item is FeedLoading
    }

    override fun onCreateViewHolder(parent: ViewGroup): FeedLoadDelegateVH {
        val binding =
            ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedLoadDelegateVH(binding, retry, notifyChanged)
    }

    override fun onBindViewHolder(item: Any, holder: FeedLoadDelegateVH, payloads: MutableList<Any>) {
        holder.bind(item as FeedLoading)
    }

    class FeedLoadDelegateVH(
        private val binding: ItemLoadingBinding,
        private val retry: () -> Any,
        private val notifyChanged: (position: Int) -> Any,
        ) : RecyclerView.ViewHolder(binding.root) {

        init {
            with(binding) {
                root.setOnClickListener {
                    retry()
                    notifyChanged(layoutPosition)
                }
            }
        }

        fun bind(item: FeedLoading) {
            with (binding) {
                Timber.d("Error state is ${item.isError}")
                if (item.isError) {
                    errorText.text = errorText.context.getString(R.string.feed_error)
                    errorImage.isVisible = true
                    errorText.isVisible = true
                    progressBar.isGone = true
                } else {
                    errorImage.isGone = true
                    errorText.isGone = true
                    progressBar.isVisible = true
                }
            }

        }
    }
}
