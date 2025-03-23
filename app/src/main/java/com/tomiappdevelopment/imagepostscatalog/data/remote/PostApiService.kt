package com.tomiappdevelopment.imagepostscatalog.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface PostApiService {

    @GET("api/")
    suspend fun getPosts(
        @Query("key") apiKey: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 50,
        @Query("editors_choice") editorsChoice: Boolean = true,
        @Query("order") order: String = "popular"
    ): PostResponse
}