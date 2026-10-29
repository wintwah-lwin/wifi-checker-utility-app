package au.edu.jcu.assessment.utilityapp.domain.model

enum class SpeedUnit(val label: String) {
    MBPS("Mbps"),
    MB_S("MB/s"),
    KBPS("Kbps")
}

data class AppSettings(
    val testServerUrl: String = "https://speed.cloudflare.com/__down",
    val autoTestOnLaunch: Boolean = false,
    val pingCount: Int = 5,
    val showDiagnosticTips: Boolean = true,
    val preferredSpeedUnit: SpeedUnit = SpeedUnit.MBPS
)
