package com.orphean.presentation.player

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import kotlin.math.atan2
import kotlin.math.absoluteValue
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import com.orphean.domain.model.Song
import com.orphean.presentation.theme.glassmorphism
import com.orphean.presentation.utils.AlbumArtImage

private class ArtworkState(val song: Song?) {
    override fun equals(other: Any?): Boolean {
        if (other !is ArtworkState) return false
        return this.song?.id == other.song?.id
    }
    override fun hashCode(): Int = song?.id?.hashCode() ?: 0
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NowPlayingScreen(
    viewModel: PlayerViewModel,
    onBack: () -> Unit
) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isShuffleEnabled by viewModel.isShuffleEnabled.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    
    val currentPlaylist by viewModel.currentPlaylist.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()

    var isFavorite by remember(currentSong) { mutableStateOf(currentSong?.isFavorite == true) }
    var dragProgress by remember { mutableStateOf<Float?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = ArtworkState(currentSong),
            transitionSpec = { fadeIn(tween(800)) togetherWith fadeOut(tween(800)) },
            label = "background_crossfade"
        ) { targetArtwork ->
            val song = targetArtwork.song
            if (song != null) {
                Box(modifier = Modifier.fillMaxSize().graphicsLayer { alpha = 1f }) {
                    AlbumArtImage(data = song.data, modifier = Modifier.fillMaxSize())
                    Box(modifier = Modifier.fillMaxSize().glassmorphism(intensity = 3.0f, shape = RoundedCornerShape(0.dp)))
                    
                    Box(
                        modifier = Modifier.fillMaxSize().background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                                startY = 400f
                            )
                        )
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Rounded.KeyboardArrowDown, contentDescription = "Back", modifier = Modifier.size(36.dp), tint = Color.White)
                }
                
                IconButton(onClick = { viewModel.toggleFavorite() }) {
                    Icon(
                        imageVector = if (currentSong?.isFavorite == true) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = "Favorite",
                        modifier = Modifier.size(32.dp),
                        tint = if (currentSong?.isFavorite == true) Color(0xFFFF4B4B) else Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            val safeInitialPage = (currentIndex ?: 0).coerceIn(0, (currentPlaylist.size - 1).coerceAtLeast(0))
            val pagerState = rememberPagerState(initialPage = safeInitialPage) { currentPlaylist.size }
            
            LaunchedEffect(currentIndex, currentPlaylist.size) {
                val idx = currentIndex ?: return@LaunchedEffect
                if (currentPlaylist.isNotEmpty() && idx in 0 until currentPlaylist.size) {
                    if (pagerState.currentPage != idx) {
                        pagerState.animateScrollToPage(idx)
                    }
                }
            }
            
            LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
                if (!pagerState.isScrollInProgress && (currentIndex ?: 0) != pagerState.currentPage) {
                    val page = pagerState.currentPage
                    if (pagerState.pageCount > 0 && page in 0 until pagerState.pageCount) {
                        viewModel.skipToQueueItem(page)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(16.dp)
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            val down = awaitFirstDown()
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val dist = (down.position - center).getDistance()
                            
                            if (dist > (size.width / 2f) - 130f) {
                                var pointer = down
                                var degrees = Math.toDegrees(atan2(pointer.position.y - center.y, pointer.position.x - center.x).toDouble()).toFloat() + 90f
                                if (degrees < 0) degrees += 360f
                                dragProgress = (degrees / 360f).coerceIn(0f, 1f)
                                
                                while (true) {
                                    val event = awaitPointerEvent()
                                    pointer = event.changes.firstOrNull { it.id == pointer.id } ?: break
                                    if (pointer.pressed) {
                                        pointer.consume()
                                        degrees = Math.toDegrees(atan2(pointer.position.y - center.y, pointer.position.x - center.x).toDouble()).toFloat() + 90f
                                        if (degrees < 0) degrees += 360f
                                        dragProgress = (degrees / 360f).coerceIn(0f, 1f)
                                    } else {
                                        break
                                    }
                                }
                                dragProgress?.let {
                                    viewModel.seekTo((it * duration).toLong())
                                }
                                dragProgress = null
                            }
                        }
                    }
                    .drawWithContent {
                        drawContent()
                        val progress = dragProgress ?: if (duration > 0) (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
                        val cornerRadius = 32.dp.toPx()
                        val strokeWidth = 3.dp.toPx()
                        
                        val path = Path().apply {
                            addRoundRect(
                                androidx.compose.ui.geometry.RoundRect(
                                    rect = androidx.compose.ui.geometry.Rect(0f, 0f, size.width, size.height),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
                                )
                            )
                        }
                        
                        drawPath(
                            path = path,
                            color = Color.White.copy(alpha = 0.15f),
                            style = Stroke(width = strokeWidth)
                        )
                        
                        val pathMeasure = PathMeasure().apply {
                            setPath(path, forceClosed = false)
                        }
                        val length = pathMeasure.length
                        val activePath = Path()
                        pathMeasure.getSegment(0f, length * progress, activePath, true)
                        
                        drawPath(
                            path = activePath,
                            color = Color.White,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        
                        val point = pathMeasure.getPosition(length * progress)
                        if (point != Offset.Unspecified) {
                            drawCircle(color = Color.White, radius = 6.dp.toPx(), center = point)
                            drawCircle(color = Color.White.copy(alpha = 0.4f), radius = 12.dp.toPx(), center = point)
                        }
                    }
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    val song = currentPlaylist.getOrNull(page)
                    
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                rotationY = pageOffset * 22f
                                scaleX = 1f - (pageOffset.absoluteValue * 0.18f)
                                scaleY = 1f - (pageOffset.absoluteValue * 0.18f)
                                alpha = 1f - (pageOffset.absoluteValue * 0.4f)
                                cameraDistance = 16 * density
                                shape = RoundedCornerShape(32.dp)
                                clip = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (song != null) {
                            AlbumArtImage(data = song.data, modifier = Modifier.fillMaxSize())
                        } else {
                            Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray))
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Crossfade(
                targetState = currentSong,
                animationSpec = tween(700),
                label = "text_crossfade"
            ) { targetSong ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.graphicsLayer { alpha = 1f }) {
                    Text(
                        text = targetSong?.title ?: "No Song",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().basicMarquee()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = targetSong?.artistName?.takeIf { it.isNotEmpty() } ?: "Unknown Artist",
                        fontSize = 18.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().basicMarquee()
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.toggleShuffle() }, modifier = Modifier.size(48.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Shuffle, 
                        contentDescription = "Shuffle", 
                        modifier = Modifier.size(28.dp), 
                        tint = if (isShuffleEnabled) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.5f)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))

                IconButton(onClick = { viewModel.previous() }, modifier = Modifier.size(56.dp)) {
                    Icon(imageVector = Icons.Rounded.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(40.dp), tint = Color.White)
                }
                
                Spacer(modifier = Modifier.width(24.dp))
                
                IconButton(
                    onClick = { viewModel.playPause() },
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.White, RoundedCornerShape(40.dp))
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = "Play/Pause",
                        modifier = Modifier.size(48.dp),
                        tint = Color.Black
                    )
                }
                
                Spacer(modifier = Modifier.width(24.dp))
                
                IconButton(onClick = { viewModel.next() }, modifier = Modifier.size(56.dp)) {
                    Icon(imageVector = Icons.Rounded.SkipNext, contentDescription = "Next", modifier = Modifier.size(40.dp), tint = Color.White)
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                Box(modifier = Modifier.size(48.dp))
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
