package com.electricbiro.runningmetronome.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.electricbiro.runningmetronome.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.electricbiro.runningmetronome.data.model.RunningLevel
import com.electricbiro.runningmetronome.ui.theme.Accent
import com.electricbiro.runningmetronome.ui.theme.AccentRim
import com.electricbiro.runningmetronome.ui.theme.AccentSoft
import com.electricbiro.runningmetronome.ui.theme.BgBase
import com.electricbiro.runningmetronome.ui.theme.BgCard
import com.electricbiro.runningmetronome.ui.theme.BgElev
import com.electricbiro.runningmetronome.ui.theme.LineBorder
import com.electricbiro.runningmetronome.ui.theme.LineStrong
import com.electricbiro.runningmetronome.ui.theme.OnAccent
import com.electricbiro.runningmetronome.ui.theme.TextDim
import com.electricbiro.runningmetronome.ui.theme.TextMute
import com.electricbiro.runningmetronome.ui.theme.TextPrimary
import com.electricbiro.runningmetronome.ui.viewmodel.OnboardingStep
import com.electricbiro.runningmetronome.ui.viewmodel.OnboardingViewModel

private val SlideEasing = CubicBezierEasing(0.22f, 0.61f, 0.36f, 1f)

@Composable
fun OnboardingScreen(viewModel: OnboardingViewModel) {
    val state by viewModel.state.collectAsState()

    AnimatedContent(
        targetState = state.step,
        transitionSpec = {
            val forward = targetState.ordinal > initialState.ordinal
            (slideInHorizontally(tween(420, easing = SlideEasing)) { if (forward) it else -it } + fadeIn(tween(200))) togetherWith
                (slideOutHorizontally(tween(420, easing = SlideEasing)) { if (forward) -it else it } + fadeOut(tween(200)))
        },
        label = "onboarding",
    ) { step ->
        when (step) {
            OnboardingStep.WELCOME -> WelcomeScreen(
                onNext = viewModel::goToLevelSelect,
                onSkip = viewModel::skip,
            )
            OnboardingStep.LEVEL_SELECT -> LevelSelectScreen(
                selectedLevel = state.selectedLevel,
                onSelect = viewModel::selectLevel,
                onBack = viewModel::goBack,
                onNext = viewModel::goToPermission,
            )
            OnboardingStep.PERMISSION -> PermissionScreen(
                onBack = viewModel::goBack,
                onFinish = viewModel::finishPermissionStep,
            )
            OnboardingStep.APP -> Unit  // handled by caller once tourStep is set
        }
    }
}

// ── Screen 1: Welcome ─────────────────────────────────────────────────────────

@Composable
fun WelcomeScreen(onNext: () -> Unit, onSkip: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBase)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
    ) {
        // Radial accent glow at top-centre
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x1FFF5A1F), Color.Transparent),
                        center = Offset(Float.MAX_VALUE / 2f, 0f),
                        radius = 1200f,
                    )
                )
        )

        // Skip button — top right
        TextButton(
            onClick = onSkip,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp),
        ) {
            Text(
                text = "Skip",
                color = TextMute,
                style = MaterialTheme.typography.labelMedium,
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Hero — centred in available space
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                PulsingRings()
            }

            // Eyebrow
            Text(
                text = "RUNNING METRONOME",
                color = Accent,
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.5.sp),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(14.dp))

            // Headline
            Text(
                text = "Run to\nthe beat.",
                color = TextPrimary,
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(14.dp))

            // Subhead
            Text(
                text = "Lock in your cadence with a clean, distraction-free beat that runs alongside your music.",
                color = TextMute,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(28.dp))

            // CTA
            CtaButton(
                label = "LET'S GO",
                onClick = onNext,
                trailingIcon = Icons.Filled.ChevronRight,
            )

            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun PulsingRings() {
    val infiniteTransition = rememberInfiniteTransition(label = "rings")
    val sizes = listOf(
        2.dp to 0.7f,
        1.5.dp to 0.5f,
        1.dp to 0.3f,
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(220.dp),
    ) {
        sizes.forEachIndexed { i, (strokeWidth, maxAlpha) ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1600, delayMillis = i * 400, easing = CubicBezierEasing(0f, 0f, 0.2f, 1f)),
                    repeatMode = RepeatMode.Restart,
                ),
                label = "scale$i",
            )
            val alpha by infiniteTransition.animateFloat(
                initialValue = maxAlpha,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1600, delayMillis = i * 400, easing = CubicBezierEasing(0f, 0f, 0.2f, 1f)),
                    repeatMode = RepeatMode.Restart,
                ),
                label = "alpha$i",
            )
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .scale(scale)
                    .border(strokeWidth, Accent.copy(alpha = alpha), CircleShape),
            )
        }

        // Central filled circle with metronome icon
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(84.dp)
                .clip(CircleShape)
                .background(Accent),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_metronome_ui),
                contentDescription = null,
                tint = OnAccent,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

// ── Screen 2: Level Select ────────────────────────────────────────────────────

@Composable
fun LevelSelectScreen(
    selectedLevel: RunningLevel?,
    onSelect: (RunningLevel) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBase)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 22.dp)
            .padding(top = 14.dp, bottom = 16.dp),
    ) {
        // Top bar
        TopBar(onBack = onBack, step = 0, totalSteps = 2)

        Spacer(Modifier.height(18.dp))

        // Title
        Text(
            text = "What's your\nlevel?",
            color = TextPrimary,
            style = MaterialTheme.typography.displaySmall,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "We'll dial in your starting cadence and six tempo presets.",
            color = TextMute,
            style = MaterialTheme.typography.bodySmall,
        )

        Spacer(Modifier.height(14.dp))

        // Level cards
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RunningLevel.entries.forEach { level ->
                LevelCard(
                    level = level,
                    selected = selectedLevel == level,
                    onClick = { onSelect(level) },
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // Continue CTA
        CtaButton(
            label = "CONTINUE",
            onClick = onNext,
            enabled = selectedLevel != null,
            trailingIcon = Icons.Filled.ChevronRight,
            height = 52.dp,
        )
    }
}

@Composable
private fun LevelCard(
    level: RunningLevel,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val lo = level.presets.first().bpm
    val hi = level.presets.last().bpm

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) AccentSoft else BgCard)
            .border(
                width = 1.dp,
                color = if (selected) Accent else LineBorder,
                shape = RoundedCornerShape(14.dp),
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Icon tile
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (selected) Accent else BgElev),
        ) {
            Icon(
                imageVector = levelIcon(level),
                contentDescription = null,
                tint = if (selected) OnAccent else Accent,
                modifier = Modifier.size(20.dp),
            )
        }

        // Label + blurb
        Column(modifier = Modifier.weight(1f)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = level.displayLabel,
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = level.tag,
                    color = TextDim,
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 0.sp,
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp,
                    ),
                    maxLines = 1,
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = level.blurb,
                color = TextMute,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
            )
        }

        // BPM range
        Text(
            text = "$lo–$hi",
            color = if (selected) Accent else TextMute,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.3).sp,
            ),
        )
    }
}

private fun levelIcon(level: RunningLevel): ImageVector = when (level) {
    RunningLevel.NEW         -> Icons.Filled.DirectionsRun
    RunningLevel.CASUAL      -> Icons.Filled.Favorite
    RunningLevel.REGULAR     -> Icons.Filled.LocalFireDepartment
    RunningLevel.COMPETITIVE -> Icons.Filled.EmojiEvents
}

// ── Screen 3: Permission ──────────────────────────────────────────────────────

@Composable
fun PermissionScreen(onBack: () -> Unit, onFinish: () -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { onFinish() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBase)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 22.dp)
            .padding(top = 14.dp, bottom = 16.dp),
    ) {
        TopBar(onBack = onBack, step = 1, totalSteps = 2)

        Spacer(Modifier.height(24.dp))

        // Centred content block
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Bell medallion
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(AccentSoft)
                    .border(1.dp, AccentRim, CircleShape),
            ) {
                Icon(
                    imageVector = Icons.Filled.NotificationsNone,
                    contentDescription = null,
                    tint = Accent,
                    modifier = Modifier.size(42.dp),
                )
            }

            Spacer(Modifier.height(26.dp))

            Text(
                text = "Control\nwithout looking",
                color = TextPrimary,
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Play, pause, and stop the metronome from your notification shade — so you never need to break stride.",
                color = TextMute,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(22.dp))

            // Faux notification preview
            FauxNotification()
        }

        // Action stack
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CtaButton(
                label = "ALLOW NOTIFICATIONS",
                onClick = {
                    if (Build.VERSION.SDK_INT >= 33) {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        onFinish()
                    }
                },
                height = 52.dp,
            )
            TextButton(
                onClick = onFinish,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
            ) {
                Text(
                    text = "Maybe later",
                    color = TextMute,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
private fun FauxNotification() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BgCard)
            .border(1.dp, LineBorder, RoundedCornerShape(14.dp))
            .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Metronome icon tile
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
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

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "RUNTICK",
                color = TextDim,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    letterSpacing = 0.3.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                text = "180 BPM · Race pace",
                color = TextPrimary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        // Pause button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(BgElev),
        ) {
            Icon(
                imageVector = Icons.Filled.Pause,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(12.dp),
            )
        }
    }
}

// ── Shared components ─────────────────────────────────────────────────────────

@Composable
private fun TopBar(onBack: () -> Unit, step: Int, totalSteps: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Back button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .border(1.dp, LineStrong, CircleShape)
                .clickable(onClick = onBack),
        ) {
            Icon(
                imageVector = Icons.Filled.NavigateBefore,
                contentDescription = "Back",
                tint = TextPrimary,
                modifier = Modifier.size(20.dp),
            )
        }

        ProgressDots(current = step, total = totalSteps)

        // Spacer for symmetry
        Spacer(Modifier.size(36.dp))
    }
}

@Composable
private fun ProgressDots(current: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(total) { i ->
            val width by animateDpAsState(
                targetValue = if (i <= current) 22.dp else 6.dp,
                animationSpec = tween(300),
                label = "dot$i",
            )
            Box(
                modifier = Modifier
                    .width(width)
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (i <= current) Accent else Color.White.copy(alpha = 0.15f)),
            )
        }
    }
}

@Composable
fun CtaButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    trailingIcon: ImageVector? = null,
    height: androidx.compose.ui.unit.Dp = 54.dp,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Accent,
            contentColor = OnAccent,
            disabledContainerColor = BgElev,
            disabledContentColor = TextDim,
        ),
        elevation = if (enabled) ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
        ) else ButtonDefaults.buttonElevation(0.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
        )
        if (trailingIcon != null) {
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
