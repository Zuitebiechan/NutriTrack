package com.haoshuang_34517812.nutritrack.data.repository

import com.haoshuang_34517812.nutritrack.data.network.FruityviceApi
import com.haoshuang_34517812.nutritrack.data.network.NetworkModule
import com.haoshuang_34517812.nutritrack.data.network.fruityvice.FruitDto
import retrofit2.Response


class FruityviceRepository() {
    private val apiService: FruityviceApi = NetworkModule.fruityviceService

    suspend fun getFruit(name: String): Response<FruitDto> {
        return apiService.getFruitByName(name)
    }
}