package com.example.lokal_kritik_musicplayer.ui.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.lokal_kritik_musicplayer.ui.components.SongCard
import com.example.lokal_kritik_musicplayer.viewmodel.PlayerViewModel
import com.example.lokal_kritik_musicplayer.viewmodel.SongInfo

@Composable
fun FavoritesScreen(
    navController: NavController,
    playerViewModel: PlayerViewModel
) {
    val favoriteSongs by playerViewModel.favoriteSongs.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "My Favorites",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        if (favoriteSongs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No favorites yet. Tap the heart on any song to add it here.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(32.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(favoriteSongs) { song ->
                    SongCard(
                        song = song,
                        playerViewModel = playerViewModel,
                        onClick = {
                            val url = song.downloadUrl?.lastOrNull()?.url
                            if (!url.isNullOrEmpty()) {
                                playerViewModel.playSong(
                                    url = url,
                                    title = song.name,
                                    imageUrl = song.image?.lastOrNull()?.url,
                                    songDuration = (song.duration ?: 0).toLong() * 1000L
                                )
                                navController.navigate("player")
                            }
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}
