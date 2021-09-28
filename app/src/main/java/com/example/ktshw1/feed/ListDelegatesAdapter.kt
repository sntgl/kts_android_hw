package com.example.ktshw1.feed

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.example.ktshw1.model.FeedItem
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

class ListDelegatesAdapter
    : AsyncListDifferDelegationAdapter<Any>(DiffCallback()) {

    init {
        delegatesManager.addDelegate(FeedItemDelegate(::notifyItemChanged))
        delegatesManager.addDelegate(FeedLoadDelegate())
    }

    class DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem.javaClass == newItem.javaClass && when (newItem) {
                is FeedItem -> newItem.uuid == (oldItem as FeedItem).uuid
                else -> true
            }
        }


        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean = oldItem == newItem
    }

}

