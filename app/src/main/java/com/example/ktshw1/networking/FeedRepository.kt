package com.example.ktshw1.networking

class FeedRepository {
    private fun unwrap(wrapped: ServerListingWrapper<ServerResponseWrapper<Subreddit>>): List<Subreddit> {
        val unwrappedList = mutableListOf<Subreddit>()
        wrapped.children.forEach { unwrappedList.add(it.data.setContentType()) }
        return unwrappedList
    }

    suspend fun getBestFeed(
        after: String
    ): Pair<List<Subreddit>?, String?> {
        val responseBody = Networking.redditApi.loadBestAfter(after).body()
        return if (responseBody != null)
            unwrap(responseBody.data) to responseBody.data.after
        else null to null
    }

    private suspend fun getSubreddit(id: String): Subreddit? {
        val body = Networking.redditApi.loadSubreddit(id)
        return if (body.data.children.isNotEmpty())
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
