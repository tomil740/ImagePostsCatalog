package com.tomiappdevelopment.imagepostscatalog.presenation.components

import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tomiappdevelopment.imagepostscatalog.domain.modules.Post

@Composable
fun PostItem(post: Post) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        // 16:9 aspect ratio image with Coil for lazy loading
        Box(modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(post.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "PostEntity image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Header with comments and likes
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Comments: ${post.comments}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Likes: ${post.likes}", style = MaterialTheme.typography.bodySmall)
        }
    }
}