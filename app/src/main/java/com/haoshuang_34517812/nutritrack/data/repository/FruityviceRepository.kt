package com.haoshuang_34517812.nutritrack.data.repository

import com.haoshuang_34517812.nutritrack.data.network.fruityvice.FruitDto
import com.haoshuang_34517812.nutritrack.data.network.fruityvice.FruityviceApi
import retrofit2.Response


class FruityviceRepository() {
    private val apiService = FruityviceApi.create()

    suspend fun getFruit(name: String): Response<FruitDto> {
        return apiService.getFruitByName(name)
    }
}