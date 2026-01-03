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
import androidx.hilt.navigation.compose.hiltViewModel
import com.electricbiro.runningmetronome.data.model.AccentPattern
import com.electricbiro.runningmetronome.data.model.AudioUsageType
import com.electricbiro.runningmetronome.data.model.DrumPattern
import com.electricbiro.runningmetronome.data.model.MetronomeSoundEnum
import com.electricbiro.runningmetronome.data.model.PatternStep
import com.electricbiro.runningmetronome.data.model.PlaybackMode
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

            // Mode Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.playbackMode == PlaybackMode.SIMPLE,
                    onClick = { viewModel.setPlaybackMode(PlaybackMode.SIMPLE) },
                    label = { Text("Simple") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = uiState.playbackMode == PlaybackMode.PATTERN,
                    onClick = { viewModel.setPlaybackMode(PlaybackMode.PATTERN) },
                    label = { Text("Pattern") },
                    modifier = Modifier.weight(1f)
                )
            }

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

            // Mode-specific content
            when (uiState.playbackMode) {
                PlaybackMode.SIMPLE -> {
                    // Sound Selector (Simple Mode)
                    Text(
                        text = "Sound",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SoundSelector(
                        selectedSound = uiState.sound,
                        onSoundSelected = { viewModel.setSound(it) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Accent Pattern Selector
                    Text(
                        text = "Accent Pattern",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AccentPatternSelector(
                        selectedPattern = uiState.accentPattern,
                        onPatternSelected = { viewModel.setAccentPattern(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                PlaybackMode.PATTERN -> {
                    // Pattern Editor
                    PatternEditor(
                        pattern = uiState.drumPattern,
                        onPatternChanged = { viewModel.setDrumPattern(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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

@Composable
fun AccentPatternSelector(
    selectedPattern: AccentPattern,
    onPatternSelected: (AccentPattern) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AccentPattern.entries.forEach { pattern ->
            FilterChip(
                selected = selectedPattern == pattern,
                onClick = { onPatternSelected(pattern) },
                label = { Text(pattern.displayName) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun PatternEditor(
    pattern: DrumPattern,
    onPatternChanged: (DrumPattern) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Sound selection for the pattern
        Text(
            text = "Pattern Sounds",
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Sound 1", style = MaterialTheme.typography.labelSmall)
                SoundSelector(
                    selectedSound = pattern.sound1,
                    onSoundSelected = { onPatternChanged(pattern.copy(sound1 = it)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Sound 2", style = MaterialTheme.typography.labelSmall)
                SoundSelector(
                    selectedSound = pattern.sound2,
                    onSoundSelected = { onPatternChanged(pattern.copy(sound2 = it)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 8-step sequencer
        Text(
            text = "Pattern Steps (8-step)",
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Two rows of 4 steps
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (i in 0..3) {
                    PatternStepButton(
                        stepIndex = i,
                        step = pattern.steps[i],
                        sound1 = pattern.sound1,
                        sound2 = pattern.sound2,
                        onStepChanged = { newStep ->
                            val newSteps = pattern.steps.toMutableList()
                            newSteps[i] = newStep
                            onPatternChanged(pattern.copy(steps = newSteps))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (i in 4..7) {
                    PatternStepButton(
                        stepIndex = i,
                        step = pattern.steps[i],
                        sound1 = pattern.sound1,
                        sound2 = pattern.sound2,
                        onStepChanged = { newStep ->
                            val newSteps = pattern.steps.toMutableList()
                            newSteps[i] = newStep
                            onPatternChanged(pattern.copy(steps = newSteps))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun PatternStepButton(
    stepIndex: Int,
    step: PatternStep,
    sound1: MetronomeSoundEnum,
    sound2: MetronomeSoundEnum,
    onStepChanged: (PatternStep) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentState = when (step.sound) {
        null -> 0  // Rest
        sound1 -> 1  // Sound 1
        sound2 -> 2  // Sound 2
        else -> 0
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${stepIndex + 1}",
            style = MaterialTheme.typography.labelSmall
        )
        OutlinedButton(
            onClick = {
                // Cycle through: Rest -> Sound1 -> Sound2 -> Rest
                val newStep = when (currentState) {
                    0 -> PatternStep(sound1, 1.0f)
                    1 -> PatternStep(sound2, 1.0f)
                    else -> PatternStep(null, 1.0f)
                }
                onStepChanged(newStep)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = when (currentState) {
                    0 -> MaterialTheme.colorScheme.surface
                    1 -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.secondaryContainer
                }
            )
        ) {
            Text(
                text = when (currentState) {
                    0 -> "–"
                    1 -> "1"
                    else -> "2"
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
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