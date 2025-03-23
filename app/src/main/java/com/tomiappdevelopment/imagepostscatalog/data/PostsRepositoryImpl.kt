package com.tomiappdevelopment.imagepostscatalog.data

import com.tomiappdevelopment.imagepostscatalog.data.local.PostDao
import com.tomiappdevelopment.imagepostscatalog.data.maper.toDomainPost
import com.tomiappdevelopment.imagepostscatalog.data.maper.toDomainPostByLike
import com.tomiappdevelopment.imagepostscatalog.data.maper.toEntity
import com.tomiappdevelopment.imagepostscatalog.data.maper.toPostByLikesEntity
import com.tomiappdevelopment.imagepostscatalog.data.remote.PostApiService
import com.tomiappdevelopment.imagepostscatalog.domain.PostRepository
import com.tomiappdevelopment.imagepostscatalog.domain.modules.Post
import com.tomiappdevelopment.imagepostscatalog.domain.util.DataError
import com.tomiappdevelopment.imagepostscatalog.domain.util.Error
import com.tomiappdevelopment.imagepostscatalog.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PostRepositoryImpl(private val postDao: PostDao,
                         private val postApiService: PostApiService
): PostRepository {

    override suspend fun fetchNewPage(page: Int): Result<Boolean, Error> {
        val existingPosts = postDao.getNewPostsByPage(10, (page - 1) * 10).firstOrNull()
        if (!existingPosts.isNullOrEmpty()) {
            return Result.Success(true)
        }

        return try {
            val response = postApiService.getPosts(apiKey = "13398314-67b0a9023aca061e2950dbb5a", page = page)
            val posts = response.hits
                .filter { it.comments > 50 && it.likes > 50 }
                // Filter posts with >50 comments and >50 likes
                .map { it.toDomain().toEntity() }
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
        return postDao.getNewPostsByPage(limit, offset).map { it.toDomainPost() }
    }

    override suspend fun upsertPosts(posts: List<Post>) {
        postDao.upsertPosts(posts.toEntity()) // Mapping domain to entity before upsert

    }

    override suspend fun deleteAllPosts() {
        // Delete all posts from the local database
        postDao.deleteAllPosts()
    }


    // Fetch posts by likes from the API and store in the database
    override suspend fun fetchPostsByLikes(page: Int): Result<Boolean, Error> {
        // Check if we already have data for this page in the database
        val existingPosts = postDao.getPostsByPageAndLikes(10, (page - 1) * 10).firstOrNull()
        if (!existingPosts.isNullOrEmpty()) {
            return Result.Success(true)  // If data exists, no need to fetch from API
        }

        return try {
            // Fetch from API
            withContext(Dispatchers.IO) {
                val response = postApiService.getPosts(
                    apiKey = "13398314-67b0a9023aca061e2950dbb5a",
                    page = 777
                )

                // Filter posts by likes > 50
                val posts = response.hits
                    .filter { it.likes > 50 }  // Filter by likes > 50
                    .map { it.toDomain().toPostByLikesEntity() } // Map to entity

                // Insert filtered posts into the database (upsert)
                postDao.upsertPostsByLikes(posts)
                Result.Success(true)
            }

        } catch (e: Exception) {
            // Handle error and return result
            Result.Error(DataError.Network.UNKNOWN)
        }
    }

    // Fetch paginated posts from the database (ordered by likes)
    override fun getPostsByLikes(page: Int): Flow<List<Post>> {
        val limit = 10  // Limit per page
        val offset = (page - 1) * limit  // Calculate offset for pagination

        // Fetch posts from the local database (cache)
        return postDao.getPostsByPageAndLikes(limit, offset).map { it.toDomainPostByLike() }
    }

    // Function to delete all posts by likes (useful for refreshing data)
    override suspend fun deleteAllPostsByLikes() {
        postDao.deleteAllPostsByLike()  // Clear the postsByLikes table
    }
}

