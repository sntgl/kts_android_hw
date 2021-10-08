package studio.kts.android.school.lection4.networking.data

import com.example.ktshw1.UserInfo
import okhttp3.Interceptor.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import timber.log.Timber

object Networking {

    private val okhttpClient = OkHttpClient.Builder()
//        .addNetworkInterceptor(
//            HttpLoggingInterceptor {
//                Timber.tag("Network").d(it)
//            }
//                .setLevel(HttpLoggingInterceptor.Level.BODY)
//        )
        .addInterceptor { chain: Chain ->
            val request = chain.request()
            val authenticatedRequest = request.newBuilder()
                .header("Authorization", "bearer ${UserInfo.authToken}").build()
            return@addInterceptor chain.proceed(authenticatedRequest) }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://oauth.reddit.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(okhttpClient)
        .build()

    val redditApi: RedditApi
        get() = retrofit.create()

}
