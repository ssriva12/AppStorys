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
import androidx.compose.ui.draw.alpha
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

@OptIn(UnstableApi::class)
@Composable
internal fun PipVideo(
    height: Dp,
    width: Dp,
    videoUri: String,
    fullScreenVideoUri: String,
    button_text: String,
    position: String?,
    link: String,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    val configuration = LocalConfiguration.current

    // Screen dimensions in pixels
    val screenWidth = configuration.screenWidthDp * density
    val screenHeight = configuration.screenHeightDp * density

    // PiP dimensions
    val pipWidth = width
    val pipHeight = height

    // Boundary padding in dp
    val boundaryPadding = 12.dp
    val boundaryPaddingPx = with(LocalDensity.current) { boundaryPadding.toPx() }

    // Initial position - will be set based on the position parameter
    var pipSize by remember { mutableStateOf(IntSize(0, 0)) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var isInitialized by remember { mutableStateOf(false) }

    var isFullScreen by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(true) }

    // Remember the player for better control
    val pipPlayer = remember {
        ExoPlayer.Builder(context)
            .setSeekBackIncrementMs(5000)
            .setLoadControl(DefaultLoadControl())
            .setSeekForwardIncrementMs(5000)
            .build()
            .apply {
                setMediaItem(MediaItem.fromUri(Uri.parse(videoUri)))
                repeatMode = Player.REPEAT_MODE_ALL
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                prepare()
                play()
            }
    }

    // Handle cleanup
    DisposableEffect(Unit) {
        onDispose {
            pipPlayer.release()
        }
    }

    // Control playback based on fullscreen state
    LaunchedEffect(isFullScreen) {
        if (isFullScreen) {
            pipPlayer.pause()
        } else {
            pipPlayer.play()
        }
    }

    // Update volume based on mute state
    LaunchedEffect(isMuted) {
        pipPlayer.volume = if (isMuted) 0f else 1.0f
    }

    if (isFullScreen) {
        FullScreenVideoDialog(
            videoUri = fullScreenVideoUri,
            onDismiss = {
                isFullScreen = false
                // Resume the PIP player when minimizing
                pipPlayer.play()
            },
            onClose = onClose,
            button_text = button_text,
            link = link
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Only render the Card once we've initialized the position
        if (isInitialized) {
            Card(
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .size(width = pipWidth, height = pipHeight)
                    .onGloballyPositioned { coordinates ->
                        pipSize = coordinates.size
                    }
                    .clickable {
                        isFullScreen = true
                        // Pause PIP player when going fullscreen
                        pipPlayer.pause()
                    }
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
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box {
                    // Use the shared player instance
                    PipPlayerView(
                        exoPlayer = pipPlayer,
                        modifier = Modifier.fillMaxSize()
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
                            .clickable {
                                isFullScreen = true
                                // Pause PIP player when going fullscreen
                                pipPlayer.pause()
                            },
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
        } else {
            // Invisible box to measure size and set initial position
            Box(
                modifier = Modifier
                    .size(width = pipWidth, height = pipHeight)
                    .onGloballyPositioned { coordinates ->
                        pipSize = coordinates.size

                        // Determine position based on the position parameter
                        if (position == "left") {
                            // Bottom left
                            offsetX = boundaryPaddingPx
                        } else {
                            // Bottom right (default, or if position is null, empty, or "right")
                            offsetX = screenWidth - pipSize.width - boundaryPaddingPx
                        }

                        // Y position is always bottom
                        offsetY = screenHeight - pipSize.height - boundaryPaddingPx

                        // Mark as initialized to show the actual PiP
                        isInitialized = true
                    }
                    .alpha(0f) // Make it invisible during measurement
            )
        }
    }
}

// Simplified player view that uses an existing ExoPlayer instance
@OptIn(UnstableApi::class)
@Composable
fun PipPlayerView(
    exoPlayer: ExoPlayer,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false
                resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
                setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
                useArtwork = false
                setKeepContentOnPlayerReset(true)
            }
        },
        modifier = modifier
    )
}

@OptIn(UnstableApi::class)
@Composable
fun FullScreenVideoDialog(
    videoUri: String,
    onDismiss: () -> Unit,
    button_text: String?,
    link: String?,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var isMuted by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    // Create a separate player for fullscreen view
    val fullscreenPlayer = remember {
        ExoPlayer.Builder(context)
            .setSeekBackIncrementMs(5000)
            .setLoadControl(DefaultLoadControl())
            .setSeekForwardIncrementMs(5000)
            .build()
            .apply {
                setMediaItem(MediaItem.fromUri(Uri.parse(videoUri)))
                repeatMode = Player.REPEAT_MODE_ALL
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                volume = if (isMuted) 0f else 1.0f
                prepare()
                play()
            }
    }

    // Update volume based on mute state
    LaunchedEffect(isMuted) {
        fullscreenPlayer.volume = if (isMuted) 0f else 1.0f
    }

    // Clean up when dialog is dismissed
    DisposableEffect(Unit) {
        onDispose {
            fullscreenPlayer.release()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // Ensure full screen
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
                ) {
                // Use PlayerView directly with the fullscreen player
                PipPlayerView(
                    exoPlayer = fullscreenPlayer,
                    modifier = Modifier.fillMaxWidth(),
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
                    onClick = {
                        if (link != null) {
                            uriHandler.openUri(link)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(text = button_text.toString(), color = Color.Black)
                }
            }
        }
    }
}