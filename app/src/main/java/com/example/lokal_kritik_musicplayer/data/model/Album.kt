package com.example.lokal_kritik_musicplayer.data.model

data class Album(
    val id: String,
    val name: String,
    val year: String?,
    val type: String?,
    val songCount: String?,
    val url: String?,
    val image: List<Image>?,
    val artists: Artists?
)

data class AlbumResponse(
    val success: Boolean,
    val data: AlbumData?
)

data class AlbumData(
    val results: List<Album>?
)

data class AlbumDetailResponse(
    val success: Boolean,
    val data: AlbumDetailData?
)

data class AlbumDetailData(
    val id: String,
    val name: String,
    val year: String?,
    val type: String?,
    val songCount: String?,
    val url: String?,
    val image: List<Image>?,
    val artists: Artists?,
    val songs: List<Song>?
)
