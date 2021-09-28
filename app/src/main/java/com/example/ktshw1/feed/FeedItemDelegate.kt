package com.example.ktshw1.feed

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ktshw1.R
import com.example.ktshw1.databinding.ItemFeedMultiBinding
import com.example.ktshw1.model.FeedContent
import com.example.ktshw1.model.FeedItem
import com.example.ktshw1.model.Vote
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate

class FeedItemDelegate(
    private val notifyChanged: (position: Int) -> Any,
) : AbsListItemAdapterDelegate<Any, Any, FeedItemDelegate.FeedItemDelegateVH>() {

    override fun isForViewType(item: Any, items: MutableList<Any>, position: Int): Boolean {
        return item is FeedItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): FeedItemDelegateVH {
        val binding = ItemFeedMultiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedItemDelegateVH(binding, notifyChanged)
    }

    override fun onBindViewHolder(item: Any, holder: FeedItemDelegateVH, payloads: MutableList<Any>) {
        holder.bind(item as FeedItem)
    }

    class FeedItemDelegateVH(
        private val binding: ItemFeedMultiBinding,
        private val notifyChanged: (position: Int) -> Any
        ) : RecyclerView.ViewHolder(binding.root) {
        private var feedItem: FeedItem? = null

        fun bind(item: FeedItem) { //TODO
            feedItem = item
            with (binding) {
                itemFeedSubredditImg.setImageResource(R.color.vote)
                if (item.content is FeedContent.ImageType) {
                    itemFeedImage.setImageResource(R.drawable.ic_launcher_foreground)
                    itemFeedImage.visibility = View.VISIBLE
                } else
                    itemFeedImage.visibility = View.GONE
                itemFeedSubredditName.text = item.subredditName
                itemFeedUserName.text = item.userName
                itemFeedDatePublished.text = item.datePublished.toString()
                itemFeedTitle.text = item.title
                setVoteCount()
                itemFeedUpButton.setOnClickListener {
                    vote(Vote.UP)
                }
                itemFeedDownButton.setOnClickListener {
                    vote(Vote.DOWN)
                }

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
            binding.itemFeedVoteCounter.text = if (feedItem?.voteCounter != null) {
                val count = feedItem?.voteCounter ?: 0
                when (feedItem?.vote) {
                    Vote.UP -> count + 1
                    Vote.DOWN -> count - 1
                    Vote.NOT_VOTED -> count
                    else -> binding.root.context.getString(R.string.vote_now)
                }.toString()
            } else
                binding.root.context.getString(R.string.vote_now)
        }

        private fun setButtonColors() {
            @ColorRes
            val upButtonColor = if (feedItem?.vote == Vote.UP) R.color.vote
            else R.color.black

            @ColorRes
            val downButtonColor = if (feedItem?.vote == Vote.DOWN) R.color.vote
            else R.color.black
            binding.itemFeedDownButton.setColorFilter(
                ContextCompat.getColor(
                    binding.root.context ?: error("context broken"), downButtonColor
                ),
                PorterDuff.Mode.SRC_IN
            )
            binding.itemFeedUpButton.setColorFilter(
                ContextCompat.getColor(
                    binding.root.context, upButtonColor
                ),
                PorterDuff.Mode.SRC_IN
            )
        }

    }
}
