package com.shitu.pickpic.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = DawnPink,
    secondary = AmberGold,
    tertiary = SmokeGray,
    background = MilkTea,
    surface = White,
    onPrimary = DeepCharcoal,
    onSecondary = White,
    onTertiary = White,
    onBackground = DeepCharcoal,
    onSurface = DeepCharcoal
)

@Composable
fun ShiTuTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
