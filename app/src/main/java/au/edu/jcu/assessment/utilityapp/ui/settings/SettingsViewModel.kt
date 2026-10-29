package au.edu.jcu.assessment.utilityapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import au.edu.jcu.assessment.utilityapp.data.repository.SettingsRepository
import au.edu.jcu.assessment.utilityapp.domain.model.Picky
import au.edu.jcu.assessment.utilityapp.domain.model.Settings
import au.edu.jcu.assessment.utilityapp.domain.model.TestDepth
import au.edu.jcu.assessment.utilityapp.domain.model.Usage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<Settings> = repository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Settings()
    )

    fun selectUsage(usage: Usage) {
        repository.updateSettings(settings.value.copy(usage = usage))
    }

    fun selectPicky(picky: Picky) {
        repository.updateSettings(settings.value.copy(picky = picky))
    }

    fun selectDepth(depth: TestDepth) {
        repository.updateSettings(settings.value.copy(depth = depth))
    }

    fun toggleDataSaver(enabled: Boolean) {
        repository.updateSettings(settings.value.copy(dataSaver = enabled))
    }

    fun toggleUseMbps(useMbps: Boolean) {
        repository.updateSettings(settings.value.copy(useMbps = useMbps))
    }

    class Factory(private val repository: SettingsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(repository) as T
        }
    }
}
