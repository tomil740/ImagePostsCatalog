package com.tomiappdevelopment.imagepostscatalog.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow


@Dao
interface PostDao {

    // Get posts by page, ordered by likes in descending order, with pagination
    @Query("SELECT * FROM posts ORDER BY likes")
    fun getPostsFlow(): Flow<List<PostEntity>>

    // Delete all posts (for sync)
    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()

    // Insert or update postEntities (upsert)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPosts(postEntities: List<PostEntity>)

    // Metadata handling functions
    @Query("DELETE FROM meta_data")
    suspend fun deleteAllMetaData()

    @Query("SELECT * FROM meta_data")
    fun getMetaDataObj(): MetaDataEntity?

    // Insert or update metadata (upsert)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMetaData(metaDataEntity: MetaDataEntity)

    // 👉 **Transaction to handle data + metadata consistency**
    @Transaction
    suspend fun upsertPostsWithMetaData(
        postEntities: List<PostEntity>,
        metaDataEntity: MetaDataEntity
    ) {
        upsertPosts(postEntities)
        updateMetaData(metaDataEntity)
    }

    // 👉 **Transaction to clear data and metadata together**
    @Transaction
    suspend fun clearAllData() {
        deleteAllPosts()
        deleteAllMetaData()
    }
}