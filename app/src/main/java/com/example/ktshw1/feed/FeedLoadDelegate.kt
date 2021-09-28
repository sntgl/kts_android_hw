package com.example.ktshw1.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ktshw1.R
import com.example.ktshw1.model.*
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import kotlinx.android.extensions.LayoutContainer

class FeedLoadDelegate(
) : AbsListItemAdapterDelegate<Any, Any, FeedLoadDelegate.ViewHolder>() {

    override fun isForViewType(item: Any, items: MutableList<Any>, position: Int): Boolean {
        return item is FeedLoading
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val feedLoadView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_loading, parent, false)
        return ViewHolder(feedLoadView)
    }

    override fun onBindViewHolder(item: Any, holder: ViewHolder, payloads: MutableList<Any>) {}

    inner class ViewHolder(
        override val containerView: View,
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer
}
