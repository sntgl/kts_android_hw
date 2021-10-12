package com.example.ktshw1.networking

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Subreddit(
    val author: String, //bagged___milk
    val created: Long, //1633625553.0
    @Json(name = "name")
    val id: String, //t3_q34cz4
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
    @Transient
    var content_type: Content = Content.NONE
) {
    enum class Content {
        NONE, URL, IMAGE
    }
}
