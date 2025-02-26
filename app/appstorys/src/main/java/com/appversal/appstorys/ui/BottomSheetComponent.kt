package com.appversal.appstorys.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetComponent(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image
            AsyncImage(
                model = "https://gratisography.com/wp-content/uploads/2024/11/gratisography-augmented-reality-800x525.jpg",
                contentDescription = "Thank you",
                modifier = Modifier
                    .fillMaxWidth() // Make it take full width
                    .height(200.dp), // Adjust height as needed
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Text
            Text(
                text = "Track your bills",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Track your bills",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp), // Space from left and right
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // First Button: Solid Green
                Button(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDismiss()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp), // Standard height
                    shape = RoundedCornerShape(12.dp), // Rounded corners
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF01C198), // Green background
                        contentColor = Color.White // White text
                    )
                ) {
                    Text(text = "Exit", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp)) // Space between buttons

                // Second Button: Outlined (White Background, Green Border)
                OutlinedButton(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDismiss()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp), // Standard height
                    shape = RoundedCornerShape(12.dp), // Rounded corners
                    border = BorderStroke(1.dp, Color(0xFF01C198)), // Green border
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White, // White background
                        contentColor = Color(0xFF01C198) // Green text
                    )
                ) {
                    Text(text = "Close", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }





            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
