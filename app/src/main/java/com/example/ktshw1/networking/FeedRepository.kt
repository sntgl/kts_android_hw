package studio.kts.android.school.lection4.networking.data

import com.example.ktshw1.networking.Subreddit

class FeedRepository {

    suspend fun getBestFeed(): List<Subreddit> {
        val response = Networking.redditApi.loadBestStart().data.children
        val unwrappedList = mutableListOf<Subreddit>()
        response.forEach { unwrappedList.add(it.data) }
        return unwrappedList
    }

    suspend fun getNextBestFeed(after: String): List<Subreddit> {
        val response = Networking.redditApi.loadBestAfter(after).data.children
        val unwrappedList = mutableListOf<Subreddit>()
        response.forEach { unwrappedList.add(it.data) }
        return unwrappedList
    }
}