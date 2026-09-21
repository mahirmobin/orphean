package com.orphean.presentation.settings

import androidx.lifecycle.ViewModel
import com.orphean.data.local.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {
    val crossfadeEnabled: StateFlow<Boolean> = repository.crossfadeEnabled
    val crossfadeDuration: StateFlow<Int> = repository.crossfadeDuration
    val filterShortAudio: StateFlow<Boolean> = repository.filterShortAudio
    val gaplessPlayback: StateFlow<Boolean> = repository.gaplessPlayback

    fun toggleCrossfade(enabled: Boolean) = repository.setCrossfade(enabled)
    fun setCrossfadeDuration(duration: Int) = repository.setCrossfadeDuration(duration)
    fun toggleFilterShort(enabled: Boolean) = repository.setFilterShort(enabled)
    fun toggleGapless(enabled: Boolean) = repository.setGapless(enabled)
}
