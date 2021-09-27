package com.example.ktshw1.feed

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ktshw1.R
import com.example.ktshw1.model.FeedImage
import com.example.ktshw1.model.Vote
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_feed_image.view.*
import kotlinx.android.synthetic.main.item_feed_text.view.*
import kotlinx.android.synthetic.main.item_feed_text.view.date_published
import kotlinx.android.synthetic.main.item_feed_text.view.down_button
import kotlinx.android.synthetic.main.item_feed_text.view.subreddit_img
import kotlinx.android.synthetic.main.item_feed_text.view.subreddit_name
import kotlinx.android.synthetic.main.item_feed_text.view.title
import kotlinx.android.synthetic.main.item_feed_text.view.up_button
import kotlinx.android.synthetic.main.item_feed_text.view.user_name
import kotlinx.android.synthetic.main.item_feed_text.view.vote_counter

class FeedImageDelegate(
//    private val onItemClick: (item: feedImage) -> Unit
    private val notifyChanged: (position: Int) -> Any,
) : AbsListItemAdapterDelegate<Any, Any, FeedImageDelegate.ViewHolder>() {

    override fun isForViewType(item: Any, items: MutableList<Any>, position: Int): Boolean {
        return item is FeedImage
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val feedImageView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed_image, parent, false)
        return ViewHolder(feedImageView)
    }

    override fun onBindViewHolder(item: Any, holder: ViewHolder, payloads: MutableList<Any>) {
        holder.bind(item as FeedImage)
    }

    inner class ViewHolder(
        override val containerView: View,
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        private var feedImage: FeedImage? = null

        fun bind(item: FeedImage) = with(containerView) {
            feedImage = item
            subreddit_img.setImageResource(R.color.vote)
            image.setImageResource(R.drawable.ic_launcher_foreground)
            subreddit_name.text = item.base.subredditName
            user_name.text = item.base.userName
            date_published.text = item.base.datePublished.toString() //TODO
            title.text = item.title
            setVoteCount()
            up_button.setOnClickListener {
                vote(Vote.UP)
            }
            down_button.setOnClickListener {
                vote(Vote.DOWN)
            }
            setButtonColors()
            setVoteCount()
        }

        private fun vote(vote: Vote) {
            val oldVote = feedImage?.base?.vote ?: Vote.NOT_VOTED
            if (oldVote != vote) {
                feedImage?.base?.vote = if (oldVote != Vote.NOT_VOTED) Vote.NOT_VOTED else vote
            }
            setButtonColors()
            setVoteCount()
            notifyChanged(layoutPosition)
        }


        private fun setVoteCount() {
            containerView.vote_counter.text =  if (feedImage?.base?.voteCounter != null) {
                val count = feedImage?.base?.voteCounter ?: 0
                when (feedImage?.base?.vote) {
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
            val upButtonColor = if (feedImage?.base?.vote == Vote.UP) R.color.vote
            else R.color.black
            @ColorRes
            val downButtonColor = if (feedImage?.base?.vote == Vote.DOWN) R.color.vote
            else R.color.black
            containerView.down_button.setColorFilter(
                ContextCompat.getColor(
                    containerView.context, downButtonColor
                ),
                PorterDuff.Mode.SRC_IN
            )
            containerView.up_button.setColorFilter(
                ContextCompat.getColor(
                    containerView.context, upButtonColor
                ),
                PorterDuff.Mode.SRC_IN
            )
        }

    }
}
