package com.example.ktshw1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ktshw1.model.FeedText
import com.example.ktshw1.model.Vote
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_feed_text.view.*

class FeedTextDelegate(
    private val onItemClick: (item: FeedText) -> Unit
) : AbsListItemAdapterDelegate<Any, Any, FeedTextDelegate.ViewHolder>() {

    override fun isForViewType(item: Any, items: MutableList<Any>, position: Int): Boolean {
        return item is FeedText
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val feedTextView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed_text, parent, false)
        return ViewHolder(feedTextView, onItemClick)
    }

    override fun onBindViewHolder(item: Any, holder: ViewHolder, payloads: MutableList<Any>) {
        holder.bind(item as FeedText)
    }

    inner class ViewHolder(
        override val containerView: View,
        onItemClick: (item: FeedText) -> Unit,
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        private var feedText: FeedText? = null

        fun bind(item: FeedText) = with(containerView) {
            feedText = item
//            subreddit_img.setImage = item.image//TODO
            subreddit_name.text = item.subredditName
            user_name.text = item.userName
            date_published.text = item.datePublished.toString() //TODO
            title.text = item.title
            vote_counter.text = item.voteCounter?.toString()
                ?: context.getString(R.string.vote_now)
            //TODO

            setButtonColors(item, up_button, down_button, context)

            up_button.setOnClickListener {
                item.vote = Vote.UP
                setButtonColors(item, up_button, down_button, context)
            }
            down_button.setOnClickListener {
                item.vote = Vote.DOWN
                setButtonColors(item, up_button, down_button, context)
            }
        }

        fun setButtonColors(item: FeedText, upButton: ImageView, downButton: ImageView, context: Context) {
            if (item.vote == Vote.UP) {
                upButton.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.vote)
                )
                downButton.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.black)
                )
            } else {
                downButton.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.vote)
                )
                upButton.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.black)
                )
            }
        }

    }
}
