package com.example.lokal_kritik_musicplayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokal_kritik_musicplayer.data.model.Album
import com.example.lokal_kritik_musicplayer.data.model.Artist
import com.example.lokal_kritik_musicplayer.data.model.HomeModulesData
import com.example.lokal_kritik_musicplayer.data.model.Song
import com.example.lokal_kritik_musicplayer.data.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuggestedViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _trendingSongs = MutableStateFlow<List<Song>>(emptyList())
    val trendingSongs: StateFlow<List<Song>> = _trendingSongs

    private val _popularAlbums = MutableStateFlow<List<Album>>(emptyList())
    val popularAlbums: StateFlow<List<Album>> = _popularAlbums

    private val _recommendedArtists = MutableStateFlow<List<Artist>>(emptyList())
    val recommendedArtists: StateFlow<List<Artist>> = _recommendedArtists

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchInitialData()
    }

    private fun fetchInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Fetch trending songs as preloaded content
                val songResponse = apiService.searchSongs("Latest")
                if (songResponse.success) {
                    _trendingSongs.value = songResponse.data?.results ?: emptyList()
                }

                // Fetch popular albums as preloaded content
                val albumResponse = apiService.searchAlbums("Top")
                if (albumResponse.success) {
                    _popularAlbums.value = albumResponse.data?.results ?: emptyList()
                }

                // Fetch recommended artists
                val artistResponse = apiService.searchArtists("Arijit")
                if (artistResponse.success) {
                    _recommendedArtists.value = artistResponse.data?.results ?: emptyList()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
