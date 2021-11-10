package com.example.ktshw1.networking

import Networking.okhttpAuthClient
import android.util.Base64
import com.example.ktshw1.BuildConfig
import com.example.ktshw1.datastore.DatastoreViewModel
import com.example.ktshw1.model.UserInfo
import com.example.ktshw1.repository.AuthRepository
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import org.json.JSONTokener
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class RefreshingInterceptor : Interceptor, KoinComponent {

    private val vm: DatastoreViewModel by inject()


    override fun intercept(chain: Interceptor.Chain): Response {
        // TODO если еще пол жизни впереди запустить в корутине, если сдох, то ждать
        // в данный момент обновляется раз в минуту для тестирования
        var tokenTimeLeft = 60000
        if (BuildConfig.DEBUG)
            tokenTimeLeft *= 59
        if ((UserInfo.expires ?: 0) - System.currentTimeMillis() < tokenTimeLeft) {
            val basicAuth = "Basic " + Base64.encodeToString(
                "${AuthRepository.CLIENT_ID}:".toByteArray(),
                Base64.NO_WRAP
            )
            val body = FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", UserInfo.refreshToken ?: "")
                .build()
            val request = Request
                .Builder()
                .url("https://ssl.reddit.com/api/v1/access_token")
                .header("Authorization", basicAuth)
                .method("POST", body)
                .build()
            val call = okhttpAuthClient.newCall(request).execute()
            val json = call.body?.string()
//            Timber.d("Refreshing called! Status code = ${call.code}, body =\n${json}")

            val jsonObject = JSONTokener(json).nextValue() as JSONObject

            vm.onReceivedApiKey(
                jsonObject.getString("access_token"),
                jsonObject.getLong("expires_in") * 1000 + System.currentTimeMillis(),
                jsonObject.getString("refresh_token")
            )
        }
        return chain.proceed(chain.request())
    }
}