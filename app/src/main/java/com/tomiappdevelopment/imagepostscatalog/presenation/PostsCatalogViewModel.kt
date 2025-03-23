package com.tomiappdevelopment.imagepostscatalog.presenation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomiappdevelopment.imagepostscatalog.domain.PostRepository
import com.tomiappdevelopment.imagepostscatalog.domain.modules.Post
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn


@OptIn(ExperimentalCoroutinesApi::class)
class PostsCatalogViewModel(
    private val postsRepo: PostRepository
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(PostCatalogUiState())
    val uiState: StateFlow<PostCatalogUiState> get() = _uiState

    // Error State
    private val _errorState = MutableSharedFlow<String>(replay = 1)
    val errorState = _errorState

    // Current Page
    private val _currentPage = MutableStateFlow(1)

    // Mode State (Live or Likes)
    private val _mode = MutableStateFlow<UiState>(UiState.LiveMode)
    val mode: StateFlow<UiState> get() = _mode

    init {
        viewModelScope.launch {
            collectPosts()
        }
    }

    // Function to handle data collection based on mode
    private suspend fun collectPosts() {
        combine(_mode, _currentPage) { mode, page ->
            Pair(mode, page)
        }.flatMapLatest { (mode, page) ->
            Log.i("hay", "New page $page in ${if (mode is UiState.LiveMode) "Live" else "Likes"} mode")

            when (mode) {
                is UiState.LiveMode -> postsRepo.getPostsByPage(page)
                is UiState.LikesMode -> postsRepo.getPostsByLikes(page)
            }
        }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
            .collect { newPosts ->
                _uiState.update { it.copy(posts = it.posts + newPosts) }
            }
    }

    // Load More Function
    fun onLoadMore() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val fetchResult = when (_mode.value) {
                is UiState.LiveMode -> postsRepo.fetchNewPage(_currentPage.value + 1)
                is UiState.LikesMode -> postsRepo.fetchPostsByLikes(_currentPage.value + 1)
            }

            when (fetchResult) {
                is Result.Success -> {
                    delay(1000)
                    _currentPage.update { it + 1 }
                    _uiState.update { it.copy(isLoading = false) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _errorState.emit("Failed to load more posts. Please try again.")
                }
            }
        }
    }

    // Mode Toggle
    fun toggleMode() {
        viewModelScope.launch {
            val newMode = if (_mode.value is UiState.LiveMode) UiState.LikesMode else UiState.LiveMode
            _mode.value = newMode

            // Clear existing posts and reset page count
            _uiState.update { it.copy(posts = emptyList()) }
            _currentPage.value = 1

            // Trigger new data fetch immediately
            onLoadMore()
        }
    }
}


// Mode Types
sealed class UiState {
    object LiveMode : UiState()
    object LikesMode : UiState()
}
