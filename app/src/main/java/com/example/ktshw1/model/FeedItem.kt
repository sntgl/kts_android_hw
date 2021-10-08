package com.example.ktshw1.model

import java.util.*

data class FeedItem(
    val uuid: UUID,
    val subredditImage: String,
    val subredditName: String,
    val userName: String,
    val datePublished: Int,
    var vote: Vote = Vote.NOT_VOTED,
    var voteCounter: Int?,
    val content: FeedContent,
    var title: String
)