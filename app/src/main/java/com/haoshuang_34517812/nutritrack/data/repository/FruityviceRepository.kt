package com.haoshuang_34517812.nutritrack.data.repository

import com.haoshuang_34517812.nutritrack.data.network.FruityviceApi
import com.haoshuang_34517812.nutritrack.data.network.fruityvice.FruitDto
import retrofit2.Response
import javax.inject.Inject

class FruityviceRepository @Inject constructor(
    private val apiService: FruityviceApi
) {
    suspend fun getFruit(name: String): Response<FruitDto> {
        return apiService.getFruitByName(name)
    }
}