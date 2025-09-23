package com.haoshuang_34517812.nutritrack.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.haoshuang_34517812.nutritrack.BuildConfig
import com.haoshuang_34517812.nutritrack.data.network.genai.Content
import com.haoshuang_34517812.nutritrack.data.network.genai.GeminiRequest
import com.haoshuang_34517812.nutritrack.data.network.genai.GenAIApiService
import com.haoshuang_34517812.nutritrack.data.network.genai.NetworkModule
import com.haoshuang_34517812.nutritrack.data.network.genai.Part
import com.haoshuang_34517812.nutritrack.viewmodel.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException


class GenAIRepository(
    private val apiService: GenAIApiService = NetworkModule.genAiService,
    private val apiKey: String = BuildConfig.apiKeySafe,
    private val offlineModel: GenerativeModel? = null
) {

    suspend fun askGemini(prompt: String): ApiResult<String> = withContext(Dispatchers.IO) {
        try {
            // 1. offline model
            offlineModel?.let { model ->
                val rsp = model.generateContent(
                    content { text(prompt) }
                )
                return@withContext ApiResult.Success(rsp.text ?: "")
            }

            // 2. online model
            val request = GeminiRequest(
                contents = listOf(Content(parts = listOf(Part(prompt))))
            )
            val resp = apiService.generateContent(request, apiKey)

            if (!resp.isSuccessful) {
                return@withContext ApiResult.Error(
                    msg = resp.errorBody()?.string() ?: resp.message(),
                    code = resp.code()
                )
            }

            val text = resp.body()
                ?.candidates?.firstOrNull()
                ?.content?.parts?.firstOrNull()?.text
                ?: return@withContext ApiResult.Error("Empty response")

            ApiResult.Success(text)

        } catch (e: IOException) {
            ApiResult.Error("Network error: ${e.localizedMessage}")
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Unknown error")
        }
    }
}