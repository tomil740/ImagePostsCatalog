package com.tomiappdevelopment.imagepostscatalog.data.maper

import com.tomiappdevelopment.imagepostscatalog.data.local.PostByLikesEntity
import com.tomiappdevelopment.imagepostscatalog.data.local.PostEntity
import com.tomiappdevelopment.imagepostscatalog.domain.modules.Post

// Extension function to map the local Room entity to domain model
fun PostEntity.toDomainPostByLike(): Post {
    return Post(
        id = this.id,
        comments = this.comments,
        likes = this.likes,
        imageUrl = this.imageUrl
    )
}

// Extension function to map a list of Room entities to domain models
fun List<PostEntity>.toDomainPost(): List<Post> {
    return this.map { it.toDomainPostByLike() }
}

// Extension function to map the domain model to local Room entity
fun Post.toEntity(): PostEntity {
    return PostEntity(
        id = this.id,
        comments = this.comments,
        likes = this.likes,
        imageUrl = this.imageUrl
    )
}

// Extension function to map a list of domain models to Room entities
fun List<Post>.toEntity(): List<PostEntity> {
    return this.map { it.toEntity() }
}



// Extension function to map the local Room entity to domain model
fun PostByLikesEntity.toDomainPostByLike(): Post {
    return Post(
        id = this.id,
        comments = this.comments,
        likes = this.likes,
        imageUrl = this.imageUrl
    )
}

// Extension function to map a list of Room entities to domain models
fun List<PostByLikesEntity>.toDomainPostByLike(): List<Post> {
    return this.map { it.toDomainPostByLike() }
}

// Extension function to map the domain model to local Room entity
fun Post.toPostByLikesEntity(): PostByLikesEntity {
    return PostByLikesEntity(
        id = this.id,
        comments = this.comments,
        likes = this.likes,
        imageUrl = this.imageUrl
    )
}
/*
// Extension function to map a list of domain models to Room entities
fun List<Post>.toPostByLikesEntity(): List<PostByLikesEntity> {
    return this.map { it.toPostByLikesEntity() }
}
*/