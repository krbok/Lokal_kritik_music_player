package com.example.lokal_kritik_musicplayer.ui.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.lokal_kritik_musicplayer.viewmodel.PlayerViewModel
import com.example.lokal_kritik_musicplayer.viewmodel.SongInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueScreen(
    navController: NavController,
    playerViewModel: PlayerViewModel
) {
    val queue by playerViewModel.queue.collectAsState()
    val currentIndex by playerViewModel.currentIndex.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Playing Queue") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (queue.isNotEmpty()) {
                        TextButton(onClick = { playerViewModel.clearQueue() }) {
                            Text("Clear")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (queue.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Queue is empty", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Add some songs to get started!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(queue) { index, song ->
                    QueueItem(
                        song = song,
                        isCurrent = index == currentIndex,
                        onRemove = { playerViewModel.removeFromQueue(index) },
                        onClick = { 
                            playerViewModel.playSong(song.url, song.title, song.imageUrl, song.duration)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun QueueItem(
    song: SongInfo,
    isCurrent: Boolean,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrent) 4.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.DragHandle, 
                contentDescription = null, 
                tint = if (isCurrent) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.padding(start = 4.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Image(
                painter = rememberAsyncImagePainter(song.imageUrl ?: ""),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = song.artist ?: "Unknown Artist",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!isCurrent) {
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Delete, 
                        contentDescription = "Remove", 
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            } else {
                // Show a "Now Playing" indicator or just some padding
                Text(
                    "Playing",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}
