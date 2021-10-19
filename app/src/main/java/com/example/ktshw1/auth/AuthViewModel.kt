package com.example.ktshw1.auth

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ktshw1.R
import com.example.ktshw1.repository.AuthRepository
import com.example.ktshw1.utils.SingleLiveEvent
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import timber.log.Timber

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val authService : AuthorizationService
) : ViewModel() {

    private val openAuthPageLiveEvent = SingleLiveEvent<Intent>()
    private val toastLiveEvent = SingleLiveEvent<Int>()
    private val loadingMutableLiveData = MutableLiveData(false)
    private val authSuccessLiveEvent = SingleLiveEvent<Unit>()

    val openAuthPageLiveData: LiveData<Intent>
        get() = openAuthPageLiveEvent

    val loadingLiveData: LiveData<Boolean>
        get() = loadingMutableLiveData

    val toastLiveData: LiveData<Int>
        get() = toastLiveEvent

    val authSuccessLiveData: LiveData<Unit>
        get() = authSuccessLiveEvent

    fun onAuthCodeFailed(exception: AuthorizationException) {
        //TODO поч-то не ретраит иногда
        Timber.d("Auth fail:\n${exception.error}\n${exception.errorDescription}")
        toastLiveEvent.postValue(R.string.auth_canceled)
    }

    fun onAuthCodeReceived(tokenRequest: TokenRequest) {
        authRepository.performTokenRequest(
            authService = authService,
            tokenRequest = tokenRequest,
            onComplete = {
                loadingMutableLiveData.postValue(false)
                authSuccessLiveEvent.postValue(Unit)
            },
            onError = {
                loadingMutableLiveData.postValue(false)
                toastLiveEvent.postValue(R.string.auth_canceled)
            }
        )
    }

    fun openLoginPage() {
        loadingMutableLiveData.postValue(true)
        val openAuthPageIntent = authService.getAuthorizationRequestIntent(
            authRepository.getAuthRequest()
        )
        openAuthPageLiveEvent.postValue(openAuthPageIntent)
    }

    override fun onCleared() {
        super.onCleared()
        authService.dispose()
    }
}