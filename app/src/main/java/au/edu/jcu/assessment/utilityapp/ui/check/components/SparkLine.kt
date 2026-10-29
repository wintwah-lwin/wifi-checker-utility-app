package au.edu.jcu.assessment.utilityapp.ui.check.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/** A small area chart of download speed during the test. */
@Composable
fun SparkLine(samples: List<Double>, modifier: Modifier = Modifier) {
    if (samples.size < 2) return
    val line = MaterialTheme.colorScheme.primary
    val area = MaterialTheme.colorScheme.surfaceVariant
    Canvas(modifier.fillMaxWidth().height(38.dp)) {
        val max = samples.max().coerceAtLeast(1.0)
        val step = size.width / (samples.size - 1)
        val pad = 4.dp.toPx()
        val points = samples.mapIndexed { i, v -> Offset(i * step, size.height - (v / max).toFloat() * (size.height - pad)) }
        val fillPath = Path().apply {
            moveTo(0f, size.height); points.forEach { lineTo(it.x, it.y) }; lineTo(size.width, size.height); close()
        }
        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y); points.drop(1).forEach { lineTo(it.x, it.y) }
        }
        drawPath(fillPath, area)
        drawPath(linePath, line, style = Stroke(width = 2.5.dp.toPx(), join = StrokeJoin.Round))
    }
}
