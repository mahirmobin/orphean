package com.orphean.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("orphean_settings", Context.MODE_PRIVATE)
    
    val crossfadeEnabled = MutableStateFlow(prefs.getBoolean("crossfade", false))
    val crossfadeDuration = MutableStateFlow(prefs.getInt("crossfade_duration", 4))
    val filterShortAudio = MutableStateFlow(prefs.getBoolean("filter_short", false))
    val gaplessPlayback = MutableStateFlow(prefs.getBoolean("gapless", true))

    fun setCrossfade(enabled: Boolean) {
        prefs.edit().putBoolean("crossfade", enabled).apply()
        crossfadeEnabled.value = enabled
    }
    
    fun setCrossfadeDuration(seconds: Int) {
        prefs.edit().putInt("crossfade_duration", seconds).apply()
        crossfadeDuration.value = seconds
    }

    fun setFilterShort(enabled: Boolean) {
        prefs.edit().putBoolean("filter_short", enabled).apply()
        filterShortAudio.value = enabled
    }
    
    fun setGapless(enabled: Boolean) {
        prefs.edit().putBoolean("gapless", enabled).apply()
        gaplessPlayback.value = enabled
    }
}
