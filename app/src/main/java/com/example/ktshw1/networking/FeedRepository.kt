package studio.kts.android.school.lection4.networking.data

import com.example.ktshw1.networking.ServerListingWrapper
import com.example.ktshw1.networking.ServerResponseWrapper
import com.example.ktshw1.networking.Subreddit
import retrofit2.Response

class FeedRepository {

    suspend fun getBestFeed(
        after: String = ""
    ): ServerListingWrapper<ServerResponseWrapper<Subreddit>>? {
        val response = Networking.redditApi.loadBestAfter(after)
        val responseBody = response.body()
        return if (response.isSuccessful && responseBody != null){
            responseBody.data
        } else {
            null
        }
    }

    private suspend fun getSubreddit(id: String): Subreddit? {
        val response = Networking.redditApi.loadSubreddit(id)
        val body = response.body()
        return if (response.isSuccessful && body != null && body.data.children.isNotEmpty())
            setContentType(body.data.children[0].data) else null
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

    companion object {
        fun setContentType(s: Subreddit): Subreddit {
            val item: Subreddit = s
            if (item.url.endsWith(".jpg", true) ||
                item.url.endsWith(".jpeg", true) ||
                item.url.endsWith(".png", true) ||
                item.url.contains("imgur.com", true)
            )
                item.content_type = Subreddit.Content.IMAGE
            else if (!(item.url.contains("/comments/") &&
                        item.url.contains("reddit.com")) &&
                        item.url != "self")
                item.content_type = Subreddit.Content.URL
            else item.content_type = Subreddit.Content.NONE
            return item
        }
    }
}
