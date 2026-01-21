package com.example.lokal_kritik_musicplayer.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.lokal_kritik_musicplayer.data.model.Album
import com.example.lokal_kritik_musicplayer.data.model.Artist
import com.example.lokal_kritik_musicplayer.data.model.Song
import com.example.lokal_kritik_musicplayer.viewmodel.PlayerViewModel
import com.example.lokal_kritik_musicplayer.viewmodel.SongInfo
import com.example.lokal_kritik_musicplayer.viewmodel.SuggestedViewModel

@Composable
fun SuggestedScreen(
    navController: NavController,
    playerViewModel: PlayerViewModel,
    viewModel: SuggestedViewModel = hiltViewModel()
) {
    val trendingSongs by viewModel.trendingSongs.collectAsStateWithLifecycle()
    val popularAlbums by viewModel.popularAlbums.collectAsStateWithLifecycle()
    val recommendedArtists by viewModel.recommendedArtists.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    if (isLoading && trendingSongs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (trendingSongs.isNotEmpty()) {
                SectionHeader("Trending Songs", onSeeAll = { navController.navigate("songs") })
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(trendingSongs) { song ->
                        TrendingSongItem(song) {
                            val url = song.downloadUrl?.lastOrNull()?.url
                            if (url != null) {
                                val songQueue = trendingSongs.mapNotNull { s ->
                                    val dUrl = s.downloadUrl?.lastOrNull()?.url
                                    if (dUrl != null) {
                                        SongInfo(
                                            url = dUrl,
                                            title = s.name,
                                            imageUrl = s.image?.lastOrNull()?.url,
                                            duration = (s.duration ?: 0).toLong() * 1000L,
                                            songId = s.id
                                        )
                                    } else null
                                }
                                val startIdx = songQueue.indexOfFirst { it.url == url }.coerceAtLeast(0)
                                if (songQueue.isNotEmpty()) {
                                    playerViewModel.setQueue(songQueue, startIdx)
                                } else {
                                    playerViewModel.playSong(
                                        url = url,
                                        title = song.name,
                                        imageUrl = song.image?.lastOrNull()?.url,
                                        songDuration = (song.duration ?: 0).toLong() * 1000L,
                                        songId = song.id
                                    )
                                }
                                navController.navigate("player")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (popularAlbums.isNotEmpty()) {
                SectionHeader("Popular Albums", onSeeAll = { navController.navigate("albums") })
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(popularAlbums) { album ->
                        AlbumItem(album) {
                            navController.navigate("album_detail/${album.id}")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            if (recommendedArtists.isNotEmpty()) {
                SectionHeader("Recommended Artists", onSeeAll = { navController.navigate("artists") })
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(recommendedArtists) { artist ->
                        SuggestedArtistItem(artist) {
                            navController.navigate("artist_detail/${artist.id}")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp)) // Extra space at bottom
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        TextButton(onClick = onSeeAll) {
            Text("See All", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun TrendingSongItem(song: Song, onClick: () -> Unit) {
    Column(modifier = Modifier.width(140.dp).clickable(onClick = onClick)) {
        Image(
            painter = rememberAsyncImagePainter(song.image?.lastOrNull()?.url ?: ""),
            contentDescription = null,
            modifier = Modifier.size(140.dp).clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Text(song.name, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(song.artists?.primary?.firstOrNull()?.name ?: "", style = MaterialTheme.typography.bodySmall, maxLines = 1)
    }
}

@Composable
fun AlbumItem(album: Album, onClick: () -> Unit) {
    Column(modifier = Modifier.width(140.dp).clickable(onClick = onClick)) {
        Image(
            painter = rememberAsyncImagePainter(album.image?.lastOrNull()?.url ?: ""),
            contentDescription = null,
            modifier = Modifier.size(140.dp).clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Text(album.name, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(album.year ?: "", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun SuggestedArtistItem(artist: Artist, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(100.dp).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(artist.image?.lastOrNull()?.url ?: ""),
            contentDescription = null,
            modifier = Modifier.size(100.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = artist.name,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
