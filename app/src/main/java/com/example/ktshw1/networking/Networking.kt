import android.util.Base64
import android.util.Base64.encodeToString
import com.example.ktshw1.UserInfo
import com.example.ktshw1.auth.AuthViewModel
import com.example.ktshw1.networking.RefreshingInterceptor
import com.example.ktshw1.repository.AuthRepository
import com.example.ktshw1.repository.AuthRepository.Companion.CLIENT_ID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import net.openid.appauth.AuthorizationService
import okhttp3.*
import okhttp3.Interceptor.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import studio.kts.android.school.lection4.networking.data.RedditApi

object Networking {

    val okhttpAuthClient = OkHttpClient.Builder()
        .build()

    private val interceptor = RefreshingInterceptor()

    private val okhttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .addInterceptor { chain: Chain ->
            val request = chain.request()
            val authenticatedRequest = request.newBuilder()
                .header("Authorization", "bearer ${UserInfo.authToken}").build()
            return@addInterceptor chain.proceed(authenticatedRequest) }
        .retryOnConnectionFailure(true)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://oauth.reddit.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(okhttpClient)
        .build()

    val redditApi: RedditApi
        get() = retrofit.create()

}
