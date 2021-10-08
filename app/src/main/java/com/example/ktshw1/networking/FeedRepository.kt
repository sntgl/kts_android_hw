package studio.kts.android.school.lection4.networking.data

import com.example.ktshw1.networking.ServerListingWrapper
import com.example.ktshw1.networking.ServerResponseWrapper
import com.example.ktshw1.networking.Subreddit

class FeedRepository {

    suspend fun getBestFeed(
        after: String = ""
    ): ServerListingWrapper<ServerResponseWrapper<Subreddit>> {
        return Networking.redditApi.loadBestAfter(after).data
    }

    private suspend fun getSubreddit(id: String): Subreddit? {
        val response = Networking.redditApi.loadSubreddit(id).data.children
        return if (response.isNotEmpty()) response[0].data else null
    }

    suspend fun vote(id: String, newVote: Boolean?): Subreddit? {
        val dir = when (newVote) {
            null -> 0
            true -> 1
            false -> -1
        }
        Networking.redditApi.vote(id, dir)
        return getSubreddit(id)
    }
}
