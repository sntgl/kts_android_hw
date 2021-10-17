package studio.kts.android.school.lection4.networking.data

import com.example.ktshw1.networking.ServerListingWrapper
import com.example.ktshw1.networking.ServerResponseWrapper
import com.example.ktshw1.networking.Subreddit
import com.example.ktshw1.networking.setContentType

class FeedRepository {

    private fun unwrap(wrapped: ServerListingWrapper<ServerResponseWrapper<Subreddit>>): List<Subreddit> {
        val unwrappedList = mutableListOf<Subreddit>()
        wrapped.children.forEach { unwrappedList.add(it.data.setContentType()) }
        return unwrappedList
    }

    suspend fun getBestFeed(
        after: String = ""
    ): Pair<List<Subreddit>?, String?> {
        val responseBody = Networking.redditApi.loadBestAfter(after).body()
        return if (responseBody != null)
            unwrap(responseBody.data) to responseBody.data.after
        else null to null
    }

    private suspend fun getSubreddit(id: String): Subreddit? {
        val response = Networking.redditApi.loadSubreddit(id)
        val body = response.body()
        return if (response.isSuccessful && body != null && body.data.children.isNotEmpty())
            body.data.children[0].data.setContentType() else null
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
