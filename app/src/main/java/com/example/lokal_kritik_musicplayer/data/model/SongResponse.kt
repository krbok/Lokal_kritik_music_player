package com.example.lokal_kritik_musicplayer.data.model

data class SongResponse(
    val success: Boolean,
    val data: SongData?
)

data class SongData(
    val results: List<Song>?
)