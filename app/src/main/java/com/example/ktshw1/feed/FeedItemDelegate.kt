package com.example.ktshw1.feed

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.core.content.ContextCompat.startActivity

import android.content.Intent
import android.net.Uri


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
                itemFeedRetryPlaceholder.setOnClickListener {
                    loadImage(binding, feedItem!!) //TODO
                    itemFeedRetryPlaceholder.visibility = View.GONE
                }
                itemFeedRetryPlaceholder.setOnLongClickListener {
                    val url = feedItem?.url
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(binding.itemFeedImageContainer.context, i, null)
                    return@setOnLongClickListener false
                }

            }
        }

        fun bind(item: Subreddit) {
            //Glide, Coil, Picasso
            feedItem = item
            with(binding) {
                loadContent(binding, item)
                itemFeedCommentNumber.text = feedItem?.num_comments.toString()
                itemFeedSubredditName.text = item.subreddit_name
                itemFeedUserName.text = item.author
                itemFeedDatePublished.text = item.created.toString()
                itemFeedTitle.text = item.title
                itemFeedVoteCounter.text = item.score.toString()
            }
            setButtonColors()
        }

        private fun loadContent(binding: ItemFeedMultiBinding, item: Subreddit) {
            with (binding) {
                itemFeedUrl.visibility = View.GONE
                itemFeedImage.visibility = View.GONE
                itemFeedProgressbar.visibility = View.GONE
                itemFeedRetryPlaceholder.visibility = View.GONE
                when (item.content_type) {
                    Subreddit.Content.URL -> {
                        itemFeedUrl.text = item.url
                        itemFeedUrl.visibility = View.VISIBLE
                    }
                    Subreddit.Content.IMAGE -> loadImage(binding, item)
                    Subreddit.Content.NONE -> {}
                }
            }
        }

        private fun loadImage(binding: ItemFeedMultiBinding, item: Subreddit) {
            with (binding) {
                itemFeedProgressbar.visibility = View.VISIBLE
                itemFeedImage.visibility = View.VISIBLE
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
                                    Timber.tag("image ${item.url}").d("thumb fail")
                                    itemFeedRetryPlaceholder.visibility = View.VISIBLE
                                    itemFeedProgressbar.visibility = View.GONE
                                    itemFeedImage.visibility = View.GONE
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    itemFeedImage.visibility = View.VISIBLE
                                    Timber.tag("image ${item.url}").d("thumb success")
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
                            itemFeedRetryPlaceholder.visibility = View.VISIBLE
                            itemFeedProgressbar.visibility = View.INVISIBLE
                            itemFeedImage.visibility = View.GONE
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
                            itemFeedProgressbar.visibility = View.GONE
                            Timber.tag("image ${item.url}").d("succcess")
                            return false
                        }
                    })
                    .into(itemFeedImage)
            }
        }

        private fun loadImageOld(binding: ItemFeedMultiBinding, item: Subreddit) {
            with(binding) {
                if (item.url.endsWith(".jpg", true) ||
                    item.url.endsWith(".jpeg", true) ||
                    item.url.endsWith(".png", true) ||
                    item.url.contains("imgur.com", true)
                ) {
                    itemFeedUrl.visibility = View.INVISIBLE
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
                } else if (!item.url.contains("/comments/")) {
                    itemFeedImage.visibility = View.GONE
                    itemFeedProgressbar.visibility = View.GONE
                    itemFeedUrl.text = item.url
                    itemFeedUrl.visibility = View.VISIBLE
                } else {
                    itemFeedImage.visibility = View.GONE
                    itemFeedProgressbar.visibility = View.GONE
                    itemFeedUrl.visibility = View.GONE
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
