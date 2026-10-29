package au.edu.jcu.assessment.utilityapp.domain.logic

import au.edu.jcu.assessment.utilityapp.domain.model.Grade
import au.edu.jcu.assessment.utilityapp.domain.model.Measurement
import au.edu.jcu.assessment.utilityapp.domain.model.Metric
import au.edu.jcu.assessment.utilityapp.domain.model.Picky
import au.edu.jcu.assessment.utilityapp.domain.model.Usage
import au.edu.jcu.assessment.utilityapp.domain.model.Verdict

class VerdictCalculator {
    fun calculateVerdict(measurement: Measurement, usage: Usage, picky: Picky): Verdict {
        val needs = usage.needs
        val scale = picky.scale

        val requiredDown = needs.downMbps * scale
        val requiredUp = needs.upMbps * scale
        val maxLatency = needs.maxLatencyMs / scale
        val maxJitter = needs.maxJitterMs / scale

        val downRatio = (measurement.downMbps ?: 0.0) / requiredDown
        val upRatio = (measurement.upMbps ?: 0.0) / requiredUp
        val latencyRatio = maxLatency / measurement.latencyMs.coerceAtLeast(1)
        val jitterRatio = maxJitter / measurement.jitterMs.coerceAtLeast(1)

        val ratios = mapOf(
            Metric.DOWNLOAD to downRatio,
            Metric.UPLOAD to upRatio,
            Metric.LATENCY to latencyRatio,
            Metric.STABILITY to jitterRatio
        )

        val (limitingMetric, lowestRatio) = ratios.minBy { it.value }

        val grade = when {
            lowestRatio >= 1.0 -> Grade.GOOD
            lowestRatio >= 0.6 -> Grade.STRUGGLES
            else -> Grade.POOR
        }

        return Verdict(grade = grade, limiting = limitingMetric, ratio = lowestRatio)
    }
}
