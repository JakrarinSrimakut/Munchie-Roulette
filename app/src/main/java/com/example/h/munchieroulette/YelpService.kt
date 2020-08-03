package com.example.h.munchieroulette

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header
import retrofit2.http.Query
import java.math.BigDecimal

interface YelpService {
    @GET("businesses/search")
    fun searchRestaurants(
        @Header("Authorization") authHeader: String,
        @Query("term") searchTerm: String,
        @Query("latitude") latitude: Float,
        @Query("longitude") longitude: Float,
        @Query("radius") radius: Int,
        @Query("limit") limit: Int
    ): Call<YelpSearchResult>//return YelpSearchResult object
}