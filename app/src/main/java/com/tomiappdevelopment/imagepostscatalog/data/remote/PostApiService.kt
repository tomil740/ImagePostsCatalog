package com.tomiappdevelopment.imagepostscatalog.data.remote

import com.tomiappdevelopment.imagepostscatalog.domain.util.Result
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response


interface PostApiService {

    @GET("api/")
    suspend fun getPosts(
        @Query("key") apiKey: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 150,
        @Query("editors_choice") editorsChoice: Boolean? = null // Made nullable to avoid forcing it
    ): Response<PostResponse> // Use Response<PostResponse> for proper error handling
}