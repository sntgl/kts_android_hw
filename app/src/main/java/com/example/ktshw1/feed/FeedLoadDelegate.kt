package com.example.ktshw1.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ktshw1.databinding.ItemLoadingBinding
import com.example.ktshw1.model.FeedLoading
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import timber.log.Timber


class FeedLoadDelegate : AbsListItemAdapterDelegate<Any, Any, FeedLoadDelegate.FeedLoadDelegateVH>() {

    override fun isForViewType(item: Any, items: MutableList<Any>, position: Int): Boolean {
        return item is FeedLoading
    }

    override fun onCreateViewHolder(parent: ViewGroup): FeedLoadDelegateVH {
        val binding =
            ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedLoadDelegateVH(binding)
    }

    override fun onBindViewHolder(
        item: Any,
        holder: FeedLoadDelegateVH,
        payloads: MutableList<Any>
    ) {
        holder.bind(item as FeedLoading)
    }

    class FeedLoadDelegateVH(
        private val binding: ItemLoadingBinding,
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(item: FeedLoading) {
            Timber.d("Loading item is binded!")
        }
    }
}
