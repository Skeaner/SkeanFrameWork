package me.skean.skeanframework.composeui

import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Created by Skean on 2025/07/03.
 */
object ButtonPresets {

    @Composable
    fun noBackgroundColors(contentColor: Color = Color.Black): ButtonColors {
        return ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            contentColor = contentColor
        )
    }

}