package com.example.lokal_kritik_musicplayer.data.model

data class Artists(
    val primary: List<Artist>?,
    val featured: List<Artist>?,
    val all: List<Artist>?
)

data class Artist(
    val id: String,
    val name: String,
    val role: String?,
    val image: List<Image>?,
    val type: String?,
    val url: String?
)
