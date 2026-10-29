package au.edu.jcu.assessment.utilityapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import au.edu.jcu.assessment.utilityapp.data.repository.SettingsRepository
import au.edu.jcu.assessment.utilityapp.domain.model.AppSettings
import au.edu.jcu.assessment.utilityapp.domain.model.SpeedUnit
import au.edu.jcu.assessment.utilityapp.domain.model.UsageStats
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = repository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings()
    )

    val usage: StateFlow<UsageStats> = repository.usage.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UsageStats()
    )

    fun toggleDiagnosticTips(enabled: Boolean) {
        val current = settings.value
        repository.updateSettings(current.copy(showDiagnosticTips = enabled))
    }

    fun selectSpeedUnit(unit: SpeedUnit) {
        val current = settings.value
        repository.updateSettings(current.copy(preferredSpeedUnit = unit))
    }

    class Factory(private val repository: SettingsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(repository) as T
        }
    }
}
