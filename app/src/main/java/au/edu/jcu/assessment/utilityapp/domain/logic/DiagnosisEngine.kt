package au.edu.jcu.assessment.utilityapp.domain.logic

import au.edu.jcu.assessment.utilityapp.domain.model.ConnectionMetrics
import au.edu.jcu.assessment.utilityapp.domain.model.WifiInfo

class DiagnosisEngine {
    fun diagnose(metrics: ConnectionMetrics, wifiInfo: WifiInfo): List<String> {
        val diagnoses = mutableListOf<String>()

        if (!wifiInfo.isConnected) {
            return listOf("Wi-Fi is currently disconnected. Connect to a network to test speed.")
        }

        if (wifiInfo.rssi < -75) {
            diagnoses.add("Weak Wi-Fi signal strength (${wifiInfo.rssi} dBm). Expect packet loss or speed drops.")
        } else {
            diagnoses.add("Strong Wi-Fi signal strength (${wifiInfo.rssi} dBm).")
        }

        if (metrics.pingMs > 100) {
            diagnoses.add("High latency / ping (${metrics.pingMs} ms). May cause lag in video calls and online gaming.")
        } else if (metrics.pingMs > 0) {
            diagnoses.add("Low network latency (${metrics.pingMs} ms). Great for real-time applications.")
        }

        if (metrics.downloadSpeedMbps < 10.0) {
            diagnoses.add("Download speed (${String.format("%.1f", metrics.downloadSpeedMbps)} Mbps) is below average for HD streaming.")
        } else {
            diagnoses.add("Solid download bandwidth (${String.format("%.1f", metrics.downloadSpeedMbps)} Mbps).")
        }

        if (metrics.jitterMs > 20) {
            diagnoses.add("Noticeable network jitter (${metrics.jitterMs} ms). Connection stability varies.")
        }

        return diagnoses
    }
}
