package com.example.ktshw1.networking

import com.example.ktshw1.db.SubredditT
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class Subreddit(
    val author: String, //bagged___milk
    val created: Long, //1633625553.0
    @Json(name = "name")
    var id: String, //t3_q34cz4
    val score: Int, //1632
    @Json(name = "subreddit_name_prefixed")
    val subreddit_name: String, //r/Jokes
    val title: String, //My favorite joke I\u2019ve ever read on Reddit, one of the first I\u2019ve ever read here too: Everyone Knows Dave
    val url: String, //may be image, may be
    @Json(name = "likes")
    val vote: Boolean?, //
    val thumbnail: String, //
    val num_comments: Int, //
    val permalink: String,
    @Json(name = "selftext")
    val text: String,
    @Transient
    var content_type: Content = Content.NONE,
    @Transient
    var random_id: UUID = UUID.randomUUID()
) {
    enum class Content {
        NONE, URL, IMAGE, TEXT
    }
}

fun Subreddit.setContentType(): Subreddit {
    val item: Subreddit = this
    if (item.url.endsWith(".jpg", true) ||
        item.url.endsWith(".jpeg", true) ||
        item.url.endsWith(".png", true) ||
        item.url.contains("imgur.com", true)
    )
        item.content_type = Subreddit.Content.IMAGE
    else if (!(item.url.contains("/comments/") &&
                item.url.contains("reddit.com")) &&
                item.url != "self")
        item.content_type = Subreddit.Content.URL
    else if (item.text != "")
        Subreddit.Content.TEXT
    else item.content_type = Subreddit.Content.NONE
    return item
}

fun Subreddit.toSubredditT() = SubredditT(
        id = id,
        author = author,
        created = created,
        score = score,
        subreddit_name = subreddit_name,
        title = title,
        url = url,
        vote = vote,
        thumbnail = thumbnail,
        num_comments = num_comments,
        permalink = permalink,
        text = text
    )
