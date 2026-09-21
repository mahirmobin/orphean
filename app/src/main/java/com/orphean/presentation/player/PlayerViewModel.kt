package com.orphean.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orphean.domain.model.Song
import com.orphean.player.PlayerController
import com.orphean.domain.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerController: PlayerController,
    private val repository: SongRepository
) : ViewModel() {
    val isPlaying: StateFlow<Boolean> = playerController.isPlaying
    val isShuffleEnabled: StateFlow<Boolean> = playerController.isShuffleModeEnabled
    val currentPosition: StateFlow<Long> = playerController.currentPosition
    val duration: StateFlow<Long> = playerController.duration
    
    val currentSong: StateFlow<Song?> = combine(
        playerController.playlist,
        playerController.currentSongIndex,
        repository.getAllSongs()
    ) { playlist, index, allSongs ->
        if (index != null && playlist.isNotEmpty() && index in playlist.indices) {
            val baseSong = playlist[index]
            allSongs.find { it.id == baseSong.id } ?: baseSong
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            combine(currentSong, isPlaying) { song, playing -> song to playing }
                .collectLatest { (song, playing) ->
                    if (song != null && playing) {
                        try {
                            delay(30000)
                            repository.incrementPlayCount(song)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
        }
        
        viewModelScope.launch {
            while(true) {
                if (isPlaying.value) {
                    playerController.updateProgress()
                }
                delay(100L)
            }
        }
    }

    fun playPause() = playerController.playPause()
    fun next() = playerController.next()
    fun previous() = playerController.previous()
    fun toggleShuffle() = playerController.toggleShuffle()
    fun seekTo(positionMs: Long) = playerController.seekTo(positionMs)
    fun skipToQueueItem(index: Int) = playerController.skipTo(index)

    val currentPlaylist: StateFlow<List<Song>> = playerController.playlist.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val currentIndex: StateFlow<Int?> = playerController.currentSongIndex.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun toggleFavorite() {
        val song = currentSong.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleFavorite(song)
        }
    }
}
