package com.example.ktshw1.utils

import com.example.ktshw1.db.SubredditT
import com.example.ktshw1.db.toSubreddit
import com.example.ktshw1.model.toSubredditT
import com.example.ktshw1.model.Subreddit



class SubredditParser {
    fun fromDataBase(l: List<*>): List<Subreddit> = MutableList(l.size) {
        (l[it] as SubredditT).toSubreddit()
    }.toList()

    fun toDataBase(l: List<*>): List<SubredditT> = MutableList(l.size) {
        (l[it] as Subreddit).toSubredditT()
    }.toList()
}