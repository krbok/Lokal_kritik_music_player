package com.example.lokal_kritik_musicplayer.data.model

data class ArtistDetailResponse(
    val success: Boolean,
    val data: ArtistDetailData?
)

data class ArtistDetailData(
    val id: String,
    val name: String,
    val url: String?,
    val image: List<Image>?,
    val topSongs: List<Song>?,
    val topAlbums: List<Album>?,
    val followerCount: String?,
    val isVerified: Boolean?
)
