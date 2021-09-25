package com.example.ktshw1.model

import com.example.ktshw1.model.Vote

data class FeedText (
    val subredditImage: String,
    val subredditName: String,
    val userName: String,
    val datePublished: Int,
    val title: String,
    val vote: Vote = Vote.NOT_VOTED,
    val voteCounter: Int?,
)