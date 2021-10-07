package com.example.ktshw1.networking

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ServerResponseWrapper<T>(
    val data: T,
    val kind: String //Listing, t1-t6
)