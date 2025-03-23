package com.tomiappdevelopment.imagepostscatalog.data

import android.util.Log
import com.tomiappdevelopment.imagepostscatalog.data.local.PostDao
import com.tomiappdevelopment.imagepostscatalog.data.maper.toDomain
import com.tomiappdevelopment.imagepostscatalog.data.maper.toEntity
import com.tomiappdevelopment.imagepostscatalog.data.remote.PostApiService
import com.tomiappdevelopment.imagepostscatalog.domain.PostRepository
import com.tomiappdevelopment.imagepostscatalog.domain.modules.Post
import com.tomiappdevelopment.imagepostscatalog.domain.util.DataError
import com.tomiappdevelopment.imagepostscatalog.domain.util.Error
import com.tomiappdevelopment.imagepostscatalog.domain.util.Result
import com.tomiappdevelopment.imagepostscatalog.domain.util.retry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PostRepositoryImpl(private val postDao: PostDao,
                         private val postApiService: PostApiService
): PostRepository {

    override suspend fun fetchNewPage(page: Int): Result<Boolean, Error> {
        return withContext(Dispatchers.Main) {
            val existingPosts = postDao.getPostsByPage(10, (page - 1) * 10).firstOrNull()
            if (!existingPosts.isNullOrEmpty()) {
                return@withContext Result.Success(true) // Skip fetching if cached
            }

            fetchAndUpdatePosts(page)
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

    // Fetch + update for WorkManager or initial load
    override suspend fun fetchAndUpdatePosts(page: Int): Result<Boolean, Error> {
        return withContext(Dispatchers.Main) {
            try {
                retry(times = 3, delayMillis = 2000) {
                    val response = postApiService.getPosts(
                        apiKey = "13398314-67b0a9023aca061e2950dbb5a",
                        page = page
                    )

                    if (response.isSuccessful) {
                        val posts = response.body()?.hits
                            ?.filter { it.likes > 50 && it.comments > 50 }
                            ?.map { it.toDomain().toEntity() }
                            ?: emptyList()

                        if (posts.isNotEmpty()) {
                            postDao.deleteAllPosts() // Clear existing data for full refresh
                            postDao.upsertPosts(posts)
                            return@retry Result.Success(true)
                        } else {
                            throw Exception("Empty or invalid data received from server.")
                        }
                    } else {
                        throw Exception("Failed to fetch posts: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("PostRepository", "Error fetching and updating posts", e)
                return@withContext Result.Error(DataError.Network.UNKNOWN)
            }
        }
    }
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