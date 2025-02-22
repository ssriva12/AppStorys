package com.appversal.appstorys

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

//const val AUTO_SLIDE_DURATION = 5000L

@Composable
fun DoubleWidgets(
    modifier: Modifier = Modifier,
    items: List<Pair<String, String>>,
    padding: Dp = 16.dp,
    autoSlideDuration: Long = AUTO_SLIDE_DURATION,
    selectedColor: Color = Color.Black,
    unSelectedColor: Color = Color.Gray,
    selectedLength: Dp = 20.dp,
    dotSize: Dp = 8.dp,
    spacingBetweenImagesAndDots: Dp = 12.dp // Space between images and dots
) {
    val pagerState = rememberPagerState { items.size }
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    // Auto-scroll logic
    LaunchedEffect(!isDragged) {
        if (!isDragged) {
            while (true) {
                delay(autoSlideDuration)
                val nextPage = (pagerState.currentPage + 1) % items.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(state = pagerState) { page ->
            val (leftImage, rightImage) = items[page % items.size]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = padding),
                horizontalArrangement = Arrangement.spacedBy(padding)
            ) {
                ImageCard(imageUrl = leftImage, modifier = Modifier.weight(1f))
                ImageCard(imageUrl = rightImage, modifier = Modifier.weight(1f))
            }
        }

        // Space between images and dots indicator
        Spacer(modifier = Modifier.height(spacingBetweenImagesAndDots))

        if (items.size > 1) {
            DotsIndicator(
                modifier = Modifier.padding(horizontal = 8.dp),
                totalDots = items.size,
                selectedIndex = pagerState.currentPage % items.size,
                dotSize = dotSize,
                selectedColor = selectedColor,
                unSelectedColor = unSelectedColor,
                selectedLength = selectedLength
            )
        }
    }
}

@Composable
fun ImageCard(imageUrl: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = modifier
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        )
    }
}
