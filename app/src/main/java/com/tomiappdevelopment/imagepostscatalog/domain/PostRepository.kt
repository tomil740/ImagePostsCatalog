package com.tomiappdevelopment.imagepostscatalog.domain

import com.tomiappdevelopment.imagepostscatalog.domain.modules.Post
import com.tomiappdevelopment.imagepostscatalog.domain.util.Error
import com.tomiappdevelopment.imagepostscatalog.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface PostRepository {

    suspend fun fetchNewPage(): Result<Boolean, Error>

    fun getPostsFlow(): Flow<List<Post>>

    suspend fun upsertPosts(posts: List<Post>)

    suspend fun deleteAllPosts()

    suspend fun fetchAndUpdatePosts(page: Int): Result<Boolean, Error>

}