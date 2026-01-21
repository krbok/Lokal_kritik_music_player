package com.example.lokal_kritik_musicplayer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.PlaylistAdd
import androidx.compose.material.icons.outlined.Queue
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.lokal_kritik_musicplayer.data.model.Song
import com.example.lokal_kritik_musicplayer.viewmodel.PlayerViewModel
import com.example.lokal_kritik_musicplayer.viewmodel.SongInfo

@Composable
fun SongCard(
    song: Song,
    playerViewModel: PlayerViewModel? = null,
    onClick: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    val favoriteSongs by playerViewModel?.favoriteSongs?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val isFavorite = favoriteSongs.any { it.id == song.id }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(song.image?.lastOrNull()?.url ?: ""),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1
                )
                Text(
                    text = song.artists?.primary?.firstOrNull()?.name ?: "Unknown Artist",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            IconButton(onClick = { 
                playerViewModel?.toggleFavorite(song)
            }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    val songInfo = SongInfo(
                        url = song.downloadUrl?.lastOrNull()?.url ?: "",
                        title = song.name,
                        imageUrl = song.image?.lastOrNull()?.url,
                        duration = (song.duration ?: 0).toLong() * 1000L,
                        artist = song.artists?.primary?.firstOrNull()?.name
                    )

                    DropdownMenuItem(
                        text = { Text("Play Next") },
                        leadingIcon = { Icon(Icons.Outlined.Queue, contentDescription = null) },
                        onClick = {
                            playerViewModel?.playNext(songInfo)
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Add to Queue") },
                        leadingIcon = { Icon(Icons.Outlined.PlaylistAdd, contentDescription = null) },
                        onClick = {
                            playerViewModel?.addToQueue(songInfo)
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}
