package com.electricbiro.runningmetronome

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.electricbiro.runningmetronome.data.model.AudioUsageType
import com.electricbiro.runningmetronome.data.model.MetronomeSoundEnum
import com.electricbiro.runningmetronome.service.MetronomeService
import com.electricbiro.runningmetronome.ui.theme.RunningMetronomeTheme
import com.electricbiro.runningmetronome.ui.viewmodel.MetronomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MetronomeViewModel by viewModels()
    private var metronomeService: MetronomeService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val serviceBinder = binder as MetronomeService.MetronomeBinder
            metronomeService = serviceBinder.getService()
            isBound = true
            // Bind the service to the ViewModel
            metronomeService?.let { viewModel.bindService(it) }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            metronomeService = null
            viewModel.unbindService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Start and bind to the metronome service
        val serviceIntent = Intent(this, MetronomeService::class.java)
        startService(serviceIntent)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        setContent {
            RunningMetronomeTheme {
                MetronomeScreen(viewModel = viewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }
}

@Composable
fun MetronomeScreen(
    viewModel: MetronomeViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            // App Title
            Text(
                text = "Running Metronome",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // BPM Display
            Text(
                text = "${uiState.bpm}",
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "BPM",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // BPM Slider
            Text(
                text = "Tempo",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.align(Alignment.Start)
            )
            Slider(
                value = uiState.bpm.toFloat(),
                onValueChange = { viewModel.setBpm(it) },
                valueRange = 40f..200f,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("40", style = MaterialTheme.typography.bodySmall)
                Text("200", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Play/Pause Button
            FloatingActionButton(
                onClick = { viewModel.togglePlayPause() },
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = if (uiState.isPlaying) {
                        Icons.Filled.Pause
                    } else {
                        Icons.Filled.PlayArrow
                    },
                    contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (uiState.isPlaying) "Playing" else "Paused",
                style = MaterialTheme.typography.bodyLarge,
                color = if (uiState.isPlaying) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Preset BPM Chips
            Text(
                text = "Quick Presets",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            BpmPresetChips(
                currentBpm = uiState.bpm,
                onBpmSelected = { viewModel.setBpm(it.toFloat()) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Volume Slider
            Text(
                text = "Volume: ${uiState.volume}%",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.align(Alignment.Start)
            )
            Slider(
                value = uiState.volume.toFloat(),
                onValueChange = { viewModel.setVolume(it) },
                valueRange = 0f..100f,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Audio Usage Type Selector
            Text(
                text = "Audio Mode",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            AudioUsageTypeSelector(
                selectedType = uiState.audioUsageType,
                onTypeSelected = { viewModel.setAudioUsageType(it) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))


        }
    }
}

@Composable
fun SoundSelector(
    selectedSound: MetronomeSoundEnum,
    onSoundSelected: (MetronomeSoundEnum) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MetronomeSoundEnum.entries.forEach { sound ->
            FilterChip(
                selected = selectedSound == sound,
                onClick = { onSoundSelected(sound) },
                label = {
                    Text(
                        text = sound.name.lowercase().replaceFirstChar { it.uppercase() }
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AudioUsageTypeSelector(
    selectedType: AudioUsageType,
    onTypeSelected: (AudioUsageType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        AudioUsageType.entries.forEach { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = {
                    Column {
                        Text(
                            text = type.displayName,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = type.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if (type != AudioUsageType.entries.last()) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun BpmPresetChips(
    currentBpm: Int,
    onBpmSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val presets = listOf(160, 170, 175, 180, 185)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        presets.forEach { bpm ->
            FilterChip(
                selected = currentBpm == bpm,
                onClick = { onBpmSelected(bpm) },
                label = {
                    Text(text = "$bpm")
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MetronomeScreenPreview() {
    RunningMetronomeTheme {
        // Note: Preview won't work with hiltViewModel,
        // but shows the UI layout
    }
}