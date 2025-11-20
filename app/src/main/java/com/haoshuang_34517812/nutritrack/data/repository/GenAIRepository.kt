package com.haoshuang_34517812.nutritrack.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.haoshuang_34517812.nutritrack.BuildConfig
import com.haoshuang_34517812.nutritrack.data.network.genai.Content
import com.haoshuang_34517812.nutritrack.data.network.genai.GeminiRequest
import com.haoshuang_34517812.nutritrack.data.network.GenAIApiService
import com.haoshuang_34517812.nutritrack.data.network.NetworkModule
import com.haoshuang_34517812.nutritrack.data.network.genai.Part
import com.haoshuang_34517812.nutritrack.viewmodel.ApiResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import javax.inject.Inject


class GenAIRepository @Inject constructor(
    private val apiService: GenAIApiService
) {
    private val apiKey: String = BuildConfig.apiKeySafe
    private val offlineModel: GenerativeModel? = null

    suspend fun askGemini(prompt: String): ApiResult<String> = withContext(Dispatchers.IO) {
        try {
            // 1. offline model
            offlineModel?.let { model ->
                val rsp = model.generateContent(content { text(prompt) })
                val text = rsp.text ?: return@withContext ApiResult.Error.Parsing(IllegalStateException("empty offline text"))

                return@withContext ApiResult.Success(text)
            }

            // 2. online model
            val request = GeminiRequest(
                contents = listOf(Content(parts = listOf(Part(prompt))))
            )
            val resp = apiService.generateContent(request, apiKey)

            if (!resp.isSuccessful) {
                val body = resp.errorBody()?.string()
                return@withContext ApiResult.Error.Http(code = resp.code(), message = resp.message(), body = body)

            }

            val text = resp.body()
                ?.candidates?.firstOrNull()
                ?.content?.parts?.firstOrNull()?.text
                ?: return@withContext ApiResult.Error.Parsing(IllegalStateException("empty candidates/parts/text"))


            ApiResult.Success(text)

        } catch (ce: CancellationException) {
            throw ce
        } catch (io: IOException) {
            ApiResult.Error.Network(io)
        } catch (t: Throwable) {
            ApiResult.Error.Unknown(t)
        }
    }
}