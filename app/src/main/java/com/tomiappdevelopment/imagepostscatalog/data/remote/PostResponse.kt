package com.tomiappdevelopment.imagepostscatalog.data.remote

import com.tomiappdevelopment.imagepostscatalog.data.local.PostByLikesEntity
import com.tomiappdevelopment.imagepostscatalog.domain.modules.Post

data class PostResponse(
    val hits: List<PostDto>
)

data class PostDto(
    val id: String,
    val comments: Int,
    val likes: Int,
    val webformatURL: String
) {
    fun toDomain() = Post(
        id = id,
        comments = comments,
        likes = likes,
        imageUrl = webformatURL
    )
    fun toPostByLikesEntity() = PostByLikesEntity(
        id = id,
        comments = comments,
        likes = likes,
        imageUrl = webformatURL
    )


}