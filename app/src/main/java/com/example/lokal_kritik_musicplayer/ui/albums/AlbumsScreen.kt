package com.example.lokal_kritik_musicplayer.ui.albums

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.lokal_kritik_musicplayer.data.model.Album
import com.example.lokal_kritik_musicplayer.viewmodel.AlbumsViewModel

@Composable
fun AlbumsScreen(
    navController: NavController,
    viewModel: AlbumsViewModel = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }
    val albums by viewModel.albums.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                if (query.length > 2) viewModel.searchAlbums(query)
            },
            label = { Text("Search Albums") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(albums) { album ->
                    AlbumListItem(album = album) {
                        navController.navigate("album_detail/${album.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun AlbumListItem(album: Album, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(album.image?.lastOrNull()?.url ?: ""),
            contentDescription = null,
            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = album.name, style = MaterialTheme.typography.titleMedium)
            Text(text = album.year ?: "", style = MaterialTheme.typography.bodySmall)
        }
    }
}
