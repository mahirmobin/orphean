package com.orphean.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orphean.domain.model.Song
import com.orphean.domain.usecase.GetSongsUseCase
import com.orphean.domain.usecase.SyncMediaUseCase
import com.orphean.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import com.orphean.domain.model.Album
import javax.inject.Inject

enum class SortOrder { NAME, ARTIST, DATE_ADDED, MOST_PLAYED, ALBUMS }

@HiltViewModel
class HomeViewModel @Inject constructor(
    getSongsUseCase: GetSongsUseCase,
    private val syncMediaUseCase: SyncMediaUseCase,
    private val repository: com.orphean.domain.repository.SongRepository,
    val playerController: PlayerController,
    private val settingsRepository: com.orphean.data.local.SettingsRepository
) : ViewModel() {

    private val songs: StateFlow<List<Song>> = getSongsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _sortOrder = MutableStateFlow(SortOrder.NAME)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val baseFilteredSongs = combine(songs, settingsRepository.filterShortAudio) { list, filterShort ->
        if (filterShort) list.filter { it.durationMs >= 30000L } else list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val displaySongs: StateFlow<List<Song>> = combine(baseFilteredSongs, _sortOrder) { list, order ->
        val deduplicated = list.distinctBy { "${it.title.lowercase().trim()}_${it.artistName.lowercase().trim()}" }
        when(order) {
            SortOrder.NAME -> deduplicated.sortedBy { it.title }
            SortOrder.ARTIST -> deduplicated.sortedBy { it.artistName }
            SortOrder.DATE_ADDED -> deduplicated.sortedByDescending { it.dateAdded }
            SortOrder.MOST_PLAYED -> deduplicated.sortedByDescending { it.playCount }
            SortOrder.ALBUMS -> deduplicated
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mostPlayed: StateFlow<List<Song>> = repository.getMostPlayedSongs(10)
        .map { list -> list.distinctBy { "${it.title.lowercase().trim()}_${it.artistName.lowercase().trim()}" } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favorites: StateFlow<List<Song>> = repository.getFavoriteSongs()
        .map { list -> list.distinctBy { "${it.title.lowercase().trim()}_${it.artistName.lowercase().trim()}" } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val displayAlbums: StateFlow<List<Album>> = baseFilteredSongs.map { list ->
        list.groupBy { it.albumName }.map { (albumName, albumSongs) ->
            Album(
                title = albumName.takeIf { it.isNotBlank() } ?: "Unknown Album",
                artistName = albumSongs.firstOrNull()?.artistName ?: "Unknown Artist",
                albumArtUri = albumSongs.firstOrNull { it.data.isNotBlank() }?.data,
                songs = albumSongs.sortedBy { it.trackNumber }
            )
        }.sortedBy { it.title }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedAlbum = MutableStateFlow<Album?>(null)
    val selectedAlbum = _selectedAlbum.asStateFlow()

    private val _isLibraryLoaded = MutableStateFlow(false)
    val isLibraryLoaded = _isLibraryLoaded.asStateFlow()

    fun selectAlbum(album: Album) {
        _selectedAlbum.value = album
    }

    init {
        playerController.initialize()
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    private var hasSynced = false

    fun syncLibrary() {
        if (hasSynced) return
        hasSynced = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                syncMediaUseCase()
                _isLibraryLoaded.value = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun onSongClick(song: Song) {
        val currentSongs = displaySongs.value
        val index = currentSongs.indexOfFirst { it.id == song.id }
        playerController.playSongs(currentSongs, index.coerceAtLeast(0))
    }
}
