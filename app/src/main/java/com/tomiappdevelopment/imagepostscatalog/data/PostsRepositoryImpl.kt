package com.tomiappdevelopment.imagepostscatalog.data

import com.tomiappdevelopment.imagepostscatalog.BuildConfig
import com.tomiappdevelopment.imagepostscatalog.data.local.PostDao
import com.tomiappdevelopment.imagepostscatalog.data.maper.toDomain
import com.tomiappdevelopment.imagepostscatalog.data.maper.toEntity
import com.tomiappdevelopment.imagepostscatalog.data.remote.PostApiService
import com.tomiappdevelopment.imagepostscatalog.domain.PostRepository
import com.tomiappdevelopment.imagepostscatalog.domain.modules.Post
import com.tomiappdevelopment.imagepostscatalog.domain.util.DataError
import com.tomiappdevelopment.imagepostscatalog.domain.util.Error
import com.tomiappdevelopment.imagepostscatalog.domain.util.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class PostRepositoryImpl(private val postDao: PostDao,
                         private val postApiService: PostApiService
): PostRepository {

    override suspend fun fetchNewPage(page: Int): Result<Boolean, Error> {
        val existingPosts = postDao.getPostsByPage(10, (page - 1) * 10).firstOrNull()
        if (!existingPosts.isNullOrEmpty()) {
            return Result.Success(true)
        }

        return try {
            val response = postApiService.getPosts(apiKey = "13398314-67b0a9023aca061e2950dbb5a", page = page)
            val posts = response.hits.map { it.toDomain().toEntity() }
            postDao.upsertPosts(posts)
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(DataError.Network.UNKNOWN)
        }
    }

    override fun getPostsByPage(page: Int): Flow<List<Post>> {
        val limit = 10  // Limit per page
        val offset = (page - 1) * limit  // Calculate offset for pagination

        // Fetch posts from the local database (cache)
        return postDao.getPostsByPage(limit, offset).map { it.toDomain() }
    }

    override suspend fun upsertPosts(posts: List<Post>) {
        postDao.upsertPosts(posts.toEntity()) // Mapping domain to entity before upsert

    }

    override suspend fun deleteAllPosts() {
        // Delete all posts from the local database
        postDao.deleteAllPosts()
    }

    // Simulated function to get some dummy posts (fake API response)
    private fun fakePosts(page: Int): List<Post> {
        return List(10) { index ->
            Post(
                id = (page * 10 + index).toString(),
                comments = 555,
                likes = 5055,
                imageUrl = "https://pixabay.com/get/ga1fcbaef556c9d3f31e8ac41c4d6485a2f5fa82d16cb873f539320225bcdda1c2b16aebde37dc901fc166cbcc7a6539b513e1151f592975580b1640c54b915a0_640.jpg"
            )
        }
    }
}