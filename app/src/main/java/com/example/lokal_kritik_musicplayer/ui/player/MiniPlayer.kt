package com.example.lokal_kritik_musicplayer.ui.player
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.lokal_kritik_musicplayer.viewmodel.PlayerViewModel

@Composable
fun MiniPlayer(
    viewModel: PlayerViewModel = hiltViewModel(),
    onPlayerClick: () -> Unit = {}
) {
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val currentSongTitle by viewModel.currentSongTitle.collectAsStateWithLifecycle()
    val currentSongImage by viewModel.currentSongImage.collectAsStateWithLifecycle()
    val position by viewModel.currentPosition.collectAsStateWithLifecycle()
    val duration by viewModel.duration.collectAsStateWithLifecycle()

    if (currentSongTitle != null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { onPlayerClick() }
        ) {
            // Progress bar
            val progress = if (duration > 0) position.toFloat() / duration.toFloat() else 0f
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Row(
                modifier = Modifier
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Song Image
                Image(
                    painter = rememberAsyncImagePainter(model = currentSongImage),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Title
                Text(
                    text = currentSongTitle ?: "Unknown",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Controls
                IconButton(onClick = { viewModel.playPrevious() }) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous"
                    )
                }

                IconButton(onClick = { viewModel.togglePlayPause() }) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play"
                    )
                }

                IconButton(onClick = { viewModel.playNext() }) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next"
                    )
                }
            }
        }
    }
}
