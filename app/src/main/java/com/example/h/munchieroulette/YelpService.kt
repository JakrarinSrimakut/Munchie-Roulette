package com.example.h.munchieroulette

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header
import retrofit2.http.Query

interface YelpService {
    @GET("businesses/search")
    fun searchRestaurants(
        @Header("Authorization") authHeader: String,
        @Query("term") searchTerm: String,
        @Query("location") location: String): Call<YelpSearchResult> //return YelpSearchResult object
}