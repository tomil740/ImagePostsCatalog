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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class PostsCatalogViewModel(
    private val postsRepo:PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostCatalogUiState())
    val uiState: StateFlow<PostCatalogUiState> get() = _uiState

    // SharedFlow for error messages
    private val _errorState = Channel<String>()
    val errorState: Channel<String> get() = _errorState

    init {
        // Collect the current page and fetch posts accordingly
        // Assuming you have a _currentPage Flow and a getPostsByPage function from the repository
        // Make sure you keep the state in memory across configuration changes
        viewModelScope.launch {
            postsRepo.getPostsFlow().collect{ posts->
                _uiState.update { it.copy(posts = posts) }
            }
        }
        viewModelScope.launch {
            //check if it is first startup
            delay(500)
            if (uiState.value.posts.isEmpty()){
                onLoadMore()
            }
        }
    }



    // Function to handle the "load more" behavior
    fun onLoadMore() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = postsRepo.fetchNewPage()

            when (result) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _errorState.send("successfully fetched new data")
                }
                is Result.Error -> {
                    // Handle error by emitting it through errorState
                    _uiState.update { it.copy(isLoading = false) }
                    _errorState.send(result.error.toString())
                }
            }
        }
    }

}