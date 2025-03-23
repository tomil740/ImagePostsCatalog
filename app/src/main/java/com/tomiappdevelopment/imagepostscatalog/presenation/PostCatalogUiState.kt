package com.tomiappdevelopment.imagepostscatalog.presenation

import com.tomiappdevelopment.imagepostscatalog.domain.modules.Post

data class PostCatalogUiState(
    val posts: List<Post> = emptyList(),
    val page:Int=1,
    val isLoading: Boolean = false,
)
