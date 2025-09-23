package com.haoshuang_34517812.nutritrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.haoshuang_34517812.nutritrack.NutriTrackApp
import com.haoshuang_34517812.nutritrack.data.repository.FruityviceRepository
import com.haoshuang_34517812.nutritrack.data.repository.PatientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.net.UnknownHostException

/**
 * ViewModel for the NutriCoach screen handling fruit API data and patient information
 */
class NutriCoachViewModel : ViewModel() {

    // Repository for fruit data API access
    private val repository: FruityviceRepository = FruityviceRepository()

    // Repository for patient data access
    private val patientRepo: PatientRepository = NutriTrackApp.patientRepository

    // UI state for fruit data
    private val _fruitUiState = MutableStateFlow<FruitUiState>(FruitUiState.Initial)
    val fruitUiState: StateFlow<FruitUiState> = _fruitUiState.asStateFlow()

    // Current fruit search query
    private val _fruitQuery = MutableStateFlow("")
    val fruitQuery: StateFlow<String> = _fruitQuery

    /**
     * Updates the current fruit search query
     * @param query The new search query
     */
    fun updateQuery(query: String) {
        _fruitQuery.value = query
    }

    /**
     * Searches for fruit information using the current query
     */
    fun searchFruit() {
        viewModelScope.launch {
            _fruitUiState.value = FruitUiState.Loading
            try {
                val resp = repository.getFruit(_fruitQuery.value.lowercase())

                if (resp.isSuccessful) {
                    val body = resp.body()
                    if (body != null) {
                        _fruitUiState.value = FruitUiState.Success(body)
                    } else {
                        _fruitUiState.value =
                            FruitUiState.Error("Empty body (HTTP ${resp.code()})")
                    }
                } else {
                    _fruitUiState.value =
                        FruitUiState.Error("HTTP ${resp.code()} - ${resp.message()}")
                }
            } catch (e: UnknownHostException) {
                _fruitUiState.value = FruitUiState.Error("DNS error – check network")
            } catch (e: Exception) {
                _fruitUiState.value = FruitUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    /**
     * Gets the fruit score for a specific user
     * @param userId ID of the user
     * @return Flow of the user's fruit score
     */
    fun getFruitScore(userId: String): Flow<Double> {
        return patientRepo.getPatientFlow(userId).map { it?.fruitScore ?: 0.0 }
    }

    /**
     * Gets the fruit variations score for a specific user
     * @param userId ID of the user
     * @return Flow of the user's fruit variations score
     */
    fun getFruitVariationsScore(userId: String): Flow<Double> {
        // 修正了方法名拼写和返回值（应该是fruitVariantionsScore而不是vegetablesVariantionsScore）
        return patientRepo.getPatientFlow(userId).map { it?.fruitVariantionsScore ?: 0.0 }
    }

    /**
     * Gets the fruit serve size for a specific user
     * @param userId ID of the user
     * @return Flow of the user's fruit serve size
     */
    fun getFruitServeSize(userId: String): Flow<Double> {
        return patientRepo.getPatientFlow(userId).map { it?.fruitServeSize ?: 0.0 }
    }

    /**
     * Checks if the user's fruit metrics are below optimal thresholds
     * @param userId ID of the user
     * @return Flow of boolean indicating if fruit metrics are below optimal (true if improvement needed)
     */
    fun checkIfFruitScoreNeedsImprovement(userId: String): Flow<Boolean> {
        // 使用combine操作符一次性获取所有需要的分数，避免多次数据库查询
        return combine(
            getFruitScore(userId),
            getFruitVariationsScore(userId),
            getFruitServeSize(userId)
        ) { fruitScore, fruitVariationsScore, fruitServeSize ->
            // 如果所有指标都低于阈值，则需要改进
            fruitScore < 5.0 && fruitVariationsScore < 2.5 && fruitServeSize < 1.0
        }
    }



    /**
     * Resets the fruit search state
     */
    fun resetState() {
        _fruitUiState.value = FruitUiState.Initial
    }

    /**
     * Factory for creating NutriCoachViewModel instances
     */
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NutriCoachViewModel::class.java)) {
                return NutriCoachViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}