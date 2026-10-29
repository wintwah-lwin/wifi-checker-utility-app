package au.edu.jcu.assessment.utilityapp.domain.model

data class UsageStats(
    val totalTestsRun: Int = 0,
    val totalDataUsedMb: Double = 0.0,
    val averageDownloadSpeedMbps: Double = 0.0,
    val highestSpeedMbps: Double = 0.0,
    val lastTestTimestamp: Long = 0L
)
