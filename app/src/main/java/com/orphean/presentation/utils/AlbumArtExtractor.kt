package com.orphean.presentation.utils

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AlbumArtExtractor {
    private val cache = LruCache<String, ImageBitmap>(50)

    suspend fun getArt(path: String): ImageBitmap? = withContext(Dispatchers.IO) {
        if (path.isEmpty()) return@withContext null
        cache.get(path)?.let { return@withContext it }
        
        try {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(path)
            val bytes = mmr.embeddedPicture
            var bitmap: ImageBitmap? = null
            if (bytes != null) {
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
                cache.put(path, bitmap)
            }
            mmr.release()
            bitmap
        } catch (e: Exception) {
            null
        }
    }
}

@Composable
fun AlbumArtImage(data: String, modifier: Modifier = Modifier) {
    var bitmap by remember(data) { mutableStateOf<ImageBitmap?>(null) }
    
    LaunchedEffect(data) {
        bitmap = AlbumArtExtractor.getArt(data)
    }
    
    if (bitmap != null) {
        Image(
            bitmap = bitmap!!, 
            contentDescription = null, 
            modifier = modifier.clip(RoundedCornerShape(12.dp)), 
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant), 
            contentAlignment = Alignment.Center
        ) { 
            Text("🎵", fontSize = 24.sp) 
        }
    }
}
