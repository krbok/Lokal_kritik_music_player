package com.example.lokal_kritik_musicplayer.data.model

data class ArtistResponse(
    val success: Boolean,
    val data: ArtistData?
)

data class ArtistData(
    val results: List<Artist>?
)
