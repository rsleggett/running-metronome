package com.electricbiro.runningmetronome.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    // 96 sp BPM mega-number
    displayLarge = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 96.sp,
        lineHeight = 86.sp,
        letterSpacing = (-2).sp,
    ),
    // 56 sp welcome headline
    displayMedium = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 56.sp,
        lineHeight = 52.sp,
        letterSpacing = (-1.5).sp,
    ),
    // 34 sp screen titles
    displaySmall = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 34.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.8).sp,
    ),
    // 22 sp preset BPM chips
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.3).sp,
    ),
    // 14 sp body / level card label
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        letterSpacing = 0.sp,
    ),
    // 13.5 sp permission body
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 13.5.sp,
        lineHeight = 21.sp,
        letterSpacing = 0.sp,
    ),
    // 12 sp card blurb
    bodySmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.sp,
    ),
    // 14 sp CTA buttons
    labelLarge = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 14.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.7.sp,
    ),
    // 13 sp ghost/secondary buttons
    labelMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.3.sp,
    ),
    // 11 sp eyebrow / section labels
    labelSmall = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 11.sp,
        lineHeight = 11.sp,
        letterSpacing = 2.sp,
    ),
)
