package com.example.ktshw1.model

import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class User (
    val icon_img: String, // https://www.redditstatic.com/avatars/defaults/v2/avatar_default_7.png
    val name: String, //saniatagilov
    val total_karma: Int, //1
    val created_utc: Float,
    val coins: Int,
    val subreddit: SubredditOnlyUrl,
    @Transient
    var random_id: UUID = UUID.randomUUID()
)