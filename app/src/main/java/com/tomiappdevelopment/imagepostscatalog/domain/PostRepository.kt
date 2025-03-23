package com.tomiappdevelopment.imagepostscatalog.domain

import com.tomiappdevelopment.imagepostscatalog.domain.modules.Post
import com.tomiappdevelopment.imagepostscatalog.domain.util.Error
import com.tomiappdevelopment.imagepostscatalog.domain.util.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface PostRepository {

    suspend fun fetchNewPage(page: Int): Result<Boolean, Error>

    fun getPostsByPage(page: Int): Flow<List<Post>>

    suspend fun upsertPosts(posts: List<Post>)

    suspend fun deleteAllPosts()

    suspend fun fetchAndUpdatePosts(page: Int): Result<Boolean, Error>

}