package com.example.aaos.music

import android.Manifest
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aaos.music.core.ui.theme.CarMusicTheme
import com.example.aaos.music.core.ui.theme.gradientColors
import com.example.aaos.music.driveside.DriveSideBroadcastContract
import com.example.aaos.music.driveside.DriveSideToggleReceiver
import com.example.aaos.music.ui.player.PlayerDashboardScreen
import com.example.aaos.music.ui.player.SmallPlayerScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var driveSideReceiver: DriveSideToggleReceiver? = null

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarMusicTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.gradientColors.start,
                                        MaterialTheme.gradientColors.end
                                    )
                                )
                            )
                    ) {
                        PermissionWrapper {
                            var showDetails by remember { mutableStateOf(false) }

                            SharedTransitionLayout {
                                AnimatedContent(
                                    targetState = showDetails,
                                    label = "shared_transition"
                                ) { details ->
                                    if (!details) {
                                        PermissionWrapper {
                                            SmallPlayerScreen(
                                                sharedTransitionScope = this@SharedTransitionLayout,
                                                animatedVisibilityScope = this@AnimatedContent,
                                                onExpand = { showDetails = true },
                                            )
                                        }

                                    } else {
                                        PermissionWrapper {
                                            PlayerDashboardScreen(
                                                onShrink = { showDetails = false },
                                                sharedTransitionScope = this@SharedTransitionLayout,
                                                animatedVisibilityScope = this@AnimatedContent
                                            )
                                        }
                                    }
                                }
                            }
                            //  SmallPlayerScreen()
                            // PlayerDashboardScreen()
                        }
                    }
                }
            }
        }
    }
    override fun onStart() {
        super.onStart()
        if (driveSideReceiver == null) {
            driveSideReceiver = DriveSideToggleReceiver()
            val filter = IntentFilter(DriveSideBroadcastContract.ACTION_SET_LHD)
                registerReceiver(
                    this,
                    driveSideReceiver,
                    filter,
                    ContextCompat.RECEIVER_EXPORTED // allow external senders like ADB
                )
        }
    }

    override fun onStop() {
        super.onStop()
        driveSideReceiver?.let {
            unregisterReceiver(it)
            driveSideReceiver = null
        }
    }
}

@Composable
fun PermissionWrapper(onPermissionGranted: @Composable () -> Unit) {
    val context = LocalContext.current
    val permission = if (Build.VERSION.SDK_INT >= 33) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    if (hasPermission) {
        onPermissionGranted()
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "We need permission to access your music.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { launcher.launch(permission) }) {
                    Text("Grant Permission")
                }
            }
        }
    }
}
