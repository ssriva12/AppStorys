package com.appversal.appstorys.ui

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.appversal.appstorys.utils.isGifUrl
import kotlinx.coroutines.delay
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size

//const val AUTO_SLIDE_DURATION = 5000L

@Composable
internal fun DoubleWidgets(
    modifier: Modifier = Modifier,
    itemContent: @Composable (index: Int) -> Unit,
    pagerState: PagerState,
    itemsCount: Int,
    autoSlideDuration: Long = AUTO_SLIDE_DURATION,
    selectedColor: Color = Color.Black,
    unSelectedColor: Color = Color.Gray,
    selectedLength: Dp = 20.dp,
    dotSize: Dp = 8.dp,
    spacingBetweenImagesAndDots: Dp = 12.dp,
    width: Dp? = null
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
                modifier = Modifier.padding(horizontal = 8.dp).then(if (width != null) Modifier.width(width) else Modifier.fillMaxWidth()),
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
internal fun ImageCard(modifier: Modifier = Modifier, imageUrl: String, height: Dp?) {

    val context = LocalContext.current
    if (isGifUrl(imageUrl)) {
        val imageLoader = ImageLoader.Builder(context)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()

        val painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context)
                .data(data = imageUrl).memoryCacheKey(imageUrl)
                .diskCacheKey(imageUrl)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .crossfade(true)
                .apply(block = { size(Size.ORIGINAL) }).build(), imageLoader = imageLoader
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            modifier = modifier
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .then(if (height != null) Modifier.height(height) else Modifier)
                    .fillMaxWidth()
            )
        }

    } else {

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            modifier = modifier
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .then(if (height != null) Modifier.height(height) else Modifier)
                    .fillMaxWidth()
            )
        }
    }
}
