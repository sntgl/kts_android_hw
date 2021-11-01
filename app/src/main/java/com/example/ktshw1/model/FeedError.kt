package com.example.ktshw1.model

import java.util.*

data class FeedError (
    val id: UUID = UUID.randomUUID()
) : FeedLastItem()