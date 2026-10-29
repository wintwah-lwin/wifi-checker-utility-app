package au.edu.jcu.assessment.utilityapp.domain.logic

import au.edu.jcu.assessment.utilityapp.domain.model.ActivityEstimates
import au.edu.jcu.assessment.utilityapp.domain.model.ConnectionMetrics

class EstimateEngine {
    fun estimate(metrics: ConnectionMetrics): ActivityEstimates {
        val speed = metrics.downloadSpeedMbps

        val videoCallQuality = when {
            speed >= 15.0 && metrics.pingMs <= 50 -> "HD 1080p Smooth"
            speed >= 5.0 -> "Standard 720p"
            speed >= 1.5 -> "Basic Video"
            else -> "Audio Only / Poor"
        }

        val streaming4k = when {
            speed >= 25.0 -> "4K Ultra HD Supported"
            speed >= 10.0 -> "1080p Full HD Supported"
            speed >= 3.0 -> "720p HD Supported"
            else -> "Buffering Expected"
        }

        val download1GbTimeMinutes = if (speed > 0.0) {
            val seconds = (1000 * 8) / speed // 1GB = 8000 Mbits
            val mins = (seconds / 60).toInt()
            val secs = (seconds % 60).toInt()
            "${mins}m ${secs}s"
        } else {
            "N/A"
        }

        return ActivityEstimates(
            videoCallQuality = videoCallQuality,
            streaming4k = streaming4k,
            download1GbTimeMinutes = download1GbTimeMinutes
        )
    }
}
