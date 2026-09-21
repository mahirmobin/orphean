package com.orphean.presentation.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.orphean.domain.model.Song
import com.orphean.domain.model.Album
import com.orphean.presentation.theme.glassmorphism
import com.orphean.presentation.utils.AlbumArtImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToPlayer: () -> Unit,
    onNavigateToAlbumDetail: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val displaySongs by viewModel.displaySongs.collectAsState()
    val displayAlbums by viewModel.displayAlbums.collectAsState()
    val mostPlayed by viewModel.mostPlayed.collectAsState()
    val favoritesList by viewModel.favorites.collectAsState()
    
    val sortOrder by viewModel.sortOrder.collectAsState()
    val context = LocalContext.current
    
    var searchQuery by remember { mutableStateOf("") }
    val filteredSongs = remember(searchQuery, displaySongs) {
        if (searchQuery.isBlank()) displaySongs
        else displaySongs.filter { 
            it.title.contains(searchQuery, ignoreCase = true) || 
            it.artistName.contains(searchQuery, ignoreCase = true) 
        }
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) viewModel.syncLibrary()
    }

    LaunchedEffect(Unit) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else Manifest.permission.READ_EXTERNAL_STORAGE
        
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            viewModel.syncLibrary()
        } else {
            permissionLauncher.launch(permission)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0.dp)
    ) { _ ->
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(initialOffsetY = { 100 }, animationSpec = tween(800)) + fadeIn(animationSpec = tween(800))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(end = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Library",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 12.dp)
                    )
                    Row {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Rounded.Settings, contentDescription = "Settings", tint = Color.White)
                        }
                    }
                }

                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search songs, artists...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .glassmorphism(shape = RoundedCornerShape(16.dp), intensity = 1.0f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    SortOrder.values().forEach { order ->
                        item {
                            FilterChip(
                                selected = sortOrder == order,
                                onClick = { viewModel.setSortOrder(order) },
                                label = { Text(order.name.lowercase().replaceFirstChar { it.uppercase() }.replace("_", " ")) },
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                }

                if (filteredSongs.isEmpty()) {
                    val isLibraryLoaded by viewModel.isLibraryLoaded.collectAsState()
                    if (isLibraryLoaded) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No matching songs.", color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f))
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                } else {
                    if (sortOrder != SortOrder.ALBUMS) {
                        val listState = rememberLazyListState()
                        Box(modifier = Modifier.weight(1f)) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                if (mostPlayed.isNotEmpty() && searchQuery.isBlank()) {
                                    item {
                                        Text("Most Played", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                            items(mostPlayed, key = { it.id }) { song ->
                                                BigSongCard(song) {
                                                    viewModel.onSongClick(song)
                                                    onNavigateToPlayer()
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                if (favoritesList.isNotEmpty() && searchQuery.isBlank()) {
                                    item {
                                        Text("Favorites", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                            items(favoritesList, key = { it.id }) { song ->
                                                BigSongCard(song) {
                                                    viewModel.onSongClick(song)
                                                    onNavigateToPlayer()
                                                }
                                            }
                                        }
                                    }
                                }

                                if (searchQuery.isBlank()) {
                                    item {
                                        Text("All Tracks", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                                    }
                                }
                                
                                items(filteredSongs, key = { it.id }) { song ->
                                    SongItem(song = song, onClick = { 
                                        viewModel.onSongClick(song) 
                                        onNavigateToPlayer()
                                    })
                                }
                                
                                item { Spacer(modifier = Modifier.height(100.dp)) }
                            }
                            val scrollRatio by remember {
                                derivedStateOf {
                                    val total = listState.layoutInfo.totalItemsCount
                                    if (total == 0) 0f else listState.firstVisibleItemIndex.toFloat() / total.toFloat()
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .fillMaxHeight()
                                    .padding(vertical = 16.dp, horizontal = 4.dp)
                            ) {
                                val config = LocalConfiguration.current
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight(0.1f)
                                        .width(3.dp)
                                        .align(Alignment.TopCenter)
                                        .offset(y = config.screenHeightDp.dp * scrollRatio * 0.70f)
                                        .background(Color.White.copy(alpha = 0.35f), RoundedCornerShape(2.dp))
                                )
                            }
                        }
                    } else {
                        SkiperAlbumsAccordion(albums = displayAlbums, viewModel = viewModel, onNavigateToAlbumDetail = onNavigateToAlbumDetail)
                    }
                }
            }
        }
    }
}

@Composable
fun SkiperAlbumsAccordion(
    albums: List<Album>,
    viewModel: HomeViewModel,
    onNavigateToAlbumDetail: () -> Unit
) {
    var expandedAlbum by remember { mutableStateOf<Album?>(null) }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(albums, key = { it.title }) { album ->
            val isExpanded = expandedAlbum == album
            val targetHeight by animateDpAsState(
                targetValue = if (isExpanded) 360.dp else 48.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "accordion_height"
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(targetHeight)
                    .clip(RoundedCornerShape(if (isExpanded) 32.dp else 24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f))
                    .clickable { 
                        if (isExpanded) {
                            viewModel.selectAlbum(album)
                            onNavigateToAlbumDetail()
                        } else {
                            expandedAlbum = album 
                        }
                    }
            ) {
                AlbumArtImage(
                    data = album.albumArtUri ?: "",
                    modifier = Modifier.fillMaxSize().graphicsLayer { alpha = if (isExpanded) 0.6f else 0.4f }
                )
                
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
                )
                
                if (isExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(album.title, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 2)
                        Spacer(Modifier.height(8.dp))
                        Text(album.artistName, fontSize = 16.sp, color = Color.White.copy(alpha=0.7f))
                        Spacer(Modifier.height(4.dp))
                        Text("${album.songs.size} tracks", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(album.title.uppercase(), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp, color = Color.White, maxLines = 1)
                    }
                }
            }
        }
        item { Spacer(Modifier.height(100.dp)) }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BigSongCard(song: Song, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .width(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AlbumArtImage(data = song.data, modifier = Modifier.size(124.dp).clip(RoundedCornerShape(8.dp)))
        Spacer(modifier = Modifier.height(12.dp))
        Text(song.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, modifier = Modifier.basicMarquee())
        if (song.artistName.isNotEmpty()) {
            Text(song.artistName, fontSize = 12.sp, maxLines = 1, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.basicMarquee())
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongItem(song: Song, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AlbumArtImage(data = song.data, modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)))
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )
            if (song.artistName.isNotEmpty()) {
                Text(
                    text = song.artistName,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    modifier = Modifier.basicMarquee()
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumCard(album: Album, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .clickable { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AlbumArtImage(data = album.albumArtUri ?: "", modifier = Modifier.aspectRatio(1f).clip(RoundedCornerShape(8.dp)))
        Spacer(modifier = Modifier.height(12.dp))
        Text(album.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, modifier = Modifier.basicMarquee())
        if (album.artistName.isNotEmpty()) {
            Text(album.artistName, fontSize = 12.sp, maxLines = 1, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.basicMarquee())
        }
        Text("${album.songs.size} tracks", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
    }
}
