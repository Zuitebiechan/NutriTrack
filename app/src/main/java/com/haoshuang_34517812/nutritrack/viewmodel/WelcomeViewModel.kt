package com.haoshuang_34517812.nutritrack.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * ViewModel for the Welcome screen with optimized LiveData for navigation events
 */
class WelcomeViewModel : ViewModel() {
    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin

    fun onLoginClicked() {
        _navigateToLogin.value = true
    }

    fun onNavigationComplete() {
        _navigateToLogin.value = false
    }

    /**
     * Factory for creating WelcomeViewModel instances
     */
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WelcomeViewModel::class.java)) {
                return WelcomeViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

