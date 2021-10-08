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
import com.example.ktshw1.networking.Subreddit
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate


class FeedItemDelegate(
    private val notifyChanged: (position: Int) -> Any,
    private val voteVM: (id: String, newVote: Boolean?) -> Any,
) : AbsListItemAdapterDelegate<Any, Any, FeedItemDelegate.FeedItemDelegateVH>() {



    override fun isForViewType(item: Any, items: MutableList<Any>, position: Int): Boolean {
        return item is Subreddit
    }

    override fun onCreateViewHolder(parent: ViewGroup): FeedItemDelegateVH {
        val binding = ItemFeedMultiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedItemDelegateVH(binding, notifyChanged, voteVM)
    }

    override fun onBindViewHolder(item: Any, holder: FeedItemDelegateVH, payloads: MutableList<Any>) {
        holder.bind(item as Subreddit)
    }

    class FeedItemDelegateVH(
        private val binding: ItemFeedMultiBinding,
        private val notifyChanged: (position: Int) -> Any,
        private val voteVM: (id: String, newVote: Boolean?) -> Any,

        ) : RecyclerView.ViewHolder(binding.root) {
        private var feedItem: Subreddit? = null

        fun bind(item: Subreddit) { //TODO
            //Glide, Coil, Picasso
            feedItem = item
            with (binding) {
//                itemFeedSubredditImg.setImageResource(R.color.vote)


//                if (item.content is FeedContent.ImageType) {
//                    itemFeedImage.setImageResource(R.drawable.ic_launcher_foreground)
//                    itemFeedImage.visibility = View.VISIBLE
//                } else
//                    itemFeedImage.visibility = View.GONE
                itemFeedImage.visibility = View.GONE

                itemFeedSubredditName.text = item.subreddit_name
                itemFeedUserName.text = item.author
                itemFeedDatePublished.text = item.created.toString()
                itemFeedTitle.text = item.title
//                setVoteCount()
                itemFeedVoteCounter.text = item.score.toString()
                itemFeedUpButton.setOnClickListener {
                    vote(true)
                }
                itemFeedDownButton.setOnClickListener {
                    vote(false)
                }

            }
            setButtonColors()
//            setVoteCount()
        }

        private fun vote(vote: Boolean) {
            val oldVote = feedItem?.vote
            if (oldVote != vote) {
                voteVM(
                    feedItem?.id ?: "",
                    if (oldVote != null) null else vote
                )
            }
            setButtonColors()
//            setVoteCount()
            notifyChanged(layoutPosition)
        }


//        private fun setVoteCount() {
//            binding.itemFeedVoteCounter.text = if (feedItem?.voteCounter != null) {
//                when (feedItem?.vote) {
//                    Vote.UP -> count + 1
//                    Vote.DOWN -> count - 1
//                    Vote.NOT_VOTED -> count
//                    else -> binding.root.context.getString(R.string.vote_now)
//                }.toString()
//            } else
//                binding.root.context.getString(R.string.vote_now)
//        }

        private fun setButtonColors() {
            @ColorRes
            val upButtonColor = if (feedItem?.vote == true) R.color.vote
            else R.color.black

            @ColorRes
            val downButtonColor = if (feedItem?.vote == false) R.color.vote
            else R.color.black

            binding.itemFeedDownButton.setColorFilter(
                ContextCompat.getColor(binding.root.context, downButtonColor),
                PorterDuff.Mode.SRC_IN
            )
            binding.itemFeedUpButton.setColorFilter(
                ContextCompat.getColor(binding.root.context, upButtonColor),
                PorterDuff.Mode.SRC_IN
            )
        }
//
    }
}
