package com.haoshuang_34517812.nutritrack.data.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.haoshuang_34517812.nutritrack.data.network.fruityvice.FruitDto
import com.haoshuang_34517812.nutritrack.data.network.genai.GeminiRequest
import com.haoshuang_34517812.nutritrack.data.network.genai.GeminiResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GenAIApiService {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun generateContent(
        @Body request: GeminiRequest,
        @Query("key") apiKey: String
    ): Response<GeminiResponse>
}

interface FruityviceApi {
    @GET("api/fruit/{name}")
    suspend fun getFruitByName(@Path("name") name: String): Response<FruitDto>
}

object NetworkModule {
    private const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/"
    private const val FRUITYVICE_BASE_URL = "https://www.fruityvice.com/"

    private val gson: Gson = GsonBuilder().serializeNulls().create()

    // 1. 创建唯一、共享的 OkHttpClient
    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // 2. 创建 Gemini 的 Retrofit 实例
    private val geminiRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(GEMINI_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // 3. 创建 Fruityvice 的 Retrofit 实例
    private val fruityviceRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(FRUITYVICE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // 4. 对外提供两个 API Service
    val genAiService: GenAIApiService by lazy {
        geminiRetrofit.create(GenAIApiService::class.java)
    }

    val fruityviceService: FruityviceApi by lazy {
        fruityviceRetrofit.create(FruityviceApi::class.java)
    }
}