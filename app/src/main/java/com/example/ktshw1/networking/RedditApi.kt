package studio.kts.android.school.lection4.networking.data

import com.example.ktshw1.networking.ServerListingWrapper
import com.example.ktshw1.networking.ServerResponseWrapper
import com.example.ktshw1.model.Subreddit
import com.example.ktshw1.model.User
import retrofit2.Response
import retrofit2.http.*

interface RedditApi {
    @GET("best")
    suspend fun loadBestAfter(
        @Query("after") after: String
    ): Response<
            ServerResponseWrapper<
                ServerListingWrapper<
                        ServerResponseWrapper<
                                Subreddit>>>>

    @FormUrlEncoded
    @POST("api/vote")
    suspend fun vote(
        @Field("id") id: String,
        @Field("dir") dir: Int
    ): Response<Any>

    @GET("api/info")
    suspend fun loadSubreddit(
        @Query("id") id: String
    ):ServerResponseWrapper<
            ServerListingWrapper<
                    ServerResponseWrapper<
                            Subreddit>>>

    @FormUrlEncoded
    @POST("api/save")
    suspend fun save(
        @Field("id") id: String
    ): Response<Any>

    @FormUrlEncoded
    @POST("api/unsave")
    suspend fun unsave(
        @Field("id") id: String
    ): Response<Any>

    @GET("api/v1/me")
    suspend fun me(): User
}
