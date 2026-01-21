package com.example.lokal_kritik_musicplayer.data.model

data class HomeModulesResponse(
    val success: Boolean,
    val data: HomeModulesData?
)

data class HomeModulesData(
    val albums: List<Album>?,
    val charts: List<Chart>?,
    val playlists: List<Playlist>?,
    val trending: TrendingData?
)

data class Chart(
    val id: String,
    val title: String,
    val subtitle: String?,
    val type: String?,
    val image: List<Image>?
)

data class Playlist(
    val id: String,
    val title: String,
    val subtitle: String?,
    val type: String?,
    val image: List<Image>?
)

data class TrendingData(
    val songs: List<Song>?,
    val albums: List<Album>?
)
