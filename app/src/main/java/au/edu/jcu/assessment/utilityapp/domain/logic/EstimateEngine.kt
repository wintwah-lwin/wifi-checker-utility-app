package au.edu.jcu.assessment.utilityapp.domain.logic

import au.edu.jcu.assessment.utilityapp.domain.model.Estimate
import au.edu.jcu.assessment.utilityapp.domain.model.EstimateKey
import au.edu.jcu.assessment.utilityapp.domain.model.Measurement
import au.edu.jcu.assessment.utilityapp.domain.model.Usage

class EstimateEngine {
    fun estimate(measurement: Measurement, usage: Usage): Estimate? {
        val down = measurement.downMbps ?: 0.0

        return when (usage) {
            Usage.HD, Usage.UHD -> {
                val quality = when {
                    down >= 25.0 -> "4K UHD"
                    down >= 8.0 -> "1080p HD"
                    down >= 3.0 -> "720p HD"
                    else -> "480p SD"
                }
                Estimate(EstimateKey.BEST_QUALITY, quality)
            }
            Usage.DOWNLOAD -> {
                if (down > 0) {
                    val minutes = (10000 * 8 / down / 60).toInt()
                    Estimate(EstimateKey.DOWNLOAD_TIME, "$minutes min")
                } else {
                    Estimate(EstimateKey.TOO_SLOW)
                }
            }
            Usage.CALL, Usage.MEETING -> {
                when {
                    down >= 6.0 && measurement.latencyMs < 100 -> Estimate(EstimateKey.GROUP_VIDEO)
                    down >= 2.0 -> Estimate(EstimateKey.ONE_TO_ONE)
                    else -> Estimate(EstimateKey.AUDIO_ONLY)
                }
            }
            else -> {
                if (down > 0) {
                    Estimate(EstimateKey.SAFE_BITRATE, String.format("%.1f", down * 0.8))
                } else null
            }
        }
    }
}
