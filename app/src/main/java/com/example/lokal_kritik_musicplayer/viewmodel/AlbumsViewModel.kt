package com.example.lokal_kritik_musicplayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokal_kritik_musicplayer.data.model.Album
import com.example.lokal_kritik_musicplayer.data.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        searchAlbums("Top") // Preload with "Top" albums
    }

    fun searchAlbums(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.searchAlbums(query)
                if (response.success) {
                    _albums.value = response.data?.results ?: emptyList()
                } else {
                    _error.value = "Failed to fetch albums"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
