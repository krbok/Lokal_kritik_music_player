package com.example.lokal_kritik_musicplayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokal_kritik_musicplayer.data.model.Artist
import com.example.lokal_kritik_musicplayer.data.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchTopArtists()
    }

    fun fetchTopArtists() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Using "Arijit Singh" as a pre-loaded artist query to ensure we get results initially
                // Or you could search for "popular" or any other common term.
                val response = apiService.searchArtists("Arijit")
                if (response.success) {
                    _artists.value = response.data?.results ?: emptyList()
                } else {
                    _error.value = "Failed to fetch artists"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchArtists(query: String) {
        if (query.isBlank()) {
            fetchTopArtists()
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.searchArtists(query)
                if (response.success) {
                    _artists.value = response.data?.results ?: emptyList()
                } else {
                    _error.value = "Failed to fetch artists"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
