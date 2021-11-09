package com.example.ktshw1.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ktshw1.model.Subreddit
import com.example.ktshw1.model.prepare

@Entity(
    tableName = SubredditContract.TABLE_NAME,
)
data class SubredditT(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = SubredditContract.Columns.ID)
    val id: String,
    @ColumnInfo(name = SubredditContract.Columns.AUTHOR)
    val author: String, //bagged___milk
    @ColumnInfo(name = SubredditContract.Columns.CREATED)
    val created: Long, //1633625553.0
    @ColumnInfo(name = SubredditContract.Columns.SCORE)
    val score: Int, //1632
    @ColumnInfo(name = SubredditContract.Columns.SUBREDDIT_NAME)
    val subreddit_name: String, //r/Jokes
    @ColumnInfo(name = SubredditContract.Columns.TITLE)
    val title: String, //My favorite joke I\u2019ve ever read on Reddit, one of the first I\u2019ve ever read here too: Everyone Knows Dave
    @ColumnInfo(name = SubredditContract.Columns.URL)
    val url: String, //may be image, may be
    @ColumnInfo(name = SubredditContract.Columns.VOTE)
    val vote: Boolean?, //
    @ColumnInfo(name = SubredditContract.Columns.THUMBNAIL)
    val thumbnail: String, //
    @ColumnInfo(name = SubredditContract.Columns.NUM_COMMENTS)
    val num_comments: Int, //
    @ColumnInfo(name = SubredditContract.Columns.PERMALINK)
    val permalink: String, //
    @ColumnInfo(name = SubredditContract.Columns.TEXT)
    val text: String, //
    @ColumnInfo(name = SubredditContract.Columns.SAVED)
    val saved: Boolean
)

fun SubredditT.toSubreddit() = Subreddit(
        id = id,
        author = author,
        created = created,
        score = score,
        subreddit_name = subreddit_name,
        title = title,
        url = url,
        vote = vote,
        thumbnail = thumbnail,
        num_comments = num_comments,
        permalink = permalink,
        text = text,
        saved = saved
    ).prepare()
