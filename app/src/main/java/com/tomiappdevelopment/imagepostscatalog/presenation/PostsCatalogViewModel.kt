package com.tomiappdevelopment.imagepostscatalog.presenation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomiappdevelopment.imagepostscatalog.domain.PostRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.tomiappdevelopment.imagepostscatalog.domain.util.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class PostsCatalogViewModel(
    private val postsRepo:PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostCatalogUiState())
    val uiState: StateFlow<PostCatalogUiState> get() = _uiState

    // Shared flow for error handling
    private val _errorState = MutableSharedFlow<String>(replay = 1)
    val errorState = _errorState

    private val _currentPage = MutableStateFlow<Int>(1)

    init {
        // Collect the current page and fetch posts accordingly
        // Assuming you have a _currentPage Flow and a getPostsByPage function from the repository
        // Make sure you keep the state in memory across configuration changes
        viewModelScope.launch {
            onLoadMore()
            _currentPage
                .flatMapLatest { page ->
                    Log.i("hay", "new page $page")
                    postsRepo.getPostsByPage(page)
                }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())  // Keep data in memory with a default value
                .collect { newPosts ->
                    // Update UI state with the new posts
                    _uiState.update { it.copy(posts = it.posts + newPosts) }
                }
        }
    }



    // Function to handle the "load more" behavior
    fun onLoadMore() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Check if data fetch is needed and attempt to fetch the new page
            val result = postsRepo.fetchNewPage(_currentPage.value + 1)

            when (result) {
                is Result.Success -> {
                    // Increment the page after successful fetch
                    delay(1000)
                    _currentPage.update { it + 1 }
                    _uiState.update { it.copy(isLoading = false) }
                }
                is Result.Error -> {
                    // Handle error by emitting it through errorState
                    _uiState.update { it.copy(isLoading = false) }
                    _errorState.emit("Failed to load more posts. Please try again.")
                }
            }
        }
    }

}