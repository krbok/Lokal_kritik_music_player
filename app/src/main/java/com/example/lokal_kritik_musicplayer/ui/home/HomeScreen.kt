package com.example.lokal_kritik_musicplayer.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.lokal_kritik_musicplayer.ui.components.SongCard
import com.example.lokal_kritik_musicplayer.viewmodel.HomeViewModel
import com.example.lokal_kritik_musicplayer.viewmodel.PlayerViewModel
import com.example.lokal_kritik_musicplayer.viewmodel.SongInfo

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel
) {
    var query by remember { mutableStateOf("") }
    val songs by viewModel.songs.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                if (query.length > 2) {
                    viewModel.searchSongs(query)
                }
            },
            label = { Text("Search songs, artists, albums") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = ""; viewModel.searchSongs("Latest") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            shape = MaterialTheme.shapes.medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (query.isEmpty() && recentSearches.isNotEmpty()) {
            RecentSearchesSection(
                recentSearches = recentSearches,
                onSearchClick = { 
                    query = it
                    viewModel.searchSongs(it)
                },
                onRemoveClick = { viewModel.removeFromRecentSearches(it) },
                onClearAll = { viewModel.clearRecentSearches() }
            )
        } else if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (songs.isEmpty() && query.isNotEmpty() && !isLoading) {
            NotFoundState()
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(songs) { song ->
                    SongCard(
                        song = song,
                        playerViewModel = playerViewModel,
                        onClick = {
                            val url = song.downloadUrl?.lastOrNull()?.url
                            if (!url.isNullOrEmpty()) {
                                val songQueue = songs.mapNotNull { s ->
                                    val dUrl = s.downloadUrl?.lastOrNull()?.url
                                    if (dUrl != null) {
                                        SongInfo(
                                            url = dUrl,
                                            title = s.name,
                                            imageUrl = s.image?.lastOrNull()?.url,
                                            duration = (s.duration ?: 0).toLong() * 1000L,
                                            artist = s.artists?.primary?.firstOrNull()?.name,
                                            songId = s.id
                                        )
                                    } else null
                                }
                                val startIdx = songQueue.indexOfFirst { it.url == url }.coerceAtLeast(0)
                                playerViewModel.setQueue(songQueue, startIdx)
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

@Composable
fun RecentSearchesSection(
    recentSearches: List<String>,
    onSearchClick: (String) -> Unit,
    onRemoveClick: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Searches",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onClearAll) {
                Text("Clear All", color = MaterialTheme.colorScheme.primary)
            }
        }
        recentSearches.forEach { search ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSearchClick(search) }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.History, contentDescription = null, tint = Color.Gray)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = search, modifier = Modifier.weight(1f))
                IconButton(onClick = { onRemoveClick(search) }) {
                    Icon(Icons.Default.Clear, contentDescription = "Remove", modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun NotFoundState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Placeholder for the "Not Found" illustration
        Box(
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("🙁", style = MaterialTheme.typography.displayLarge)
        }
        Text(
            text = "Not Found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sorry, the keyword you entered cannot be found, please check again or search with another keyword.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}
