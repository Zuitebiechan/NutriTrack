package com.haoshuang_34517812.nutritrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.haoshuang_34517812.nutritrack.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.haoshuang_34517812.nutritrack.NutriTrackApp
import com.haoshuang_34517812.nutritrack.data.repository.GenAIRepository
import com.haoshuang_34517812.nutritrack.data.repository.NutriCoachTipRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing GenAI interactions and storing nutrition tips.
 * Handles communication with Gemini API and local tip storage.
 */
class GenAIViewModel(
    private val repo: GenAIRepository = GenAIRepository(
        offlineModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.apiKeySafe
        )
    ),
    private val tipRepository: NutriCoachTipRepository = NutriTrackApp.nutriCoachTipRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ApiResult<String>>(ApiResult.Initial)
    val state: StateFlow<ApiResult<String>> = _state.asStateFlow()

    private val _ui = MutableStateFlow<GenAiUiState>(GenAiUiState.Idle)
    val ui: StateFlow<GenAiUiState> = _ui

    // Current prompt for database storage
    private var currentPrompt: String = ""

    /**
     * Gets all tips for a specific user
     * @param userId ID of the user
     * @return Flow of tips for the user
     */
    fun getTipsForUser(userId: String) = tipRepository.getTipsForUser(userId)

    /**
     * Generates insights based on population data
     * @param maleCount Number of male users
     * @param femaleCount Number of female users
     * @param maleAvg Average HEIFA score for male users
     * @param femaleAvg Average HEIFA score for female users
     */
    fun generateInsights(
        maleCount: Int,
        femaleCount: Int,
        maleAvg: Double,
        femaleAvg: Double
    ) {
        val total = maleCount + femaleCount
        if (total == 0) {
            _ui.value = GenAiUiState.Error("No population data.")
            return
        }

        val prompt = buildInsightPrompt(
            maleCount = maleCount,
            femaleCount = femaleCount,
            maleAvg = maleAvg,
            femaleAvg = femaleAvg,
            total = total
        )

        currentPrompt = prompt
        sendPrompt(prompt)
    }

    /**
     * Builds a prompt for population health insights
     */
    private fun buildInsightPrompt(
        maleCount: Int,
        femaleCount: Int,
        maleAvg: Double,
        femaleAvg: Double,
        total: Int
    ): String = buildString {
        appendLine("As a data analyst, please analyze the following nutritional health data for this population and identify 3 interesting data patterns or insights:")
        appendLine("- Male users: $maleCount people, average HEIFA score: $maleAvg/100")
        appendLine("- Female users: $femaleCount people, average HEIFA score: $femaleAvg/100")
        appendLine("- Total users: $total people")
        appendLine("The HEIFA score is a healthy eating index assessment, with a perfect score of 100.")
        appendLine("Please list 3 insights directly in this format:")
        appendLine("1. ...")
        appendLine("2. ...")
        appendLine("3. ...")
    }

    /**
     * Sends a custom prompt to the AI
     * @param prompt The prompt to send
     */
    fun sendCustomPrompt(prompt: String) {
        if (prompt.isBlank()) {
            _ui.value = GenAiUiState.Validation("Prompt cannot be empty")
            return
        }

        // Ensure prompt is about fruits and nutrition
        val enhancedPrompt = ensureNutritionContext(prompt)

        currentPrompt = enhancedPrompt
        sendPrompt(enhancedPrompt)
    }

    /**
     * Ensures the prompt is related to nutrition and health
     */
    private fun ensureNutritionContext(prompt: String): String {
        val nutritionKeywords = listOf("fruit", "nutrition", "diet", "health")
        val containsNutritionContext = nutritionKeywords.any {
            prompt.contains(it, ignoreCase = true)
        }

        return if (!containsNutritionContext) {
            "Generate a response about fruits and nutrition based on this prompt: $prompt"
        } else {
            prompt
        }
    }

    /**
     * Sends a prompt to the AI and updates state with the result
     * @param prompt The prompt to send
     */
    private fun sendPrompt(prompt: String) {
        viewModelScope.launch {
            _ui.value = GenAiUiState.Loading
            when (val r = repo.askGemini((prompt))) {
                is ApiResult.Success -> _ui.value = GenAiUiState.Content(r.data)
                is ApiResult.Error.Network -> _ui.value = GenAiUiState.Error("Network issue, try again.")
                is ApiResult.Error.Http -> _ui.value = GenAiUiState.Error("Server error: ${r.code}")
                is ApiResult.Error.Parsing -> _ui.value = GenAiUiState.Error(r.toString())
                else -> _ui.value = GenAiUiState.Error("Unknown error")
            }
        }
    }

    /**
     * Saves the current AI response as a tip in the database
     * @param userId ID of the user to associate with the tip
     */
    fun saveTipToDatabase(userId: String) {
        val currentResult = _state.value
        if (currentResult is ApiResult.Success) {
            val tipContent = currentResult.data

            viewModelScope.launch {
                try {
                    tipRepository.saveTip(
                        userId = userId,
                        content = tipContent,
                        prompt = currentPrompt
                    )
                } catch (ce: CancellationException) {
                    throw ce
                } catch (t: Throwable) {
                    // Silent error handling
                }
            }
        }
    }

    /**
     * Deletes a specific tip by its ID
     * @param tipId The ID of the tip to delete
     */
    fun deleteTip(tipId: Long) {
        viewModelScope.launch {
            try {
                tipRepository.deleteTipById(tipId)
            } catch (e: Exception) {
                // Silent error handling
            }
        }
    }

    /**
     * Resets the AI state
     */
    fun reset() {
        _state.value = ApiResult.Initial
        currentPrompt = ""
    }

    /**
     * Factory for creating GenAIViewModel instances
     */
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GenAIViewModel::class.java)) {
                return GenAIViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}