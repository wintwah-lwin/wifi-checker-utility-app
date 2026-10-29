package au.edu.jcu.assessment.utilityapp.domain.logic

import au.edu.jcu.assessment.utilityapp.domain.model.ConnectionMetrics
import au.edu.jcu.assessment.utilityapp.domain.model.NetworkVerdict

class VerdictCalculator {
    fun calculateVerdict(metrics: ConnectionMetrics, isConnected: Boolean): NetworkVerdict {
        if (!isConnected || (metrics.downloadSpeedMbps == 0.0 && metrics.pingMs == 0L)) {
            return NetworkVerdict.OFFLINE
        }

        val speed = metrics.downloadSpeedMbps
        val ping = metrics.pingMs

        return when {
            speed >= 50.0 && ping <= 30 -> NetworkVerdict.EXCELLENT
            speed >= 20.0 && ping <= 60 -> NetworkVerdict.GOOD
            speed >= 5.0 && ping <= 120 -> NetworkVerdict.FAIR
            else -> NetworkVerdict.POOR
        }
    }
}
