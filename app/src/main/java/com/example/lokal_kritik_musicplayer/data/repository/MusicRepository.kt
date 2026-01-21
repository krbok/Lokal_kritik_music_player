package com.example.lokal_kritik_musicplayer.data.repository

import com.example.lokal_kritik_musicplayer.data.model.SongResponse
import com.example.lokal_kritik_musicplayer.data.network.ApiService
import javax.inject.Inject

class MusicRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun searchSongs(query: String): SongResponse {
        return apiService.searchSongs(query)
    }
}