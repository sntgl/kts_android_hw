package com.example.ktshw1.repository

import android.net.Uri
import com.example.ktshw1.UserInfo
import net.openid.appauth.*
import timber.log.Timber

class AuthRepository {

    fun getAuthRequest(): AuthorizationRequest {
        val redirectUri = Uri.parse(CALLBACK_URL)
        val serviceConfiguration = AuthorizationServiceConfiguration(
            Uri.parse(AUTH_URI),
            Uri.parse(TOKEN_URI)
        )


        return AuthorizationRequest.Builder(
            serviceConfiguration,
            CLIENT_ID,
            RESPONSE_TYPE,
            redirectUri
        )
            .setAdditionalParameters(mapOf(DURATION_KEY to DURATION))
            .setScope(SCOPE)
            .build()
    }

    fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
        onComplete: () -> Unit,
        onError: () -> Unit
    ) {
        authService.performTokenRequest(tokenRequest, getSecret()) { response, err ->

            when {
                response != null -> {
                    UserInfo.authToken = response.accessToken.orEmpty()
                    val accessToken = response.accessToken.orEmpty()
                    Timber.d("Token is $accessToken")
                    onComplete()
                }
                else -> onError()
            }
        }
    }

    private fun getSecret(): ClientAuthentication {
        return ClientSecretBasic(CLIENT_SECRET)
    }

    companion object {
        const val AUTH_URI = "https://ssl.reddit.com/api/v1/authorize.compact"
        const val TOKEN_URI = "https://ssl.reddit.com/api/v1/access_token"
        const val RESPONSE_TYPE = ResponseTypeValues.CODE
        const val SCOPE = "identity,read,vote,save"
        const val DURATION_KEY = "duration"
        const val DURATION = "permanent"

        const val CLIENT_ID = "5wbgdPE1hneZ5bnMO4C9Xw"
        const val CALLBACK_URL = "com.sntgl.breddit://auth"
        const val CLIENT_SECRET = ""
    }
}