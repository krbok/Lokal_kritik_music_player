package com.example.lokal_kritik_musicplayer.ui.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.lokal_kritik_musicplayer.viewmodel.PlayerViewModel
import com.example.lokal_kritik_musicplayer.viewmodel.RepeatMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    navController: NavController,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val title by viewModel.currentSongTitle.collectAsStateWithLifecycle()
    val image by viewModel.currentSongImage.collectAsStateWithLifecycle()
    val position by viewModel.currentPosition.collectAsStateWithLifecycle()
    val duration by viewModel.duration.collectAsStateWithLifecycle()
    val isShuffleEnabled by viewModel.isShuffleEnabled.collectAsStateWithLifecycle()
    val repeatMode by viewModel.repeatMode.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Now Playing") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("queue") }) {
                        Icon(Icons.AutoMirrored.Filled.QueueMusic, contentDescription = "Queue")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // artwork
            Image(
                painter = rememberAsyncImagePainter(model = image ?: ""),
                contentDescription = title ?: "",
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(8.dp)
                    .clip(MaterialTheme.shapes.large),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = title ?: "Unknown",
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // seek bar
            val progress =
                if (duration > 0L) (position.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
                else 0f
            
            Slider(
                value = progress,
                onValueChange = { fraction -> viewModel.seekToFraction(fraction) },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = formatTime(position), style = MaterialTheme.typography.bodySmall)
                Text(text = formatTime(duration), style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main controls row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.toggleShuffle() }) {
                    Icon(
                        Icons.Filled.Shuffle, 
                        contentDescription = "Shuffle",
                        tint = if (isShuffleEnabled) MaterialTheme.colorScheme.primary else LocalContentColor.current
                    )
                }

                IconButton(onClick = { viewModel.playPrevious() }) {
                    Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(36.dp))
                }
                
                IconButton(
                    onClick = { viewModel.togglePlayPause() },
                    modifier = Modifier.size(72.dp)
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }

                IconButton(onClick = { viewModel.playNext() }) {
                    Icon(Icons.Filled.SkipNext, contentDescription = "Next", modifier = Modifier.size(36.dp))
                }

                IconButton(onClick = { viewModel.toggleRepeatMode() }) {
                    val icon = when (repeatMode) {
                        RepeatMode.NONE -> Icons.Filled.Repeat
                        RepeatMode.ALL -> Icons.Filled.Repeat
                        RepeatMode.ONE -> Icons.Filled.RepeatOne
                    }
                    Icon(
                        icon, 
                        contentDescription = "Repeat",
                        tint = if (repeatMode != RepeatMode.NONE) MaterialTheme.colorScheme.primary else LocalContentColor.current
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Secondary controls row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.seekBackward() }) {
                    Icon(Icons.Filled.Replay10, contentDescription = "-10 seconds")
                }
                Spacer(modifier = Modifier.width(32.dp))
                IconButton(onClick = { viewModel.seekForward() }) {
                    Icon(Icons.Filled.Forward10, contentDescription = "+10 seconds")
                }
            }
        }
    }
}

fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
