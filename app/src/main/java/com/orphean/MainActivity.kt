package com.orphean

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.orphean.presentation.navigation.NavGraph
import com.orphean.presentation.theme.OrpheanTheme
import com.orphean.presentation.navigation.NavGraph
import com.orphean.presentation.theme.OrpheanTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val homeViewModel: com.orphean.presentation.home.HomeViewModel by viewModels()
    private val playerViewModel: com.orphean.presentation.player.PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        setContent {
            OrpheanTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavGraph(
                        homeViewModel = homeViewModel, 
                        playerViewModel = playerViewModel
                    )
                }
            }
        }
    }
}
