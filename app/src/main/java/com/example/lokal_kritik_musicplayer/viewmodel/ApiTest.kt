package com.example.lokal_kritik_musicplayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokal_kritik_musicplayer.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApiTest @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    fun testApi() {
        viewModelScope.launch {
            try {
                val response = repository.searchSongs(query = "arijit")
                val count = response.data?.results?.size ?: 0
                println("✅ API Success: $count songs found")
            } catch (e: Exception) {
                println("❌ API Error: ${e.localizedMessage}")
            }
        }
    }
}