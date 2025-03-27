package com.tomiappdevelopment.imagepostscatalog.presenation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tomiappdevelopment.imagepostscatalog.presenation.components.PostItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsCatalogScreen(viewModel: PostsCatalogViewModel) {
    val state by viewModel.uiState.collectAsState()
    val errorState by viewModel.errorState.collectAsState(initial = "")

    val snackbarHostState = remember { SnackbarHostState() }

    // Collect error state from ViewModel and display in a Snackbar
    LaunchedEffect(key1 = errorState) {
        if (errorState.isNotEmpty()) {
            snackbarHostState.showSnackbar(errorState)
        }
    }

    val listState = rememberLazyListState()

    // Trigger "Load More" when the user reaches the bottom of the list
    LaunchedEffect(listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index) {
        val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        // Check if the last visible item is at the end of the list
        if (lastVisibleItemIndex == state.posts.size - 1 && !state.isLoading) {
            viewModel.onLoadMore() // Trigger load more
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Posts Catalog") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (state.isLoading && state.posts.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.posts) { post ->
                            PostItem(post = post)
                        }

                        // Show a loading spinner at the bottom when loading more data
                        item {
                            if (state.isLoading) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}