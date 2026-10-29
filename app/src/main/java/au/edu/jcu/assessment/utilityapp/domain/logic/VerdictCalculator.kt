package au.edu.jcu.assessment.utilityapp.domain.logic

import au.edu.jcu.assessment.utilityapp.domain.model.*
import kotlin.math.max

/**
 * Turns a measurement into a verdict for one activity.
 *
 * Each metric becomes a ratio against what the activity needs: 1.0 or more means it is met.
 * The worst ratio decides the grade and names the limiting metric.
 */
object VerdictCalculator {
    const val GOOD_AT = 1.0
    const val STRUGGLES_AT = 0.6

    fun ratios(m: Measurement, usage: Usage, picky: Picky): Map<Metric, Double> {
        val need = usage.needs
        val k = picky.scale
        return buildMap {
            // Lower is better for latency and jitter, so the ratio is need divided by measured.
            put(Metric.LATENCY, (need.maxLatencyMs / k) / max(m.latencyMs, 1))
            put(Metric.STABILITY, (need.maxJitterMs / k) / max(m.jitterMs, 1))
            m.downMbps?.let { put(Metric.DOWNLOAD, it / (need.downMbps * k)) }
            m.upMbps?.let { put(Metric.UPLOAD, it / (need.upMbps * k)) }
        }
    }

    fun grade(m: Measurement, usage: Usage, picky: Picky): Verdict {
        val worst = ratios(m, usage, picky).entries.minBy { it.value }
        val grade = when {
            worst.value >= GOOD_AT -> Grade.GOOD
            worst.value >= STRUGGLES_AT -> Grade.STRUGGLES
            else -> Grade.POOR
        }
        return Verdict(grade, worst.key, worst.value)
    }
}