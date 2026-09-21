package com.orphean.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.orphean.presentation.home.AlbumDetailScreen
import com.orphean.presentation.home.HomeScreen
import com.orphean.presentation.home.HomeViewModel
import com.orphean.presentation.home.SplashScreen
import com.orphean.presentation.player.MiniPlayer
import com.orphean.presentation.player.NowPlayingScreen
import com.orphean.presentation.player.PlayerViewModel
import com.orphean.presentation.settings.SettingsScreen
import com.orphean.presentation.settings.SettingsViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun NavGraph(
    homeViewModel: HomeViewModel,
    playerViewModel: PlayerViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToPlayer = { navController.navigate("player") },
                    onNavigateToAlbumDetail = { navController.navigate("album_detail") },
                    onNavigateToSettings = { navController.navigate("settings") }
                )
            }
            composable("album_detail") {
                AlbumDetailScreen(
                    viewModel = homeViewModel,
                    onBack = { navController.navigateUp() },
                    onNavigateToPlayer = { navController.navigate("player") }
                )
            }
            composable("player") {
                NowPlayingScreen(
                    viewModel = playerViewModel,
                    onBack = { navController.navigateUp() }
                )
            }
            composable("settings") {
                val settingsViewModel = hiltViewModel<SettingsViewModel>()
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onBack = { navController.navigateUp() }
                )
            }
        }

        AnimatedVisibility(
            visible = currentRoute == "home",
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            MiniPlayer(
                viewModel = playerViewModel,
                onExpand = { navController.navigate("player") }
            )
        }
    }
}
