package me.skean.framework.example.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Created by Skean on 2025/07/01.
 */
private val primary = Color(0xFF3F51B5)
private val primaryVariant = Color(0xFF303F9F)
private val secondary = Color(0xFFFF4081)
private val secondaryVariant = Color(0xFFCA3367)
private val background = Color.Transparent
private val textSelectedColor = Color(0x99FF6A6A)

private val DarkColors = darkColors(
    primary = primary,
    primaryVariant = primaryVariant,
    secondary = secondary,
    secondaryVariant = secondaryVariant,
    background = background
)

private val LightColors = lightColors(
    primary = primary,
    primaryVariant = primaryVariant,
    secondary = secondary,
    secondaryVariant = secondaryVariant,
    background = background
)

private val appTextSelectionColors = TextSelectionColors(handleColor = secondary, backgroundColor = textSelectedColor)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(colors = if (darkTheme) DarkColors else LightColors) {
        CompositionLocalProvider(LocalTextSelectionColors provides appTextSelectionColors, content = content)
    }
}