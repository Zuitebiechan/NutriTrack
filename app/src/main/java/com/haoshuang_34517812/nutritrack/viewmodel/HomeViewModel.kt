package com.haoshuang_34517812.nutritrack.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haoshuang_34517812.nutritrack.data.repository.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import javax.inject.Inject

/**
 * ViewModel for the Home screen displaying user profile and scores
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PatientRepository
) : ViewModel() {

    // User ID - Using LiveData for simple state
    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> = _userId

    // Username - Using LiveData for UI state
    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    // Total score - Using LiveData
    private val _totalScore = MutableLiveData(0)
    val totalScore: LiveData<Int> = _totalScore

    /**
     * Loads patient data from repository based on user ID
     * @param userId ID of the patient to load
     */
    fun loadPatientData(userId: String) {
        _userId.value = userId
        viewModelScope.launch {
            val patient = repository.getPatientById(userId)
            _username.value = patient?.name ?: userId
            _totalScore.value = (patient?.heifaTotalScore ?: 0.0).roundToInt()
        }
    }
}