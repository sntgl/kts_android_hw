package com.example.ktshw1.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubredditOnlyUrl(
    val url: String
)