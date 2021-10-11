package com.example.ktshw1.feed

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.ktshw1.R
import com.example.ktshw1.databinding.ItemFeedMultiBinding
import com.example.ktshw1.networking.Subreddit
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import timber.log.Timber
import kotlin.coroutines.coroutineContext


class FeedItemDelegate(
    private val notifyChanged: (position: Int) -> Any,
    private val voteVM: (subreddit: Subreddit, newVote: Boolean?) -> Any,
) : AbsListItemAdapterDelegate<Any, Any, FeedItemDelegate.FeedItemDelegateVH>() {


    override fun isForViewType(item: Any, items: MutableList<Any>, position: Int): Boolean {
        return item is Subreddit
    }

    override fun onCreateViewHolder(parent: ViewGroup): FeedItemDelegateVH {
        val binding =
            ItemFeedMultiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedItemDelegateVH(binding, notifyChanged, voteVM)
    }

    override fun onBindViewHolder(
        item: Any,
        holder: FeedItemDelegateVH,
        payloads: MutableList<Any>
    ) {
        holder.bind(item as Subreddit)
    }

    class FeedItemDelegateVH(
        private val binding: ItemFeedMultiBinding,
        private val notifyChanged: (position: Int) -> Any,
        private val voteVM: (subreddit: Subreddit, newVote: Boolean?) -> Any,

        ) : RecyclerView.ViewHolder(binding.root) {
        private var feedItem: Subreddit? = null

        init {
            with(binding) {
                itemFeedUpButton.setOnClickListener {
                    vote(true)
                }
                itemFeedDownButton.setOnClickListener {
                    vote(false)
                }
            }
        }

        fun bind(item: Subreddit) {
            //Glide, Coil, Picasso
            feedItem = item
            with(binding) {
//                if (item.thumbnail != "self") {
                loadImage(binding, item)
                itemFeedCommentNumber.text = feedItem?.num_comments.toString()

//                itemFeedSubredditImg.setImageResource(R.color.vote)


//                if (item.content is FeedContent.ImageType) {
//                    itemFeedImage.setImageResource(R.drawable.ic_launcher_foreground)
//                    itemFeedImage.visibility = View.VISIBLE
//                } else
//                    itemFeedImage.visibility = View.GONE
//                itemFeedImage.visibility = View.GONE

                itemFeedSubredditName.text = item.subreddit_name
                itemFeedUserName.text = item.author
                itemFeedDatePublished.text = item.created.toString()
                itemFeedTitle.text = item.title
//                setVoteCount()
                itemFeedVoteCounter.text = item.score.toString()
            }
            setButtonColors()
        }

        private fun loadImage(binding: ItemFeedMultiBinding, item: Subreddit) {
            with(binding) {
                if (item.url.endsWith(".jpg", true) ||
                    item.url.endsWith(".jpeg", true) ||
                    item.url.endsWith(".png", true)
                ) {
                    itemFeedImage.visibility = View.INVISIBLE
                    itemFeedProgressbar.visibility = View.VISIBLE
                    Glide.with(itemFeedImage.context)
                        .load(item.url)
                        .thumbnail(
                            Glide.with(itemFeedImage.context)
                                .load(item.thumbnail)
                                .listener(object : RequestListener<Drawable> {
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        itemFeedImage.visibility = View.INVISIBLE
                                        itemFeedProgressbar.visibility = View.VISIBLE
                                        Timber.tag("image ${item.url}").d("thumb fail")
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        dataSource: DataSource?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        Timber.tag("image ${item.url}").d("thumb success")
                                        itemFeedImage.visibility = View.VISIBLE
                                        itemFeedProgressbar.visibility = View.GONE
                                        return false
                                    }
                                })
                        )
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Timber.tag("image ${item.url}").d("fail")
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Timber.tag("image ${item.url}").d("succcess")
                                return false
                            }
                        })
                        .placeholder(R.drawable.hand)
                        .into(itemFeedImage)
                } else {
                    itemFeedImage.visibility = View.GONE
                    itemFeedProgressbar.visibility = View.GONE
                }
            }
        }

        private fun vote(vote: Boolean) {
            val item = feedItem
            if (item != null) {
                voteVM(item, if (item.vote != null) null else vote)
                setButtonColors()
                notifyChanged(layoutPosition)
            }
        }

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
    }
}
