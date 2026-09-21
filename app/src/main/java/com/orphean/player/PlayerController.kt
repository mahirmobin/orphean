package com.orphean.player

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.orphean.domain.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: com.orphean.data.local.SettingsRepository
) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    private var pendingPlaySongs: List<Song>? = null
    private var pendingStartIndex: Int? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentSongIndex = MutableStateFlow<Int?>(null)
    val currentSongIndex: StateFlow<Int?> = _currentSongIndex.asStateFlow()

    private val _playlist = MutableStateFlow<List<Song>>(emptyList())
    val playlist: StateFlow<List<Song>> = _playlist.asStateFlow()

    private val _isShuffleModeEnabled = MutableStateFlow(false)
    val isShuffleModeEnabled: StateFlow<Boolean> = _isShuffleModeEnabled.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()
    fun initialize() {
        if (controllerFuture != null) return
        val sessionToken = SessionToken(context, ComponentName(context, OrpheanMediaService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            controller = controllerFuture?.get()
            setupControllerListener()
            
            pendingPlaySongs?.let { songs ->
                pendingStartIndex?.let { index ->
                    playSongs(songs, index)
                }
            }
            pendingPlaySongs = null
            pendingStartIndex = null
        }, ContextCompat.getMainExecutor(context))
    }

    private fun setupControllerListener() {
        controller?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                _currentSongIndex.value = controller?.currentMediaItemIndex
            }
            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _isShuffleModeEnabled.value = shuffleModeEnabled
            }
        })
    }

    fun playSongs(songs: List<Song>, startIndex: Int = 0) {
        _playlist.value = songs
        if (controller == null) {
            pendingPlaySongs = songs
            pendingStartIndex = startIndex
            return
        }
        val mediaItems = songs.map { 
            MediaItem.Builder()
                .setMediaId(it.id.toString())
                .setUri(it.data)
                .build() 
        }
        val safeIndex = startIndex.coerceIn(0, (mediaItems.size - 1).coerceAtLeast(0))
        controller?.setMediaItems(mediaItems, safeIndex, 0L)
        controller?.prepare()
        controller?.play()
    }

    fun playPause() {
        if (controller?.isPlaying == true) {
            controller?.pause()
        } else {
            controller?.play()
        }
    }
    
    fun next() = controller?.seekToNextMediaItem()
    fun previous() = controller?.seekToPreviousMediaItem()

    fun toggleShuffle() {
        val current = controller?.shuffleModeEnabled ?: false
        controller?.shuffleModeEnabled = !current
    }

    fun updateProgress() {
        controller?.let { c ->
            val pos = c.currentPosition
            val dur = c.duration.coerceAtLeast(0L)
            _currentPosition.value = pos
            _duration.value = dur
            
            if (settingsRepository.crossfadeEnabled.value) {
                val fadeDurationMs = settingsRepository.crossfadeDuration.value * 1000L
                if (dur > fadeDurationMs && (dur - pos) <= fadeDurationMs) {
                    val remaining = (dur - pos).toFloat()
                    val fraction = (remaining / fadeDurationMs).coerceIn(0f, 1f)
                    c.volume = fraction
                } else if (pos < fadeDurationMs && dur > fadeDurationMs) {
                    val fraction = (pos.toFloat() / fadeDurationMs).coerceIn(0f, 1f)
                    c.volume = fraction
                } else {
                    if (c.volume != 1f) c.volume = 1f
                }
            } else {
                if (c.volume != 1f) c.volume = 1f
            }
        }
    }

    fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
    }

    fun skipTo(index: Int) {
        controller?.seekToDefaultPosition(index)
    }

    fun release() {
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controller = null
        controllerFuture = null
    }
}
