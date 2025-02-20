package com.appversal.appstorys

import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest


@Composable
fun PinnedBanner(
    modifier: Modifier = Modifier,
    url: String,
    contentScale: ContentScale = ContentScale.Crop,
    height: Dp = 200.dp,
    placeHolder: Drawable?
){
    val context = LocalContext.current
    val imageRequest = ImageRequest.Builder(context)
        .data(url)
        .memoryCacheKey(url)
        .diskCacheKey(url)
        .placeholder(placeHolder)
        .error(placeHolder)
        .fallback(placeHolder)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .crossfade(true)
        .build()


    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        ),
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
    ) {
        AsyncImage(
            model = imageRequest,
            contentDescription = null,
            contentScale = contentScale,
            modifier = Modifier.height(height)
        )
    }
}