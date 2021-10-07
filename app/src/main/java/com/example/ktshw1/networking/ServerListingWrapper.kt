package com.example.ktshw1.networking

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ServerListingWrapper<T>(
    val after: String?,
    val before: String?,
    val children: List<T>
)