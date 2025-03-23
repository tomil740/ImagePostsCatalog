package com.tomiappdevelopment.imagepostscatalog.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    // Get posts by page, ordered by likes in descending order, with pagination
    @Query("SELECT * FROM posts ORDER BY id DESC LIMIT :limit OFFSET :offset")
    fun getNewPostsByPage(limit: Int, offset: Int): Flow<List<PostEntity>>

    // Delete all posts (for sync)
    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()

    // Insert or update postEntities (upsert)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsertPosts(postEntities: List<PostEntity>)


    // Get posts by page, ordered by likes in descending order, with pagination
    @Query("SELECT * FROM postsByLikes ORDER BY likes DESC LIMIT :limit OFFSET :offset")
    fun getPostsByPageAndLikes(limit: Int, offset: Int): Flow<List<PostByLikesEntity>>

    // Delete all posts (for sync)
    @Query("DELETE FROM postsByLikes")
    suspend fun deleteAllPostsByLike()

    // Insert or update postEntities (upsert)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsertPostsByLikes(postEntities: List<PostByLikesEntity>)
}