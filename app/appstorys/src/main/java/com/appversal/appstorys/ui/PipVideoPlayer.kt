package com.appversal.appstorys.ui

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun Pip() {
    var isFullScreen by remember { mutableStateOf(false) }
    var offset by remember { mutableStateOf(Offset(100f, 100f)) }

    if (isFullScreen) {
        FullScreenContent(onExitFullScreen = { isFullScreen = false })
    } else {
        Box(
            modifier = Modifier
                .offset(offset.x.dp, offset.y.dp)
                .size(150.dp)
                .zIndex(1f)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { },
                        onDrag = { change: PointerInputChange, dragAmount: Offset ->
                            change.consumeAllChanges()
                            offset = Offset(
                                offset.x + dragAmount.x,
                                offset.y + dragAmount.y
                            )
                        }
                    )
                }
                .background(Color.Gray, shape = RoundedCornerShape(12.dp))
                .clickable { isFullScreen = true },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "PiP Content",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun FullScreenContent(onExitFullScreen: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Full Screen Content",
            color = Color.White,
            style = MaterialTheme.typography.headlineLarge
        )

        Button(
            onClick = onExitFullScreen,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text("Exit PiP")
        }
    }
}