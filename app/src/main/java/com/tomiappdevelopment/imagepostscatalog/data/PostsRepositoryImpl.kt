package com.tomiappdevelopment.imagepostscatalog.data

import android.util.Log
import com.tomiappdevelopment.imagepostscatalog.BuildConfig.API_KEY
import com.tomiappdevelopment.imagepostscatalog.data.local.MetaDataEntity
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

    companion object {
        private const val PAGE_SIZE = 150
    }

    override suspend fun fetchNewPage(page: Int): Result<Boolean, Error> {
        return withContext(Dispatchers.Main) {
            val existingMeta = postDao.getMetaDataObj()
            val requiredSize = (page) * PAGE_SIZE
            val existingPosts = postDao.getPostsFlow().firstOrNull()
            if (!existingPosts.isNullOrEmpty() && existingPosts.size >= existingMeta.) {
                return@withContext Result.Success(true) // Skip fetching if cached
            }

            fetchAndUpdatePosts(page)
        }
    }

    override fun getPostFlow(page: Int): Flow<List<Post>> {

        // Fetch posts from the local database (cache)
        return postDao.getPostsFlow().map { it.toDomain() }
    }

    override suspend fun upsertPosts(posts: List<Post>) {
        postDao.upsertPosts(posts.toEntity()) // Mapping domain to entity before upsert

    }

    override suspend fun deleteAllPosts() {
        // Delete all posts from the local database
        postDao.clearAllData()
    }

    override suspend fun fetchAndUpdatePosts(page: Int): Result<Boolean, Error> {
        return withContext(Dispatchers.IO) {
            val thePage = if (page == -1) 1 else page

            try {
                retry(times = 3, delayMillis = 2000) {
                    val response = postApiService.getPosts(
                        apiKey = API_KEY, // Use a constant
                        page = thePage
                    )

                    if (response.isSuccessful) {
                        val posts = response.body()?.hits
                            ?.filter { it.likes > 50 && it.comments > 50 }
                            ?.map { it.toDomain().toEntity() }
                            ?: emptyList()

                        if (posts.isNotEmpty()) {
                            val existingMeta = postDao.getMetaDataObj() ?: MetaDataEntity()
                            val updatedMeta = existingMeta.copy(
                                lastFetchedPage = thePage,
                                filteredSize = posts.size,
                                totalFetchedSize = response.body()?.hits?.size ?: 0,
                                lastUpdateTime = System.currentTimeMillis()
                            )

                            postDao.upsertPostsWithMetaData(
                                posts,
                                updatedMeta
                            )

                            return@retry Result.Success(true)
                        } else if (page > 0) {
                            // ✅ Edge case: Partial data on valid page → Attempt next page if it's within limit
                            if (thePage < 2) {
                                fetchAndUpdatePosts(thePage + 1)
                            }
                            throw Exception("Empty or invalid data received from server.")
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