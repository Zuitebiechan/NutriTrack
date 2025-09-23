package com.haoshuang_34517812.nutritrack.data.network.genai

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GenAIApiService {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun generateContent(
        @Body request: GeminiRequest,
        @Query("key") apiKey: String
    ): Response<GeminiResponse>
}