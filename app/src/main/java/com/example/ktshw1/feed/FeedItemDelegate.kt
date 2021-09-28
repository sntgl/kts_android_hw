package com.example.ktshw1.feed

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ktshw1.R
import com.example.ktshw1.model.FeedContent
import com.example.ktshw1.model.FeedItem
import com.example.ktshw1.model.Vote
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_feed_multi.view.*
import kotlinx.android.synthetic.main.item_feed_multi.view.item_feed_date_published
import kotlinx.android.synthetic.main.item_feed_multi.view.item_feed_down_button
import kotlinx.android.synthetic.main.item_feed_multi.view.item_feed_subreddit_img
import kotlinx.android.synthetic.main.item_feed_multi.view.item_feed_subreddit_name
import kotlinx.android.synthetic.main.item_feed_multi.view.item_feed_title
import kotlinx.android.synthetic.main.item_feed_multi.view.item_feed_up_button
import kotlinx.android.synthetic.main.item_feed_multi.view.item_feed_user_name
import kotlinx.android.synthetic.main.item_feed_multi.view.item_feed_vote_counter

class FeedItemDelegate(
    private val notifyChanged: (position: Int) -> Any,
) : AbsListItemAdapterDelegate<Any, Any, FeedItemDelegate.ViewHolder>() {

    override fun isForViewType(item: Any, items: MutableList<Any>, position: Int): Boolean {
        return item is FeedItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val feedImageView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed_multi, parent, false)
        return ViewHolder(feedImageView)
    }

    override fun onBindViewHolder(item: Any, holder: ViewHolder, payloads: MutableList<Any>) {
        holder.bind(item as FeedItem)
    }

    inner class ViewHolder(
        override val containerView: View,
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        private var feedItem: FeedItem? = null

        fun bind(item: FeedItem) = with(containerView) { //TODO
            feedItem = item
            item_feed_subreddit_img.setImageResource(R.color.vote)
            if (item.content is FeedContent.ImageType) {
                item_feed_image.setImageResource(R.drawable.ic_launcher_foreground)
                item_feed_image.visibility = View.VISIBLE
            } else
                item_feed_image.visibility = View.GONE
            item_feed_subreddit_name.text = item.subredditName
            item_feed_user_name.text = item.userName
            item_feed_date_published.text = item.datePublished.toString()
            item_feed_title.text = item.title
            setVoteCount()
            item_feed_up_button.setOnClickListener {
                vote(Vote.UP)
            }
            item_feed_down_button.setOnClickListener {
                vote(Vote.DOWN)
            }
            setButtonColors()
            setVoteCount()
        }

        private fun vote(vote: Vote) {
            val oldVote = feedItem?.vote ?: Vote.NOT_VOTED
            if (oldVote != vote) {
                feedItem?.vote = if (oldVote != Vote.NOT_VOTED) Vote.NOT_VOTED else vote
            }
            setButtonColors()
            setVoteCount()
            notifyChanged(layoutPosition)
        }


        private fun setVoteCount() {
            containerView.item_feed_vote_counter.text =  if (feedItem?.voteCounter != null) {
                val count = feedItem?.voteCounter ?: 0
                when (feedItem?.vote) {
                    Vote.UP -> count + 1
                    Vote.DOWN -> count - 1
                    Vote.NOT_VOTED -> count
                    else -> containerView.context.getString(R.string.vote_now)
                }.toString()
            } else
                containerView.context.getString(R.string.vote_now)
        }

        fun setButtonColors() {
            @ColorRes
            val upButtonColor = if (feedItem?.vote == Vote.UP) R.color.vote
            else R.color.black
            @ColorRes
            val downButtonColor = if (feedItem?.vote == Vote.DOWN) R.color.vote
            else R.color.black
            containerView.item_feed_down_button.setColorFilter(
                ContextCompat.getColor(
                    containerView.context, downButtonColor
                ),
                PorterDuff.Mode.SRC_IN
            )
            containerView.item_feed_up_button.setColorFilter(
                ContextCompat.getColor(
                    containerView.context, upButtonColor
                ),
                PorterDuff.Mode.SRC_IN
            )
        }

    }
}
