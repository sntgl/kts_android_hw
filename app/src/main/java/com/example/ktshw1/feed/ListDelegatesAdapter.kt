package com.example.ktshw1.feed

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.leanback.widget.DiffCallback
import androidx.recyclerview.widget.DiffUtil
import com.example.ktshw1.model.FeedImage
import com.example.ktshw1.model.FeedText
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import java.util.*

class ListDelegatesAdapter ()
    : AsyncListDifferDelegationAdapter<Any>(DiffCallback()) {

    init {
        delegatesManager.addDelegate(FeedTextDelegate(::notifyItemChanged))
        delegatesManager.addDelegate(FeedImageDelegate(::notifyItemChanged))
    }

    class DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem.javaClass == newItem.javaClass && when (newItem) {
                is FeedImage -> newItem.base.uuid == (oldItem as FeedImage).base.uuid
                is FeedText -> newItem.base.uuid == (oldItem as FeedText).base.uuid
                else -> true
            }
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean = oldItem == newItem
    }

}

