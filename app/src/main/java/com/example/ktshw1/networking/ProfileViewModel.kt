package com.example.ktshw1.networking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktshw1.model.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    private val jobGettingIdMutable = MutableStateFlow<Job?>(null)

    private val userMutableFlow = MutableStateFlow<User?>(null)
    val userFlow: StateFlow<User?>
        get() = userMutableFlow

    private val userErrorMutable = MutableStateFlow(false)
    val userError: StateFlow<Boolean>
        get() = userErrorMutable

    fun getId() {
        if (jobGettingIdMutable.value == null) {
            jobGettingIdMutable.value = viewModelScope.launch {
                runCatching {
                    repository.getId()
                }.onSuccess {
                    userMutableFlow.emit(it)
                    jobGettingIdMutable.value = null
                }.onFailure {
                    Timber.d("Error: ${it.localizedMessage}")
                    jobGettingIdMutable.value = null
                    userErrorMutable.emit(true)
                }
            }
        }
    }

    fun gotUserError() {
        userErrorMutable.value = false
    }
}