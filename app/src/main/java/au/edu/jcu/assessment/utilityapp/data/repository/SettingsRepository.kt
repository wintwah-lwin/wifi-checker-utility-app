package au.edu.jcu.assessment.utilityapp.data.repository

import au.edu.jcu.assessment.utilityapp.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository {
    private val _settings = MutableStateFlow(Settings())
    val settings: Flow<Settings> = _settings.asStateFlow()

    fun updateSettings(newSettings: Settings) {
        _settings.value = newSettings
    }
}
