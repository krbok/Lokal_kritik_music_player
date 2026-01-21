package com.example.lokal_kritik_musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lokal_kritik_musicplayer.ui.albums.AlbumDetailScreen
import com.example.lokal_kritik_musicplayer.ui.albums.AlbumsScreen
import com.example.lokal_kritik_musicplayer.ui.artists.ArtistDetailScreen
import com.example.lokal_kritik_musicplayer.ui.artists.ArtistScreen
import com.example.lokal_kritik_musicplayer.ui.components.MiniPlayer
import com.example.lokal_kritik_musicplayer.ui.favorites.FavoritesScreen
import com.example.lokal_kritik_musicplayer.ui.home.HomeScreen
import com.example.lokal_kritik_musicplayer.ui.home.SuggestedScreen
import com.example.lokal_kritik_musicplayer.ui.player.PlayerScreen
import com.example.lokal_kritik_musicplayer.ui.player.QueueScreen
import com.example.lokal_kritik_musicplayer.ui.theme.MusicPlayerTheme
import com.example.lokal_kritik_musicplayer.viewmodel.PlayerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val playerViewModel: PlayerViewModel = hiltViewModel()
            val isDark by playerViewModel.isDarkMode.collectAsState()

            MusicPlayerTheme(darkTheme = isDark) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val topLevelRoutes = listOf("suggested", "songs", "artists", "albums", "favorites", "queue")

                Scaffold(
                    topBar = {
                        if (currentDestination?.route in topLevelRoutes) {
                            Column {
                                TopAppBar(
                                    title = { 
                                        Text(
                                            "Lokal Music", 
                                            style = MaterialTheme.typography.titleLarge, 
                                            fontWeight = FontWeight.Bold 
                                        ) 
                                    },
                                    actions = {
                                        IconButton(onClick = { playerViewModel.toggleTheme() }) {
                                            Icon(
                                                imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                                contentDescription = "Toggle Theme"
                                            )
                                        }
                                    }
                                )
                                HomeTopTabBar(
                                    selectedTab = currentDestination?.route ?: "suggested",
                                    onTabSelected = { route ->
                                        navController.navigate(route) {
                                            popUpTo("suggested") { inclusive = false }
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }
                        }
                    },
                    bottomBar = {
                        if (currentDestination?.route != "player") {
                            MiniPlayer(
                                playerViewModel = playerViewModel,
                                onClick = { navController.navigate("player") }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        NavHost(
                            navController = navController,
                            startDestination = "suggested"
                        ) {
                            composable("suggested") { SuggestedScreen(navController, playerViewModel) }
                            composable("songs") { HomeScreen(navController, playerViewModel = playerViewModel) }
                            composable("artists") { ArtistScreen(navController) }
                            composable("albums") { AlbumsScreen(navController) }
                            composable("favorites") { FavoritesScreen(navController, playerViewModel) }
                            composable("queue") { QueueScreen(navController, playerViewModel) }
                            
                            composable(
                                "artist_detail/{artistId}",
                                arguments = listOf(navArgument("artistId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val artistId = backStackEntry.arguments?.getString("artistId") ?: ""
                                ArtistDetailScreen(artistId, navController, playerViewModel)
                            }
                            composable(
                                "album_detail/{albumId}",
                                arguments = listOf(navArgument("albumId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val albumId = backStackEntry.arguments?.getString("albumId") ?: ""
                                AlbumDetailScreen(albumId, navController, playerViewModel)
                            }
                            composable("player") {
                                PlayerScreen(navController = navController, viewModel = playerViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeTopTabBar(selectedTab: String, onTabSelected: (String) -> Unit) {
    val tabs = listOf(
        "Suggested" to "suggested",
        "Songs" to "songs",
        "Artists" to "artists",
        "Albums" to "albums",
        "Favorites" to "favorites",
        "Queue" to "queue"
    )
    ScrollableTabRow(
        selectedTabIndex = tabs.indexOfFirst { it.second == selectedTab }.coerceAtLeast(0),
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        divider = {}
    ) {
        tabs.forEach { (label, route) ->
            Tab(
                selected = selectedTab == route,
                onClick = { onTabSelected(route) },
                text = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (selectedTab == route) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}
