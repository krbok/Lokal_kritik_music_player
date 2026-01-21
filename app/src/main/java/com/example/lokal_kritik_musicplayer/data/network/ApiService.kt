package com.example.lokal_kritik_musicplayer.data.network

import com.example.lokal_kritik_musicplayer.data.model.*
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("search/songs")
    suspend fun searchSongs(
        @Query("query") query: String,
        @Query("limit") limit: Int = 20
    ): SongResponse

    @GET("search/artists")
    suspend fun searchArtists(
        @Query("query") query: String,
        @Query("limit") limit: Int = 20
    ): ArtistResponse

    @GET("search/albums")
    suspend fun searchAlbums(
        @Query("query") query: String,
        @Query("limit") limit: Int = 20
    ): AlbumResponse

    @GET("artists")
    suspend fun getArtistDetails(
        @Query("id") id: String
    ): ArtistDetailResponse

    @GET("albums")
    suspend fun getAlbumDetails(
        @Query("id") id: String
    ): AlbumDetailResponse

    @GET("modules")
    suspend fun getHomeModules(
        @Query("language") language: String = "hindi,english"
    ): HomeModulesResponse

    @GET("songs/lyrics")
    suspend fun getLyrics(
        @Query("id") id: String
    ): LyricsResponse
}

data class LyricsResponse(
    val success: Boolean,
    val data: LyricsData?
)

data class LyricsData(
    val lyrics: String?,
    val snippet: String?
)
