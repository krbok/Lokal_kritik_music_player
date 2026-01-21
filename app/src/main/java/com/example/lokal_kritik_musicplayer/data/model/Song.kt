package com.example.lokal_kritik_musicplayer.data.model

data class Song(
    val id: String,
    val name: String,
    val type: String,
    val year: String?,
    val releaseDate: String?,
    val duration: Int?,
    val label: String?,
    val explicitContent: Boolean?,
    val playCount: Long?,
    val language: String?,
    val hasLyrics: Boolean?,
    val url: String?,
    val album: Album?,
    val artists: Artists?,
    val image: List<Image>?,
    val downloadUrl: List<DownloadUrl>?
)