package com.groze.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApiService {
    @GET("v6/{api_key}/latest/USD")
    suspend fun getExchangeRates(
        @Path("api_key") apiKey: String
    ): ExchangeRateResponse
}