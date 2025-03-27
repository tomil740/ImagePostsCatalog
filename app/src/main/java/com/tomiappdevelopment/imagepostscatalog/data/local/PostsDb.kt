package com.tomiappdevelopment.imagepostscatalog.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PostEntity::class,PageCounterEntity::class], version = 3, exportSchema = false)
abstract class PostsDb : RoomDatabase() {
    abstract fun postDao(): PostDao
}