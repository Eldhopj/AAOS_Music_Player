package com.example.aaos.music

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.aaos.music.feature.player.FullPlayer
import com.example.aaos.music.feature.player.PlayerViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

import com.example.aaos.music.core.ui.theme.CarMusicTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarMusicTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: PlayerViewModel = hiltViewModel()
                    val state by viewModel.state.collectAsState()
                    FullPlayer(
                        state = state,
                        onEvent = viewModel::handleEvent,
                        // In real app, check config for LHD/RHD
                        isLhd = true 
                    )
                }
            }
        }
    }
}
