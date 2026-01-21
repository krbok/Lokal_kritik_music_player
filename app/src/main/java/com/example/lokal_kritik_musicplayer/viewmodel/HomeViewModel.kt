package com.example.lokal_kritik_musicplayer.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokal_kritik_musicplayer.data.model.Song
import com.example.lokal_kritik_musicplayer.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches

    init {
        // Preload Latest Songs
        searchSongs("Latest", saveToRecent = false)
    }

    fun searchSongs(query: String, saveToRecent: Boolean = true) {
        if (query.isBlank()) {
            _songs.value = emptyList()
            return
        }
        
        viewModelScope.launch {
            Log.d("HomeViewModel", "API call for query: $query")
            _isLoading.value = true
            _error.value = null
            try {
                val response = repository.searchSongs(query)
                if (response.success) {
                    _songs.value = response.data?.results ?: emptyList()
                    if (saveToRecent && query != "Latest" && _songs.value.isNotEmpty()) {
                        addToRecentSearches(query)
                    }
                } else {
                    _songs.value = emptyList()
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Something went wrong"
                _songs.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun addToRecentSearches(query: String) {
        val currentList = _recentSearches.value.toMutableList()
        currentList.remove(query)
        currentList.add(0, query)
        if (currentList.size > 10) currentList.removeAt(10)
        _recentSearches.value = currentList
    }

    fun removeFromRecentSearches(query: String) {
        _recentSearches.value = _recentSearches.value.filter { it != query }
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }
}
