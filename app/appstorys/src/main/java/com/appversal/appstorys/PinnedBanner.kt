package com.appversal.appstorys

import android.graphics.drawable.Drawable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.appversal.appstorys.campaign.CampaignService
import com.appversal.appstorys.types.ActionType
import com.appversal.appstorys.types.Campaign

@Composable
fun PinnedBanner(
    modifier: Modifier = Modifier,
    campaignService: CampaignService,
    contentScale: ContentScale = ContentScale.Crop,
    height: Dp = 200.dp,
    placeHolder: Drawable?
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val campaignState by campaignService.state.collectAsState()
    
    var bannerVisible by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf(false) }
    
    // Find banner campaign from state
    val bannerCampaign = campaignState.data.campaigns.firstOrNull { campaign ->
        campaign is Campaign.Banner
    } as? Campaign.Banner

    if (!bannerVisible || bannerCampaign == null) return

    // Track impression when banner is first displayed
    LaunchedEffect(bannerCampaign) {
        campaignService.trackUserAction(
            userId = campaignState.data.userId,
            campaignId = bannerCampaign.id,
            action = ActionType.VIEW
        )
    }

    val imageRequest = ImageRequest.Builder(context)
        .data(bannerCampaign.details.image)
        .memoryCacheKey(bannerCampaign.details.image)
        .diskCacheKey(bannerCampaign.details.image)
        .placeholder(placeHolder)
        .error(placeHolder)
        .fallback(placeHolder)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .crossfade(true)
        .listener(
            onStart = { isLoading = true },
            onSuccess = { _, _ -> 
                isLoading = false
                loadError = false
            },
            onError = { _, _ -> 
                isLoading = false
                loadError = true
            }
        )
        .build()

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        ),
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Box {
            // Close button
            IconButton(
                onClick = { bannerVisible = false },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Banner",
                    tint = Color.White
                )
            }

            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                loadError -> {
                    Text(
                        text = "Failed to load banner image",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Red
                    )
                }
                else -> {
                    AsyncImage(
                        model = imageRequest,
                        contentDescription = "Banner Campaign",
                        contentScale = contentScale,
                        modifier = Modifier
                            .height(height)
                            .clickable {
                                bannerCampaign.details.link?.let { link ->
                                    uriHandler.openUri(link)
                                    // Track click
                                    campaignService.trackUserAction(
                                        userId = campaignState.data.userId,
                                        campaignId = bannerCampaign.id,
                                        action = ActionType.CLICK
                                    )
                                }
                            }
                    )
                }
            }
        }
    }
}