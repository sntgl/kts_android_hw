package com.example.ktshw1

import com.example.ktshw1.db.SubredditT
import com.example.ktshw1.db.toSubreddit
import com.example.ktshw1.networking.Subreddit
import com.example.ktshw1.networking.toSubredditT

class SubredditParser {
    fun fromDataBase(l: List<*>) =
        MutableList(l.size) {
            if (l[it] is SubredditT)
                (l[it] as SubredditT).toSubreddit()
        }.toList() as List<Subreddit>
    fun toDataBase(l: List<*>): List<SubredditT> = MutableList(l.size) {
            (l[it] as Subreddit).toSubredditT()
        }.toList()
}