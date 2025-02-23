package com.appversal.appstorys

import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
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
    itemContent: @Composable (index: Int) -> Unit,
    pagerState: PagerState,
    itemsCount: Int,
    autoSlideDuration: Long = AUTO_SLIDE_DURATION,
    selectedColor: Color = Color.Black,
    unSelectedColor: Color = Color.Gray,
    selectedLength: Dp = 20.dp,
    dotSize: Dp = 8.dp,
    spacingBetweenImagesAndDots: Dp = 12.dp // Space between images and dots
) {
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    // Auto-scroll logic
    LaunchedEffect(!isDragged) {
        if (!isDragged) {
            while (true) {
                delay(autoSlideDuration)
                val nextPage = (pagerState.currentPage + 1) % itemsCount
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(state = pagerState) { page ->
            itemContent(page)

        }

        // Space between images and dots indicator
        Spacer(modifier = Modifier.height(spacingBetweenImagesAndDots))
        if (itemsCount > 1) {
            DotsIndicator(
                modifier = Modifier.padding(horizontal = 8.dp),
                totalDots = itemsCount,
                selectedIndex = pagerState.currentPage % itemsCount,
                dotSize = dotSize,
                selectedColor = selectedColor,
                unSelectedColor = unSelectedColor,
                selectedLength = selectedLength
            )
        }
    }
}

@Composable
fun ImageCard(modifier: Modifier = Modifier, imageUrl: String, height: Dp = 200.dp) {
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
