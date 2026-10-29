package au.edu.jcu.assessment.utilityapp.ui.check.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

private const val START_ANGLE = 135f
private const val SWEEP_ANGLE = 270f

/**
 * A 270-degree ring that fills to [fill] (0..1). The small tick at [tick] marks the speed the
 * chosen activity needs, so you can see whether the fill passes it.
 */
@Composable
fun SpeedGauge(
    fill: Float,
    tick: Float?,
    centerText: String,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val animatedFill by animateFloatAsState(fill, tween(500), label = "gaugeFill")
    val track = MaterialTheme.colorScheme.surfaceVariant
    val tickColor = MaterialTheme.colorScheme.onSurface

    Box(modifier.size(236.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(236.dp)) {
            val stroke = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
            val inset = stroke.width / 2
            val arcSize = Size(size.width - stroke.width, size.height - stroke.width)
            val topLeft = Offset(inset, inset)

            drawArc(track, START_ANGLE, SWEEP_ANGLE, false, topLeft, arcSize, style = stroke)
            if (animatedFill > 0f) {
                drawArc(color, START_ANGLE, SWEEP_ANGLE * animatedFill, false, topLeft, arcSize, style = stroke)
            }
            tick?.let {
                val angle = Math.toRadians((START_ANGLE + SWEEP_ANGLE * it).toDouble())
                val radius = arcSize.width / 2
                fun point(r: Float) = center + Offset(cos(angle).toFloat() * r, sin(angle).toFloat() * r)
                drawLine(tickColor, point(radius - 14.dp.toPx()), point(radius + 6.dp.toPx()), 3.dp.toPx(), StrokeCap.Round)
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(centerText, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.ExtraBold)
            Text(unit, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}