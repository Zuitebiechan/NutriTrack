package com.haoshuang_34517812.nutritrack.data.network.fruityvice

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface FruityviceApi {
    @GET("api/fruit/{name}")
    suspend fun getFruitByName(@Path("name") name: String): Response<FruitDto>

    companion object {
        private const val BASE_URL = "https://www.fruityvice.com/"

        fun create(): FruityviceApi {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(FruityviceApi::class.java)
        }
    }
}