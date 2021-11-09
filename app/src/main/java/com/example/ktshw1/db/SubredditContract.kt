package com.example.ktshw1.db


object SubredditContract {
    const val TABLE_NAME = "feedItems"

    object Columns {
        const val ID = "id"
        const val AUTHOR = "author"
        const val CREATED = "created"
        const val SCORE = "score"
        const val SUBREDDIT_NAME = "subreddit_name"
        const val TITLE = "title"
        const val URL = "url"
        const val VOTE = "vote"
        const val THUMBNAIL = "thumbnail"
        const val NUM_COMMENTS = "num_comments"
        const val PERMALINK = "permalink"
        const val TEXT = "text"
        const val SAVED = "saved"
    }
}
