package com.example.ktshw1.feed

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.datastore.dataStore
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.ktshw1.R
import com.example.ktshw1.databinding.ItemFeedMultiBinding
import com.example.ktshw1.model.Subreddit
import com.example.ktshw1.utils.ClickToFullTextSpan
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import timber.log.Timber
import java.lang.Math.round
import java.text.DateFormat
import java.util.*
import kotlin.math.round
import kotlin.math.roundToInt


class FeedItemDelegate(
    private val notifyChanged: (position: Int) -> Any,
    private val voteVM: (subreddit: Subreddit, newVote: Boolean?) -> Any,
    private val saveVM: (subreddit: Subreddit) -> Any,
    private val share: (url: String) -> Any,
) : AbsListItemAdapterDelegate<Any, Any, FeedItemDelegate.FeedItemDelegateVH>() {


    override fun isForViewType(item: Any, items: MutableList<Any>, position: Int): Boolean {
        return item is Subreddit
    }

    override fun onCreateViewHolder(parent: ViewGroup): FeedItemDelegateVH {
        val binding =
            ItemFeedMultiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedItemDelegateVH(binding, notifyChanged, voteVM, saveVM, share)
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
        private val saveVM: (subreddit: Subreddit) -> Any,
        private val share: (url: String) -> Any,
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
                    if (feedItem != null) {
                        val item: Subreddit = feedItem as Subreddit
                        loadImage(item) //TODO
                        itemFeedRetryPlaceholder.visibility = View.GONE
                    }
                }
                itemFeedRetryPlaceholder.setOnLongClickListener {
                    val url = feedItem?.url
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(binding.itemFeedImageContainer.context, i, null)
                    return@setOnLongClickListener false
                }
                itemFeedShareButton.setOnClickListener {
                    val url = feedItem?.permalink
                    if (url != null)
                        share(url)
                }


            }
        }

        fun bind(item: Subreddit) {
            //also: Coil, Picasso
            feedItem = item
            with(binding) {
                loadContent(binding, item)
                itemFeedCommentNumber.text = feedItem?.num_comments.toString()
                itemFeedSubredditName.text = item.subreddit_name
                itemFeedUserName.text = item.author
                itemFeedTitle.text = item.title
                itemFeedVoteCounter.text = item.score.toString()
            }
            setButtonColors()
            setVoteLoading()
            setStar()
            setTime()
        }

        private fun setTime() {
            val item = feedItem ?: return
            val context = binding.itemFeedDatePublished.context
            val created: Long = (item.created)
            val minutes = 60.0
            val hours = minutes * 60.0
            val days = hours * 24.0
            val delta = ((System.currentTimeMillis() / 1000) - created).toFloat()
            binding.itemFeedDatePublished.text = if (delta < hours) {
                (delta / minutes).roundToInt().toString() + context.getString(R.string.minutes_mini)
            } else if (delta < days) {
                (delta / hours).roundToInt().toString() + context.getString(R.string.hours_mini)
            } else {
                (delta / days).roundToInt().toString() + context.getString(R.string.days_mini)
            } + context.getString(R.string.space) + context.getString(R.string.ago)
        }

        private fun loadContent(binding: ItemFeedMultiBinding, item: Subreddit) {
            with(binding) {
                itemFeedUrl.visibility = View.GONE
                itemFeedImage.visibility = View.GONE
                itemFeedProgressbar.visibility = View.GONE
                itemFeedRetryPlaceholder.visibility = View.GONE

                when (item.content_type) {
                    Subreddit.Content.URL -> {
                        itemFeedUrl.text = item.url
                        itemFeedUrl.visibility = View.VISIBLE

                    }
                    Subreddit.Content.TEXT -> {
                        Timber.d("TEXT!!! ${item.text}")
                        if (!item.isTextPreviewed)
                            itemFeedUrl.text = item.text
                        else {
                            //TODO ссылки как в md надо превратить в спаны((
                            val span = ClickToFullTextSpan(itemFeedUrl, item.text)
                            val spanText =
                                itemFeedUrl.context.getString(R.string.get_more_text) + "(" + item.text.length.toString() + ")"
                            val spanClickable = (item.textPreview + spanText).toSpannable()
                            spanClickable
                                .setSpan(
                                    span,
                                    item.textPreview.length,
                                    item.textPreview.length + spanText.length,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            itemFeedUrl.setText(spanClickable, TextView.BufferType.SPANNABLE)
                        }
                        itemFeedUrl.visibility = View.VISIBLE
                    }
                    Subreddit.Content.IMAGE -> loadImage(item)
                    Subreddit.Content.NONE -> {
                    }
                }
            }
        }

        private fun loadImage(item: Subreddit) {
            with(binding) {
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
//                                    Timber.tag("image ${item.url}").d("thumb fail")
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
//                                    Timber.tag("image ${item.url}").d("thumb success")
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
//                            Timber.tag("image ${item.url}").d("fail")
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
//                            Timber.tag("image ${item.url}").d("succcess")
                            return false
                        }
                    })
                    .into(itemFeedImage)
            }
        }

        private fun vote(vote: Boolean) {
            val item = feedItem
            if (item != null) {
                setVoteLoading(true)
                voteVM(item, if (item.vote != null) null else vote)
//                notifyChanged(layoutPosition)
//                setButtonColors()
            }
        }

        private fun setVoteLoading(loading: Boolean = false) {
            binding.voteLoading.isVisible = loading
            if (loading)
                binding.itemFeedVoteCounter.visibility = View.INVISIBLE
            else
                binding.itemFeedVoteCounter.visibility = View.VISIBLE
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

        private fun setStar() {
            if (feedItem?.saved == true) {
                binding.itemFeedStar.setImageResource(R.drawable.ic_baseline_star_24)
            } else {
                binding.itemFeedStar.setImageResource(R.drawable.ic_baseline_star_border_24)
            }
            binding.itemFeedStar.isVisible = true
            binding.starLoading.isVisible = false
            binding.itemFeedStar.setOnClickListener {
                feedItem?.let { saveVM(it) }
                binding.itemFeedStar.visibility = View.INVISIBLE
                binding.starLoading.isVisible = true
            }
        }
    }
}
