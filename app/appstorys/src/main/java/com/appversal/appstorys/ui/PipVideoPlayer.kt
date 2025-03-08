package com.appversal.appstorys.ui

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.appversal.appstorys.R
import kotlin.math.roundToInt

@Composable
fun MovablePipVideo(
    videoUri: String,
    fullScreenVideoUri: String,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    val configuration = LocalConfiguration.current

    // Screen dimensions in pixels
    val screenWidth = configuration.screenWidthDp * density
    val screenHeight = configuration.screenHeightDp * density

    // PiP dimensions
    val pipWidth = 120.dp
    val pipHeight = 180.dp

    // Boundary padding in dp
    val boundaryPadding = 12.dp
    val boundaryPaddingPx = with(LocalDensity.current) { boundaryPadding.toPx() }

    // Initial position - bottom right with padding
    var pipSize by remember { mutableStateOf(IntSize(0, 0)) }
    var offsetX by remember { mutableStateOf(0f) } // Will be initialized properly once we know pip size
    var offsetY by remember { mutableStateOf(0f) } // Will be initialized properly once we know pip size
    var isInitialized by remember { mutableStateOf(false) }

    var isFullScreen by remember { mutableStateOf(false) }

    if (isFullScreen) {
        FullScreenVideoDialog(
            videoUri = fullScreenVideoUri,
            onDismiss = { isFullScreen = false },
            onClose = onClose  // Pass the onClose callback to the FullScreenVideoDialog
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .size(width = pipWidth, height = pipHeight)
                .onGloballyPositioned { coordinates ->
                    pipSize = coordinates.size

                    // Set initial position to bottom right once we know the size
                    if (!isInitialized) {
                        offsetX = screenWidth - pipSize.width - boundaryPaddingPx
                        offsetY = screenHeight - pipSize.height - boundaryPaddingPx
                        isInitialized = true
                    }
                }
                .clickable { isFullScreen = true }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX = (offsetX + dragAmount.x).coerceIn(
                            boundaryPaddingPx,
                            screenWidth - pipSize.width - boundaryPaddingPx
                        )
                        offsetY = (offsetY + dragAmount.y).coerceIn(
                            boundaryPaddingPx,
                            screenHeight - pipSize.height - boundaryPaddingPx
                        )
                    }
                },
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box {
                var isMuted by remember { mutableStateOf(false) }

                EnhancedVideoPlayer(
                    videoUri = videoUri,
                    modifier = Modifier.fillMaxSize(),
                    isMuted = isMuted
                )

                // Close Button (Top-Right)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(23.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .clickable { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                }


                // Mute Button (Top-Left)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                        .size(24.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .clickable { isMuted = !isMuted },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = if (isMuted) painterResource(R.drawable.mute) else painterResource(R.drawable.volume),
                        contentDescription = "Mute/Unmute",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }


                // Maximize Button (Bottom-Right)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .size(24.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .clickable { isFullScreen = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.expand),
                        contentDescription = "Maximize",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }

            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun EnhancedVideoPlayer(
    videoUri: String,
    modifier: Modifier = Modifier,
    isMuted: Boolean = false
) {
    val context = LocalContext.current
    val uri = remember(videoUri) { Uri.parse(videoUri) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setSeekBackIncrementMs(5000)
            .setLoadControl(DefaultLoadControl())
            .setSeekForwardIncrementMs(5000)
            .build()
            .apply {
                setMediaItem(MediaItem.fromUri(uri))
                repeatMode = Player.REPEAT_MODE_ALL
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                volume = if (isMuted) 0f else 1.0f
                prepare()
                play()
            }
    }

    DisposableEffect(key1 = uri, key2 = isMuted) {
        exoPlayer.volume = if (isMuted) 0f else 1.0f
        onDispose { exoPlayer.release() }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false
                resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL
                setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
                useArtwork = false
                setKeepContentOnPlayerReset(true)
            }
        },
        modifier = modifier,
        update = { view ->
            if (view.player != exoPlayer) view.player = exoPlayer
            if (!exoPlayer.isPlaying && exoPlayer.playbackState == Player.STATE_READY) {
                exoPlayer.play()
            }
        }
    )
}

@Composable
fun FullScreenVideoDialog(
    videoUri: String,
    onDismiss: () -> Unit,
    onClose: () -> Unit  // Added parameter to close both the fullscreen and PiP
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // Ensure full screen
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                EnhancedVideoPlayer(
                    videoUri = videoUri,
                    modifier = Modifier.fillMaxWidth()
                )

                // Top-left Minimize Button - Only minimizes the fullscreen view
                IconButton(
                    onClick = onDismiss,  // Now properly calls onDismiss to minimize
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.minimize),
                        contentDescription = "Minimize",
                        tint = Color.White,
                        modifier = Modifier.size(23.dp)
                    )
                }

                // Top-right Buttons (Mute & Close)
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute Button
                    var isMuted by remember { mutableStateOf(false) }
                    IconButton(
                        onClick = { isMuted = !isMuted },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            painter = if (isMuted) painterResource(R.drawable.mute) else painterResource(R.drawable.volume),
                            contentDescription = "Mute/Unmute",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    // Close Button - Now closes both fullscreen and PiP
                    IconButton(
                        onClick = onClose,  // Use onClose to close everything
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Bottom-center Click Button
                Button(
                    onClick = {},
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(text = "Click Here", color = Color.Black)
                }
            }
        }
    }
}