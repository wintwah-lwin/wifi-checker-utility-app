package au.edu.jcu.assessment.utilityapp.data.repository

import au.edu.jcu.assessment.utilityapp.domain.model.AppSettings
import au.edu.jcu.assessment.utilityapp.domain.model.UsageStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsRepository {
    private val _settings = MutableStateFlow(AppSettings())
    val settings: Flow<AppSettings> = _settings.asStateFlow()

    private val _usage = MutableStateFlow(UsageStats())
    val usage: Flow<UsageStats> = _usage.asStateFlow()

    fun updateSettings(newSettings: AppSettings) {
        _settings.value = newSettings
    }

    fun recordTestRun(downloadMbps: Double) {
        _usage.update { current ->
            val newTotal = current.totalTestsRun + 1
            val newHighest = maxOf(current.highestSpeedMbps, downloadMbps)
            val newAvg = if (newTotal == 1) downloadMbps else (current.averageDownloadSpeedMbps * (newTotal - 1) + downloadMbps) / newTotal

            current.copy(
                totalTestsRun = newTotal,
                totalDataUsedMb = current.totalDataUsedMb + 12.5,
                averageDownloadSpeedMbps = newAvg,
                highestSpeedMbps = newHighest,
                lastTestTimestamp = System.currentTimeMillis()
            )
        }
    }
}
