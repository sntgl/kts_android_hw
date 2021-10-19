package com.example.ktshw1.feed

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber


class FeedPagination(
    private val layoutManager: LinearLayoutManager,
    private val requestNextItems: () -> Unit,
    private val visibilityThreshold: Int = DEFAULT_VISIBILITY_THRESHOLD
) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        with (layoutManager) {
            if (dy <= 0) return

            val scrolledOffItems = findFirstVisibleItemPosition()
            val visibleItems = childCount
            val itemsTotal = itemCount

            if (visibleItems + scrolledOffItems + visibilityThreshold >= itemsTotal) {
                Timber.d("Request next items")
                requestNextItems.invoke()
                //При работе с текстом добавить статус "запрос отправлен",
                //чтобы новые запросы не отправлялись
            }
        }
    }
    companion object {
        const val DEFAULT_VISIBILITY_THRESHOLD = 5
    }
}