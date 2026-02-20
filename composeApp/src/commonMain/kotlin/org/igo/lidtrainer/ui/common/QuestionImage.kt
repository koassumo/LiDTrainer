package org.igo.lidtrainer.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import lidtrainer.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun QuestionImage(
    imageKey: String?,
    imageAttribution: String? = null,
    modifier: Modifier = Modifier
) {
    if (imageKey.isNullOrBlank()) return

    val imageModifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(Dimens.QuestionImageCornerRadius))

    Column(modifier = modifier.fillMaxWidth()) {
        if (imageKey.startsWith("http")) {
            AsyncImage(
                model = imageKey,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = imageModifier
            )
        } else {
            var imageBitmap by remember(imageKey) { mutableStateOf<ImageBitmap?>(null) }

            LaunchedEffect(imageKey) {
                try {
                    val bytes = Res.readBytes("files/images/$imageKey")
                    imageBitmap = loadImageBitmapFromBytes(bytes)
                } catch (_: Exception) {
                    // Image not found or failed to decode — skip silently
                }
            }

            imageBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = imageModifier
                )
            }
        }

        if (!imageAttribution.isNullOrBlank()) {
            Text(
                text = imageAttribution,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Dimens.SpaceSmall)
            )
        }
    }
}

expect fun loadImageBitmapFromBytes(bytes: ByteArray): ImageBitmap
