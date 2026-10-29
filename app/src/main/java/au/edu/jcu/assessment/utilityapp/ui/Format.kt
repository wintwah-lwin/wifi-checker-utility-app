package au.edu.jcu.assessment.utilityapp.ui

import kotlin.math.roundToInt
import kotlin.math.sqrt

/** Square-root scale so slow speeds still move the gauge visibly. 200 Mbps fills the ring. */
fun speedFraction(mbps: Double): Float = sqrt((mbps / 200.0).coerceIn(0.0, 1.0)).toFloat()

fun formatSpeed(mbps: Double, useMbps: Boolean): String {
    val value = if (useMbps) mbps else mbps / 8
    return if (value >= 10) value.roundToInt().toString() else "%.1f".format(value)
}

fun formatDuration(seconds: Double): String = when {
    seconds < 90 -> "${seconds.roundToInt()} s"
    seconds < 5_400 -> "${(seconds / 60).roundToInt()} min"
    else -> "%.1f h".format(seconds / 3_600)
}