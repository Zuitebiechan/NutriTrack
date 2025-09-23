package com.haoshuang_34517812.nutritrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.haoshuang_34517812.nutritrack.NutriTrackApp
import com.haoshuang_34517812.nutritrack.data.room.entity.PatientEntity
import kotlinx.coroutines.flow.Flow

/**
 * ViewModel for accessing patient data from the database
 */
class InsightsViewModel : ViewModel() {
    // Access the PatientRepository from application global state
    private val patientRepository = NutriTrackApp.patientRepository

    /**
     * Gets a reactive flow of patient data for a specific user ID
     * Using Flow instead of LiveData for reactive database monitoring
     *
     * @param userId The ID of the patient to retrieve
     * @return Flow of PatientEntity that updates when data changes
     */
    fun getPatient(userId: String): Flow<PatientEntity?> {
        return patientRepository.getPatientFlow(userId)
    }

    /**
     * Factory for creating InsightsViewModel instances
     */
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InsightsViewModel::class.java)) {
                return InsightsViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}