package com.orphean.presentation.settings

import android.content.Intent
import android.media.audiofx.AudioEffect
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orphean.presentation.theme.glassmorphism
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val crossfade = viewModel.crossfadeEnabled.collectAsState().value
    val crossfadeDur = viewModel.crossfadeDuration.collectAsState().value
    val filterShort = viewModel.filterShortAudio.collectAsState().value
    val gapless = viewModel.gaplessPlayback.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text("Settings", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text("Playback", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 16.dp))
        
        Column(modifier = Modifier.fillMaxWidth().glassmorphism(intensity = 1.0f, shape = RoundedCornerShape(16.dp)).padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("Crossfade Tracks")
                Switch(checked = crossfade, onCheckedChange = { viewModel.toggleCrossfade(it) })
            }
            if (crossfade) {
                Text("Duration: ${crossfadeDur}s", fontSize = 12.sp, color = Color.Gray)
                Slider(
                    value = crossfadeDur.toFloat(),
                    onValueChange = { viewModel.setCrossfadeDuration(it.toInt()) },
                    valueRange = 2f..12f,
                    steps = 9
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("Gapless Playback")
                Switch(checked = gapless, onCheckedChange = { viewModel.toggleGapless(it) })
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        Text("Library", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 16.dp))
        Column(modifier = Modifier.fillMaxWidth().glassmorphism(intensity = 1.0f, shape = RoundedCornerShape(16.dp)).padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("Filter Short Audios (< 30s)")
                Switch(checked = filterShort, onCheckedChange = { viewModel.toggleFilterShort(it) })
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.packageName)
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
        ) {
            Text("Open System Equalizer", color = Color.White)
        }
    }
}
