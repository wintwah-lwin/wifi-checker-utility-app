package au.edu.jcu.assessment.utilityapp.domain.logic

import au.edu.jcu.assessment.utilityapp.domain.model.*

/** Activity-specific numbers that make the verdict concrete. */
object EstimateEngine {
    private val videoTiers = listOf("480p" to 3.0, "720p" to 5.0, "1080p" to 8.0, "4K" to 25.0)
    private const val GAME_BITS_MEGA = 80_000.0      // 10 GB in megabits
    private const val FILE_BITS_MEGA = 4_000.0       // 500 MB in megabits

    /** Highest video quality the measured download speed can hold, or null if even 480p is too much. */
    fun bestQuality(m: Measurement, picky: Picky): String? {
        val down = m.downMbps ?: return null
        return videoTiers.lastOrNull { down >= it.second * picky.scale }?.first
    }

    fun estimate(m: Measurement, s: Settings): Estimate? {
        val down = m.downMbps ?: return null
        val up = m.upMbps ?: return null
        return when (s.usage) {
            Usage.HD, Usage.UHD -> bestQuality(m, s.picky)
                ?.let { Estimate(EstimateKey.BEST_QUALITY, it) } ?: Estimate(EstimateKey.TOO_SLOW)
            Usage.DOWNLOAD -> Estimate(EstimateKey.DOWNLOAD_TIME, (GAME_BITS_MEGA / down.coerceAtLeast(0.1)).toString())
            Usage.WORK -> Estimate(EstimateKey.UPLOAD_TIME, (FILE_BITS_MEGA / up.coerceAtLeast(0.1)).toString())
            Usage.LIVE -> Estimate(EstimateKey.SAFE_BITRATE, "%.0f".format(up * 0.7))
            Usage.CALL, Usage.MEETING -> when {
                VerdictCalculator.grade(m, Usage.MEETING, s.picky).grade == Grade.GOOD -> Estimate(EstimateKey.GROUP_VIDEO)
                VerdictCalculator.grade(m, Usage.CALL, s.picky).grade == Grade.GOOD -> Estimate(EstimateKey.ONE_TO_ONE)
                else -> Estimate(EstimateKey.AUDIO_ONLY)
            }
            else -> null
        }
    }
}