package com.example.lokal_kritik_musicplayer.ui.albums

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.lokal_kritik_musicplayer.data.model.AlbumDetailData
import com.example.lokal_kritik_musicplayer.data.network.ApiService
import com.example.lokal_kritik_musicplayer.ui.components.SongCard
import com.example.lokal_kritik_musicplayer.viewmodel.PlayerViewModel
import com.example.lokal_kritik_musicplayer.viewmodel.SongInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    private val _albumDetail = MutableStateFlow<AlbumDetailData?>(null)
    val albumDetail: StateFlow<AlbumDetailData?> = _albumDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getAlbumDetails(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getAlbumDetails(id)
                if (response.success) {
                    _albumDetail.value = response.data
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    albumId: String,
    navController: NavController,
    playerViewModel: PlayerViewModel,
    viewModel: AlbumDetailViewModel = hiltViewModel()
) {
    val albumDetail by viewModel.albumDetail.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(albumId) {
        viewModel.getAlbumDetails(albumId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(albumDetail?.name ?: "Album") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            albumDetail?.let { detail ->
                val songs = detail.songs ?: emptyList()
                LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
                    item {
                        AlbumHeader(detail) {
                             if (songs.isNotEmpty()) {
                                val songQueue = songs.mapNotNull { s ->
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
                                if (songQueue.isNotEmpty()) {
                                    playerViewModel.setQueue(songQueue, 0)
                                    navController.navigate("player")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Songs", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    items(songs) { song ->
                        SongCard(
                            song = song,
                            playerViewModel = playerViewModel
                        ) {
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
        }
    }
}

@Composable
fun AlbumHeader(detail: AlbumDetailData, onPlayAll: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = rememberAsyncImagePainter(detail.image?.lastOrNull()?.url ?: ""),
            contentDescription = null,
            modifier = Modifier.size(200.dp).clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(detail.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(detail.artists?.primary?.firstOrNull()?.name ?: "Unknown Artist", style = MaterialTheme.typography.bodyMedium)
        Text("${detail.year ?: ""} • ${detail.songCount ?: "0"} Songs", style = MaterialTheme.typography.bodySmall)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onPlayAll,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(25.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Play All")
        }
    }
}
