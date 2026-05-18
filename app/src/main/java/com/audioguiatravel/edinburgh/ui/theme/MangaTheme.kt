package com.audioguiatravel.edinburgh.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Paleta manga: tinta, papel, acentos cómic
val MangaInk = Color(0xFF1A1A2E)
val MangaPaper = Color(0xFFFFF8F0)
val MangaPaperDim = Color(0xFFF3E8D8)
val MangaAccentPink = Color(0xFFE94560)
val MangaAccentTeal = Color(0xFF2EC4B6)
val MangaShadow = Color(0x33000000)
val MangaSpeechFill = Color(0xFFFFFFFF)
val MangaSpeedLine = Color(0x1A1A1A2E)

private val LightColors = lightColorScheme(
    primary = MangaInk,
    onPrimary = MangaPaper,
    secondary = MangaAccentPink,
    onSecondary = MangaPaper,
    tertiary = MangaAccentTeal,
    background = MangaPaper,
    onBackground = MangaInk,
    surface = MangaPaper,
    onSurface = MangaInk,
    surfaceVariant = MangaPaperDim,
    onSurfaceVariant = MangaInk,
)

private val DarkColors = darkColorScheme(
    primary = MangaPaper,
    onPrimary = MangaInk,
    secondary = MangaAccentPink,
    onSecondary = MangaInk,
    tertiary = MangaAccentTeal,
    background = Color(0xFF12121F),
    onBackground = MangaPaper,
    surface = Color(0xFF1E1E30),
    onSurface = MangaPaper,
    surfaceVariant = Color(0xFF2A2A42),
    onSurfaceVariant = MangaPaperDim,
)

val MangaTitleStyle = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Black,
    fontSize = 26.sp,
    letterSpacing = (-0.5).sp,
)

val MangaBodyStyle = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Medium,
    fontSize = 15.sp,
    lineHeight = 22.sp,
)

val MangaCaptionStyle = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Bold,
    fontSize = 12.sp,
    letterSpacing = 0.5.sp,
)

@Composable
fun EdinburghMangaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = MaterialTheme.typography,
        content = content,
    )
}
