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
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.painterResource
import com.electricbiro.runningmetronome.R
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.electricbiro.runningmetronome.data.model.AudioUsageType
import com.electricbiro.runningmetronome.data.model.Preset
import com.electricbiro.runningmetronome.service.MetronomeService
import com.electricbiro.runningmetronome.ui.onboarding.OnboardingScreen
import com.electricbiro.runningmetronome.ui.theme.Accent
import com.electricbiro.runningmetronome.ui.theme.AccentRim
import com.electricbiro.runningmetronome.ui.theme.BgBase
import com.electricbiro.runningmetronome.ui.theme.BgCard
import com.electricbiro.runningmetronome.ui.theme.BgElev
import com.electricbiro.runningmetronome.ui.theme.LineBorder
import com.electricbiro.runningmetronome.ui.theme.LineStrong
import com.electricbiro.runningmetronome.ui.theme.OnAccent
import com.electricbiro.runningmetronome.ui.theme.RunningMetronomeTheme
import com.electricbiro.runningmetronome.ui.theme.TextDim
import com.electricbiro.runningmetronome.ui.theme.TextMute
import com.electricbiro.runningmetronome.ui.theme.TextPrimary
import com.electricbiro.runningmetronome.ui.viewmodel.MetronomeViewModel
import com.electricbiro.runningmetronome.ui.viewmodel.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MetronomeViewModel by viewModels()
    private val onboardingViewModel: OnboardingViewModel by viewModels()
    private var metronomeService: MetronomeService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val serviceBinder = binder as MetronomeService.MetronomeBinder
            metronomeService = serviceBinder.getService()
            isBound = true
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

        val serviceIntent = Intent(this, MetronomeService::class.java)
        startService(serviceIntent)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        setContent {
            RunningMetronomeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgBase,
                ) {
                    AppRoot(
                        metronomeViewModel = viewModel,
                        onboardingViewModel = onboardingViewModel,
                    )
                }
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
fun AppRoot(
    metronomeViewModel: MetronomeViewModel,
    onboardingViewModel: OnboardingViewModel,
) {
    val onboardingState by onboardingViewModel.state.collectAsState()

    when {
        onboardingState.isLoading -> Box(Modifier.fillMaxSize().background(BgBase))
        onboardingState.isComplete || onboardingState.step.ordinal == 3 -> {
            // After onboarding is done (or step is APP), show main screen
            // Refresh presets from the now-persisted level
            LaunchedEffect(Unit) { metronomeViewModel.refreshPresets() }
            MainScreen(
                viewModel = metronomeViewModel,
                tourStep = onboardingState.tourStep,
                onTourNext = onboardingViewModel::nextTourStep,
                onChangeLevel = onboardingViewModel::resetOnboarding,
            )
        }
        else -> OnboardingScreen(viewModel = onboardingViewModel)
    }
}

// ── Main screen ───────────────────────────────────────────────────────────────

@Composable
fun MainScreen(
    viewModel: MetronomeViewModel,
    tourStep: Int = -1,
    onTourNext: () -> Unit = {},
    onChangeLevel: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    // Beat pulse for the BPM ring
    var beatPulse by remember { mutableStateOf(false) }
    val ringScale by animateFloatAsState(
        targetValue = if (uiState.isPlaying && beatPulse) 1.04f else 1.0f,
        animationSpec = tween(100, easing = FastOutSlowInEasing),
        label = "ring",
    )

    LaunchedEffect(uiState.isPlaying, uiState.bpm) {
        if (!uiState.isPlaying) return@LaunchedEffect
        val intervalMs = 60_000L / uiState.bpm
        while (true) {
            beatPulse = true
            delay(80)
            beatPulse = false
            delay(intervalMs - 80)
        }
    }

    // Coachmark target bounds (captured from layout, in root px)
    var presetsRect by remember { mutableStateOf(Rect.Zero) }
    var sliderRect by remember { mutableStateOf(Rect.Zero) }
    var playRect by remember { mutableStateOf(Rect.Zero) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgBase)
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp)
                .padding(top = 10.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // ── Header ────────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(Accent),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_metronome_ui),
                            contentDescription = null,
                            tint = OnAccent,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                    Text(
                        text = "RUNTICK",
                        color = TextPrimary,
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 0.4.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .border(1.dp, LineStrong, CircleShape)
                        .clickable(onClick = onChangeLevel),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Change level",
                        tint = TextMute,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // ── BPM display ───────────────────────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(vertical = 8.dp),
            ) {
                // Pulsing ring
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(ringScale)
                        .border(
                            1.dp,
                            AccentRim.copy(alpha = if (uiState.isPlaying) 1f else 0.4f),
                            CircleShape,
                        ),
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${uiState.bpm}",
                        color = TextPrimary,
                        style = MaterialTheme.typography.displayLarge,
                    )
                    val presetLabel = uiState.presets.find { it.id == uiState.activePresetId }?.label ?: "Custom"
                    Text(
                        text = "BPM · $presetLabel",
                        color = TextMute,
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 3.sp),
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── Preset chips grid ─────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coords ->
                        presetsRect = coords.boundsInRoot()
                    },
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                val row1 = uiState.presets.take(3)
                val row2 = uiState.presets.drop(3)
                listOf(row1, row2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        row.forEach { preset ->
                            PresetChip(
                                preset = preset,
                                active = uiState.activePresetId == preset.id,
                                onClick = { viewModel.setPreset(preset) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // ── Tempo slider ──────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coords ->
                        sliderRect = coords.boundsInRoot()
                    },
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "TEMPO",
                        color = TextMute,
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
                    )
                    Text(
                        text = "130–210",
                        color = TextDim,
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 0.sp,
                            fontWeight = FontWeight.Normal,
                        ),
                    )
                }
                Spacer(Modifier.height(4.dp))
                MetronomeSlider(
                    value = uiState.bpm.toFloat(),
                    valueRange = 130f..210f,
                    onValueChange = { viewModel.setBpm(it) },
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Play / Pause ──────────────────────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(70.dp)
                    .onGloballyPositioned { coords ->
                        playRect = coords.boundsInRoot()
                    }
                    .clip(CircleShape)
                    .background(if (uiState.isPlaying) BgElev else Accent)
                    .border(
                        width = if (uiState.isPlaying) 1.dp else 0.dp,
                        color = if (uiState.isPlaying) LineStrong else Color.Transparent,
                        shape = CircleShape,
                    )
                    .clickable { viewModel.togglePlayPause() },
            ) {
                Icon(
                    imageVector = if (uiState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                    tint = if (uiState.isPlaying) TextPrimary else OnAccent,
                    modifier = Modifier.size(28.dp),
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Volume slider ─────────────────────────────────────────────────
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "VOLUME",
                        color = TextMute,
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
                    )
                    Text(
                        text = "${uiState.volume}%",
                        color = TextDim,
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 0.sp,
                            fontWeight = FontWeight.Normal,
                        ),
                    )
                }
                Spacer(Modifier.height(4.dp))
                MetronomeSlider(
                    value = uiState.volume.toFloat(),
                    valueRange = 0f..100f,
                    onValueChange = { viewModel.setVolume(it) },
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Audio mode ────────────────────────────────────────────────────
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "AUDIO MODE",
                    color = TextMute,
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AudioUsageType.entries.forEach { type ->
                        val selected = uiState.audioUsageType == type
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) Accent else BgCard)
                                .border(
                                    1.dp,
                                    if (selected) Accent else LineBorder,
                                    RoundedCornerShape(10.dp),
                                )
                                .clickable { viewModel.setAudioUsageType(type) }
                                .padding(10.dp),
                        ) {
                            Text(
                                text = type.displayName,
                                color = if (selected) OnAccent else TextPrimary,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            )
                            Text(
                                text = type.description,
                                color = if (selected) OnAccent.copy(alpha = 0.7f) else TextMute,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    letterSpacing = 0.sp,
                                    fontWeight = FontWeight.Normal,
                                ),
                            )
                        }
                    }
                }
            }
        }

        // ── Coachmark tour overlay ─────────────────────────────────────────────
        if (tourStep in 0..2) {
            CoachmarkTour(
                step = tourStep,
                presetsRect = presetsRect,
                sliderRect = sliderRect,
                playRect = playRect,
                onNext = onTourNext,
            )
        }
    }
}

// ── Preset chip ───────────────────────────────────────────────────────────────

@Composable
private fun PresetChip(
    preset: Preset,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (active) Accent else Color.Transparent)
            .border(
                1.dp,
                if (active) Accent else LineStrong,
                RoundedCornerShape(10.dp),
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        Text(
            text = preset.label.uppercase(),
            color = if (active) OnAccent.copy(alpha = 0.7f) else TextMute,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.4.sp,
                fontSize = 11.sp,
            ),
            maxLines = 1,
        )
        Text(
            text = "${preset.bpm}",
            color = if (active) OnAccent else TextPrimary,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
    }
}

// ── Slider ────────────────────────────────────────────────────────────────────

@Composable
private fun MetronomeSlider(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        colors = SliderDefaults.colors(
            thumbColor = Accent,
            activeTrackColor = Accent,
            inactiveTrackColor = Color.White.copy(alpha = 0.08f),
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}

// ── Coachmark tour ────────────────────────────────────────────────────────────

private val tourCopy = listOf(
    "Your six personalized presets. Tap to switch tempo.",
    "Fine-tune your cadence here, or pick a preset above.",
    "You're all set. Tap to start the metronome — it plays alongside your music.",
)

@Composable
private fun CoachmarkTour(
    step: Int,
    presetsRect: Rect,
    sliderRect: Rect,
    playRect: Rect,
    onNext: () -> Unit,
) {
    val density = LocalDensity.current
    val targetRect = when (step) {
        0 -> presetsRect
        1 -> sliderRect
        else -> playRect
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidthDp = maxWidth

        // Scrim with transparent punch-out over the highlighted element
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen },
        ) {
            drawRect(color = Color(0x8C080B12))
            if (targetRect != Rect.Zero) {
                val hPad = 10.dp.toPx()
                val vPad = 8.dp.toPx()
                drawRoundRect(
                    color = Color.Transparent,
                    topLeft = Offset(targetRect.left - hPad, targetRect.top - vPad),
                    size = Size(targetRect.width + hPad * 2, targetRect.height + vPad * 2),
                    cornerRadius = CornerRadius(16.dp.toPx()),
                    blendMode = BlendMode.Clear,
                )
            }
        }

        if (targetRect != Rect.Zero) {
            val tooltipWidth = 248.dp
            val targetTopDp = with(density) { targetRect.top.toDp() }
            val targetCenterXDp = with(density) { targetRect.center.x.toDp() }

            // Center tooltip over the target; clamp to screen edges
            val tooltipStartDp = (targetCenterXDp - tooltipWidth / 2)
                .coerceIn(12.dp, screenWidthDp - tooltipWidth - 12.dp)

            // Place tooltip above target; fall back to below if near the top
            val spaceAbove = targetTopDp - 12.dp  // available above punch-out
            val tooltipEstimatedHeight = 120.dp
            val arrowSize = 10.dp
            val aboveTop = targetTopDp - tooltipEstimatedHeight - arrowSize - 16.dp
            val belowTop = with(density) { (targetRect.bottom).toDp() } + 16.dp + arrowSize
            val placeAbove = spaceAbove >= tooltipEstimatedHeight + arrowSize + 16.dp
            val tooltipTopDp = if (placeAbove) aboveTop.coerceAtLeast(60.dp) else belowTop

            Column(
                modifier = Modifier
                    .padding(start = tooltipStartDp, top = tooltipTopDp)
                    .width(tooltipWidth),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Arrow pointing toward the highlighted element
                if (!placeAbove) {
                    Box(
                        modifier = Modifier
                            .size(arrowSize)
                            .graphicsLayer { rotationZ = 45f }
                            .background(Accent)
                    )
                    Spacer(Modifier.height(2.dp))
                }

                // Tooltip card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Accent)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                ) {
                    Text(
                        text = tourCopy[step],
                        color = OnAccent,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(3) { i ->
                                Box(
                                    modifier = Modifier
                                        .width(if (i == step) 16.dp else 6.dp)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (i == step) OnAccent else OnAccent.copy(alpha = 0.3f)),
                                )
                            }
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(OnAccent)
                                .clickable(onClick = onNext)
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = if (step == 2) "START RUNNING" else "NEXT",
                                color = Accent,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    letterSpacing = 0.5.sp,
                                ),
                            )
                        }
                    }
                }

                if (placeAbove) {
                    Spacer(Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .size(arrowSize)
                            .graphicsLayer { rotationZ = 45f }
                            .background(Accent)
                    )
                }
            }
        }
    }
}
