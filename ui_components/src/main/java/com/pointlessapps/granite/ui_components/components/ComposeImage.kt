package com.pointlessapps.granite.ui_components.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.pointlessapps.granite.ui_components.R

@Composable
fun ComposeImage(
    url: String,
    modifier: Modifier = Modifier,
    imageStyle: ComposeImageStyle = defaultComposeImageStyle(),
) {
    SubcomposeAsyncImage(
        modifier = modifier.fillMaxSize(),
        model = ImageRequest.Builder(LocalContext.current)
            .data(
                if (imageStyle.size is ComposeImageStyle.ComposeImageSize.UrlResized) {
                    imageStyle.size.transform(url)
                } else {
                    url
                }
            )
            .diskCacheKey(imageStyle.cacheKey ?: url)
            .memoryCacheKey(imageStyle.cacheKey ?: url)
            .placeholderMemoryCacheKey(imageStyle.cacheKey ?: url)
            .crossfade(imageStyle.crossfade)
            .let {
                if (imageStyle.size is ComposeImageStyle.ComposeImageSize.Resized) {
                    it.size(imageStyle.size.width, imageStyle.size.height)
                } else {
                    it
                }
            }
            .build(),
        contentScale = imageStyle.contentScale,
        loading = @Composable { PlaceholderContent(imageStyle) },
        contentDescription = null,
    )
}

@Composable
private fun PlaceholderContent(imageStyle: ComposeImageStyle) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.Black
                    .copy(alpha = 0.2f)
                    .compositeOver(MaterialTheme.colorScheme.surface),
            ),
        contentAlignment = Alignment.Center,
    ) {
        ComposeIcon(
            modifier = Modifier.size(dimensionResource(R.dimen.placeholder_icon_size)),
            iconRes = imageStyle.placeholderDrawableRes,
        )
    }
}

@Composable
fun defaultComposeImageStyle() = ComposeImageStyle(
    crossfade = true,
    contentScale = ContentScale.Crop,
    placeholderDrawableRes = R.drawable.ic_placeholder,
    size = ComposeImageStyle.ComposeImageSize.Default,
    cacheKey = null,
)

data class ComposeImageStyle(
    val crossfade: Boolean,
    val contentScale: ContentScale,
    @DrawableRes val placeholderDrawableRes: Int,
    val size: ComposeImageSize,
    val cacheKey: String?,
) {
    sealed interface ComposeImageSize {
        data object Default : ComposeImageSize

        data class Resized(
            val width: Int,
            val height: Int,
        ) : ComposeImageSize

        data class UrlResized(
            private val width: Int,
            private val height: Int?,
            val transform: (String) -> String = { "$it?resize=${width}x${height ?: ""}" },
        ) : ComposeImageSize
    }
}
