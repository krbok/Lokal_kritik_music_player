package com.example.lokal_kritik_musicplayer.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokal_kritik_musicplayer.data.model.Song
import com.example.lokal_kritik_musicplayer.data.network.ApiService
import com.example.lokal_kritik_musicplayer.service.MusicService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SongInfo(
    val url: String,
    val title: String,
    val imageUrl: String? = null,
    val duration: Long = 0L,
    val artist: String? = null,
    val songId: String? = null
)

enum class RepeatMode {
    NONE, ALL, ONE
}

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) : ViewModel() {

    private var progressJob: Job? = null
    private var currentUrl: String? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentSongTitle = MutableStateFlow<String?>(null)
    val currentSongTitle: StateFlow<String?> = _currentSongTitle

    private val _currentSongImage = MutableStateFlow<String?>(null)
    val currentSongImage: StateFlow<String?> = _currentSongImage

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    private val _queue = MutableStateFlow<List<SongInfo>>(emptyList())
    val queue: StateFlow<List<SongInfo>> = _queue
    
    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    private val _favoriteSongs = MutableStateFlow<List<Song>>(emptyList())
    val favoriteSongs: StateFlow<List<Song>> = _favoriteSongs

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled

    private val _repeatMode = MutableStateFlow(RepeatMode.NONE)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode

    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun toggleShuffle() {
        _isShuffleEnabled.value = !_isShuffleEnabled.value
    }

    fun toggleRepeatMode() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.NONE -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.NONE
        }
    }

    fun setQueue(songs: List<SongInfo>, startIndex: Int = 0) {
        _queue.value = songs
        _currentIndex.value = startIndex.coerceIn(songs.indices)
        
        if (_queue.value.isNotEmpty()) {
            val song = _queue.value[_currentIndex.value]
            playSong(song.url, song.title, song.imageUrl, song.duration, song.songId)
        }
    }

    fun addToQueue(song: SongInfo) {
        val currentQueue = _queue.value.toMutableList()
        val insertIndex = if (currentQueue.isEmpty()) 0 else _currentIndex.value + 1
        currentQueue.add(insertIndex, song)
        _queue.value = currentQueue
    }

    fun playNext(song: SongInfo) {
        val currentQueue = _queue.value.toMutableList()
        if (currentQueue.isEmpty()) {
            setQueue(listOf(song))
            return
        }
        
        val nextIndex = _currentIndex.value + 1
        currentQueue.add(nextIndex, song)
        _queue.value = currentQueue
    }

    fun clearQueue() {
        _queue.value = emptyList()
        _currentIndex.value = 0
        _isPlaying.value = false
        _currentSongTitle.value = null
        _currentSongImage.value = null
        _currentPosition.value = 0L
        _duration.value = 0L
        currentUrl = null
        
        val intent = Intent(context, MusicService::class.java).apply {
            action = MusicService.ACTION_STOP
        }
        context.startService(intent)
    }

    fun removeFromQueue(index: Int) {
        val currentQueue = _queue.value.toMutableList()
        if (index in currentQueue.indices) {
            val removedSong = currentQueue.removeAt(index)
            
            if (index == _currentIndex.value) {
                if (currentQueue.isEmpty()) {
                    clearQueue()
                } else {
                    playNext()
                }
            } else if (index < _currentIndex.value) {
                _currentIndex.value--
            }
            _queue.value = currentQueue
        }
    }

    fun toggleFavorite(song: Song) {
        val currentFavs = _favoriteSongs.value.toMutableList()
        val existing = currentFavs.find { it.id == song.id }
        if (existing != null) {
            currentFavs.remove(existing)
        } else {
            currentFavs.add(song)
        }
        _favoriteSongs.value = currentFavs
    }

    fun playSong(url: String, title: String, imageUrl: String? = null, songDuration: Long = 0L, songId: String? = null) {
        currentUrl = url
        _currentSongTitle.value = title
        _currentSongImage.value = imageUrl
        _duration.value = songDuration
        _currentPosition.value = 0L
        _isPlaying.value = true

        val intent = Intent(context, MusicService::class.java).apply {
            putExtra(MusicService.EXTRA_URL, url)
            putExtra(MusicService.EXTRA_TITLE, title)
            putExtra(MusicService.EXTRA_IMAGE, imageUrl)
        }
        context.startService(intent)
        
        startProgressUpdate()
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
        val intent = Intent(context, MusicService::class.java).apply {
            action = MusicService.ACTION_TOGGLE
        }
        context.startService(intent)
    }

    fun playNext() {
        val songs = _queue.value
        if (songs.isEmpty()) return

        if (_currentIndex.value < songs.size - 1) {
            _currentIndex.value++
            val song = songs[_currentIndex.value]
            playSong(song.url, song.title, song.imageUrl, song.duration, song.songId)
        } else if (_repeatMode.value == RepeatMode.ALL) {
            _currentIndex.value = 0
            val song = songs[_currentIndex.value]
            playSong(song.url, song.title, song.imageUrl, song.duration, song.songId)
        } else {
            _isPlaying.value = false
        }
    }

    fun playPrevious() {
        val songs = _queue.value
        if (songs.isEmpty()) return

        if (_currentIndex.value > 0) {
            _currentIndex.value--
        } else {
            _currentIndex.value = songs.size - 1
        }
        
        val song = songs[_currentIndex.value]
        playSong(song.url, song.title, song.imageUrl, song.duration, song.songId)
    }

    fun seekForward() {
        _currentPosition.value = (_currentPosition.value + 10000).coerceAtMost(_duration.value)
        val intent = Intent(context, MusicService::class.java).apply {
            action = MusicService.ACTION_FORWARD
        }
        context.startService(intent)
    }

    fun seekBackward() {
        _currentPosition.value = (_currentPosition.value - 10000).coerceAtLeast(0L)
        val intent = Intent(context, MusicService::class.java).apply {
            action = MusicService.ACTION_REVERSE
        }
        context.startService(intent)
    }

    private fun startProgressUpdate() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive) {
                if (_isPlaying.value) {
                    val nextPos = _currentPosition.value + 1000
                    if (_duration.value > 0 && nextPos >= _duration.value) {
                        _currentPosition.value = _duration.value
                        if (_repeatMode.value == RepeatMode.ONE) {
                            val song = _queue.value[_currentIndex.value]
                            playSong(song.url, song.title, song.imageUrl, song.duration, song.songId)
                        } else {
                            playNext()
                        }
                    } else {
                        _currentPosition.value = nextPos
                    }
                }
                delay(1000)
            }
        }
    }

    fun seekToFraction(fraction: Float) {
        val dur = _duration.value
        if (dur > 0) {
            val newPos = (dur * fraction).toLong()
            _currentPosition.value = newPos
            val intent = Intent(context, MusicService::class.java).apply {
                action = MusicService.ACTION_SEEK
                putExtra(MusicService.EXTRA_POSITION, newPos)
            }
            context.startService(intent)
        }
    }

    override fun onCleared() {
        super.onCleared()
        progressJob?.cancel()
    }
}
