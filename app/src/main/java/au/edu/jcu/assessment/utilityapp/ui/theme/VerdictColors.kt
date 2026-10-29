package au.edu.jcu.assessment.utilityapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import au.edu.jcu.assessment.utilityapp.domain.model.Grade

@Immutable
data class VerdictColors(val good: Color, val warn: Color, val bad: Color)

private val LightVerdict = VerdictColors(Color(0xFF17934B), Color(0xFFB8740A), Color(0xFFD23B2A))
private val DarkVerdict = VerdictColors(Color(0xFF4ADE80), Color(0xFFFBBF24), Color(0xFFF87171))

/** Semantic colours for good, struggling and poor, picked for contrast in light and dark themes. */
@Composable
fun verdictColors(): VerdictColors = if (isSystemInDarkTheme()) DarkVerdict else LightVerdict

@Composable
fun Grade?.color(): Color {
    val colors = verdictColors()
    return when (this) {
        Grade.GOOD -> colors.good
        Grade.STRUGGLES -> colors.warn
        Grade.POOR -> colors.bad
        null -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}