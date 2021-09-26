package com.example.ktshw1

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class AppViewModel(private val state: SavedStateHandle) : ViewModel() {
    //дефолт true для того, чтобы при первом запуске не было ошибок
    private var loginEmailValidMutable = MutableLiveData(false)
    val loginEmailValid: LiveData<Boolean>
        get() = loginEmailValidMutable

    private var loginPassValidMutable = MutableLiveData(false)
    val loginPassValid: LiveData<Boolean>
        get() = loginPassValidMutable

    private var loginValidMutable = MutableLiveData(false)
    val loginState: LiveData<Boolean>
        get() = loginValidMutable

    init {
        loginPassValid.observeForever{ passValid ->
            if (passValid && loginEmailValid.value == true)
                loginValidMutable.value = true
        }
        loginEmailValid.observeForever{ emailValid ->
            if (emailValid && loginPassValid.value == true)
                loginValidMutable.value = true
        }
    }

    fun onEditPass(newPass: String?) {
        loginPassValidMutable.value = newPass?.length ?: 0 >= PASSWORD_MIN_LENGTH
    }

    fun onEditEmail(newEmail: String?) {
        loginEmailValidMutable.value = Patterns.EMAIL_ADDRESS.matcher(newEmail ?: "").matches()
    }

    companion object {
        private const val PASSWORD_MIN_LENGTH: Int = 8
    }

}