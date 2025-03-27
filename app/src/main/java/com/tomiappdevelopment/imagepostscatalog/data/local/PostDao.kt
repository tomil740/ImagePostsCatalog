package com.tomiappdevelopment.imagepostscatalog.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Query("SELECT * FROM posts ORDER BY likes DESC")
    fun getPostsByPage(): Flow<List<PostEntity>>

    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPosts(postEntities: List<PostEntity>)

    @Transaction
    suspend fun insertPostsAndUpdatePageCounter(postEntities: List<PostEntity>, pageNumber: Int) {
        upsertPosts(postEntities)

        val pageCounter = getPageCounter()
        if (pageCounter != null) {
            updateFetchedPageCount(pageNumber)
        } else {
            insertPageCounter(PageCounterEntity(id = 0, fetchedPages = pageNumber))
        }
    }

    @Query("UPDATE page_counter SET fetchedPages = :pageNumber WHERE id = 0")
    suspend fun updateFetchedPageCount(pageNumber: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPageCounter(pageCounter: PageCounterEntity)

    @Query("SELECT * FROM page_counter WHERE id = 0")
    suspend fun getPageCounter(): PageCounterEntity?
}