package com.example.ktshw1.model

import java.util.*

data class FeedLoading(
    val id: UUID = UUID.randomUUID(),
    var isError: Boolean = false
)