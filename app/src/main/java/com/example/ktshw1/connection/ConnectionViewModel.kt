package com.example.ktshw1.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import timber.log.Timber

class ConnectionViewModel(
    private val repository: ConnectionRepositoryInterface
) : ViewModel() {

    private val connectionFlowMutable = MutableStateFlow(false)
    val connectionFlow: StateFlow<Boolean>
        get() = connectionFlowMutable

    private val internalConnectionFlow = flow {
            while (true) {
                emit(repository.checkConnection())
                delay(DELAY)
            }
    }.onStart {
        Timber.d("Connection flow started, delay = $DELAY")
    }.distinctUntilChanged()

    init {
        viewModelScope.launch {
            internalConnectionFlow.flowOn(Dispatchers.IO).collect {
                connectionFlowMutable.emit(it)
            }
        }
    }

    companion object {
        const val DELAY: Long = 1000
    }
}