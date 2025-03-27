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
import com.tomiappdevelopment.imagepostscatalog.domain.util.mapHttpErrorCodeToDataError
import com.tomiappdevelopment.imagepostscatalog.domain.util.mapThrowableToDataError
import com.tomiappdevelopment.imagepostscatalog.domain.util.retry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PostRepositoryImpl(private val postDao: PostDao,
                         private val postApiService: PostApiService
): PostRepository {

    companion object {
        private const val PAGE_SIZE = 200
    }

    //should be implemented with some all around error handling(in the fetchAndUpdatePosts function)
    override suspend fun fetchNewPage(): Result<Boolean, Error> {

        val currentPage = postDao.getPageCounter()?.fetchedPages ?: 0
        val nextPage = currentPage + 1

        return withContext(Dispatchers.IO) {
            try {
                return@withContext fetchAndUpdatePosts(nextPage)
            } catch (e: Exception) {
                // Handle any errors during the fetch process
                return@withContext Result.Error(DataError.Network.UNKNOWN)
            }
        }
    }

    override fun getPostsFlow(): Flow<List<Post>> {

        // Fetch posts from the local database (cache)
        return postDao.getPostsByPage().map { it.toDomain() }
    }

    override suspend fun upsertPosts(posts: List<Post>) {
        postDao.upsertPosts(posts.toEntity()) // Mapping domain to entity before upsert

    }

    override suspend fun deleteAllPosts() {
        // Delete all posts from the local database
        postDao.deleteAllPosts()
    }

    override suspend fun fetchAndUpdatePosts(pageArg: Int): Result<Boolean, Error> {
        return withContext(Dispatchers.IO) {
            // Set page to 1 if it's the initial load or worker flag (-1 indicates a full refresh)
            val page = if (pageArg == -1) 1 else pageArg

            try {
                retry(times = 3, delayMillis = 2000) {
                    val response = postApiService.getPosts(
                        apiKey = "13398314-67b0a9023aca061e2950dbb5a",
                        perPage = PAGE_SIZE,
                        editorsChoice = true,
                        page = page
                    )

                    if (response.isSuccessful) {
                        val posts = response.body()?.hits
                            ?.filter { it.likes > 50 && it.comments > 50 }
                            ?.map { it.toDomain().toEntity() }
                            ?: emptyList()

                        if (posts.isNotEmpty()) {
                            if (pageArg == -1) {
                                postDao.deleteAllPosts() // Clear existing data for full refresh
                            }
                            postDao.insertPostsAndUpdatePageCounter(posts, pageNumber = page)
                            return@retry Result.Success(true)
                        } else {
                            throw Exception("Empty or invalid data received from server.")
                        }
                    } else {

                        return@retry Result.Error(mapHttpErrorCodeToDataError(response.code()))
                    }
                }
            } catch (e: Exception) {
                Log.e("PostRepository", "Error fetching and updating posts", e)

                // If it's an exception, map it to a network error
                val error = mapThrowableToDataError(e)
                return@withContext Result.Error(error)
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