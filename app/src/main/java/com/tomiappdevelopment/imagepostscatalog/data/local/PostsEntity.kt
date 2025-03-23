package com.tomiappdevelopment.imagepostscatalog.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey
    val id: String, // Unique identifier to avoid duplicates
    val comments: Int,
    val likes: Int,
    val imageUrl: String
)

@Entity(tableName = "postsByLikes")
data class PostByLikesEntity(
    @PrimaryKey
    val id: String, // Unique identifier to avoid duplicates
    val comments: Int,
    val likes: Int,
    val imageUrl: String
)