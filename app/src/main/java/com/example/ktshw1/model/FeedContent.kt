package com.example.ktshw1.model

sealed class FeedContent {
    data class ImageType(
        val image: String
    ) : FeedContent()

    object OnlyTitleType : FeedContent()
}
