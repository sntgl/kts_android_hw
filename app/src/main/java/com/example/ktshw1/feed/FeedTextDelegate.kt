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
import com.example.ktshw1.model.FeedText
import com.example.ktshw1.model.Vote
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_feed_text.view.*
import timber.log.Timber

class FeedTextDelegate(
//    private val onItemClick: (item: FeedText) -> Unit
    private val notifyChanged: (position: Int) -> Any,
) : AbsListItemAdapterDelegate<Any, Any, FeedTextDelegate.ViewHolder>() {

    override fun isForViewType(item: Any, items: MutableList<Any>, position: Int): Boolean {
        return item is FeedText
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val feedTextView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed_text, parent, false)
        return ViewHolder(feedTextView)
    }

    override fun onBindViewHolder(item: Any, holder: ViewHolder, payloads: MutableList<Any>) {
        holder.bind(item as FeedText)
    }

    inner class ViewHolder(
        override val containerView: View,
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        private var feedText: FeedText? = null

        fun bind(item: FeedText) = with(containerView) {
            feedText = item
            subreddit_img.setImageResource(R.color.black)
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
            val oldVote = feedText?.base?.vote ?: Vote.NOT_VOTED
            if (oldVote != vote) {
                feedText?.base?.vote = if (oldVote != Vote.NOT_VOTED) Vote.NOT_VOTED else vote
            }
            setVoteCount()
            setButtonColors()
            notifyChanged(layoutPosition)
        }


        private fun setVoteCount() {
            containerView.vote_counter.text =  if (feedText?.base?.voteCounter != null) {
                val count = feedText?.base?.voteCounter ?: 0
                when (feedText?.base?.vote) {
                    Vote.UP -> count + 1
                    Vote.DOWN -> count - 1
                    Vote.NOT_VOTED -> count
                    else -> containerView.context.getString(R.string.vote_now)
                }.toString()
            } else
                containerView.context.getString(R.string.vote_now)
        }

        private fun setButtonColors() {
            @ColorRes
            val upButtonColor = if (feedText?.base?.vote == Vote.UP) R.color.vote
            else R.color.black
            @ColorRes
            val downButtonColor = if (feedText?.base?.vote == Vote.DOWN) R.color.vote
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
