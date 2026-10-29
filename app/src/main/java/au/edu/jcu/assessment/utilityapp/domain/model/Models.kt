package au.edu.jcu.assessment.utilityapp.domain.model

enum class TestStage {
    IDLE,
    PING,
    DOWNLOAD,
    UPLOAD,
    DIAGNOSIS,
    FINISHED,
    ERROR
}

enum class NetworkVerdict {
    EXCELLENT,
    GOOD,
    FAIR,
    POOR,
    OFFLINE
}

data class ConnectionMetrics(
    val downloadSpeedMbps: Double = 0.0,
    val uploadSpeedMbps: Double = 0.0,
    val pingMs: Long = 0,
    val jitterMs: Long = 0,
    val signalStrengthDbm: Int = -100,
    val ssid: String = "Unknown Wi-Fi",
    val ipAddress: String = "0.0.0.0",
    val isWifi: Boolean = true
)

data class WifiInfo(
    val ssid: String = "Not Connected",
    val bssid: String = "00:00:00:00:00:00",
    val rssi: Int = -100,
    val linkSpeedMbps: Int = 0,
    val frequencyMhz: Int = 0,
    val isConnected: Boolean = false
)

data class ActivityEstimates(
    val videoCallQuality: String = "Poor",
    val streaming4k: String = "Not Recommended",
    val download1GbTimeMinutes: String = "--"
)

data class AnalysisReport(
    val verdict: NetworkVerdict = NetworkVerdict.OFFLINE,
    val metrics: ConnectionMetrics = ConnectionMetrics(),
    val wifiInfo: WifiInfo = WifiInfo(),
    val diagnosisList: List<String> = emptyList(),
    val tipsList: List<String> = emptyList(),
    val estimates: ActivityEstimates = ActivityEstimates()
)
