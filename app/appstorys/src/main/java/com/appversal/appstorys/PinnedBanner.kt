package com.appversal.appstorys

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment


@Composable
fun PinnedBanner(
    modifier: Modifier = Modifier,
    url: String,
    contentScale: ContentScale = ContentScale.Crop,
    width: Dp? = null,
    height: Dp = 200.dp,
    bottomMargin: Dp = 0.dp,
    exitIcon: Boolean = false,
    exitUnit: () -> Unit,
    shape: RoundedCornerShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
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


    Log.i("MarginBottom", bottomMargin.toString())
    Log.i("ExitIcon", exitIcon.toString())

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        ),
        modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = bottomMargin),
        shape = shape,
    ) {
        Box(modifier = Modifier){
            AsyncImage(
                model = imageRequest,
                contentDescription = null,
                contentScale = contentScale,
                modifier = Modifier
                    .height(height)
                    .then(
                        if (width == null) {
                            Modifier.fillMaxWidth()
                        }else{
                            Modifier.width(width)
                        }
                    )
            )

            if (exitIcon){
                Icon(
                    modifier = Modifier
                        .padding(15.dp)
                        .size(30.dp)
                        .align(Alignment.TopEnd)
                        .clickable {
                            exitUnit()
                        },
                    tint = Color.Black,
                    imageVector = Icons.Filled.Close,
                    contentDescription = ""
                )
            }
        }
    }
}