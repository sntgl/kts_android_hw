package com.example.ktshw1

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import timber.log.Timber

class AppViewModel(state: SavedStateHandle): ViewModel() {
//    private var onBoardingPassed: Boolean = false
    private val savedStateHandle = state
    fun isOnBoardingPassed(): Boolean{
        Timber.d("onb got ${ savedStateHandle.get(ON_BOARDING_KEY)?: false}")
        return savedStateHandle.get(ON_BOARDING_KEY)?: false
    }
    fun onBoardingPass() {
        Timber.d("onb passed!")
        savedStateHandle.set(ON_BOARDING_KEY, false)
    }

    companion object {
        private const val ON_BOARDING_KEY = "onBoardingPassed"
    }
}