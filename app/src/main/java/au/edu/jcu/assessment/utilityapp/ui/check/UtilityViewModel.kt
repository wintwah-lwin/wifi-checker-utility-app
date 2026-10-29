package au.edu.jcu.assessment.utilityapp.ui.check

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import au.edu.jcu.assessment.utilityapp.data.repository.ConnectionRepository
import au.edu.jcu.assessment.utilityapp.data.repository.SettingsRepository
import au.edu.jcu.assessment.utilityapp.domain.logic.ConnectionAnalyzer
import au.edu.jcu.assessment.utilityapp.domain.model.Analysis
import au.edu.jcu.assessment.utilityapp.domain.model.LinkInfo
import au.edu.jcu.assessment.utilityapp.domain.model.Measurement
import au.edu.jcu.assessment.utilityapp.domain.model.Settings
import au.edu.jcu.assessment.utilityapp.domain.model.TestEvent
import au.edu.jcu.assessment.utilityapp.domain.model.Usage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UtilityUiState(
    val isTesting: Boolean = false,
    val linkInfo: LinkInfo = LinkInfo(),
    val measurement: Measurement? = null,
    val analysis: Analysis? = null,
    val settings: Settings = Settings(),
    val currentSampleMbps: Double = 0.0
)

class UtilityViewModel(
    private val connectionRepository: ConnectionRepository,
    private val settingsRepository: SettingsRepository,
    private val analyzer: ConnectionAnalyzer = ConnectionAnalyzer()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UtilityUiState())
    val uiState: StateFlow<UtilityUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { newSettings ->
                _uiState.update { current ->
                    val newAnalysis = current.measurement?.let { m ->
                        analyzer.analyze(m, current.linkInfo, newSettings)
                    }
                    current.copy(settings = newSettings, analysis = newAnalysis ?: current.analysis)
                }
            }
        }
        refreshLinkInfo()
    }

    fun refreshLinkInfo() {
        val info = connectionRepository.getLinkInfo()
        _uiState.update { it.copy(linkInfo = info) }
    }

    fun selectUsage(usage: Usage) {
        val updated = _uiState.value.settings.copy(usage = usage)
        settingsRepository.updateSettings(updated)
    }

    fun startTest() {
        if (_uiState.value.isTesting) return

        val settings = _uiState.value.settings
        viewModelScope.launch {
            _uiState.update { it.copy(isTesting = true, currentSampleMbps = 0.0) }

            connectionRepository.runSpeedTest(settings.depth).collect { event ->
                when (event) {
                    is TestEvent.Sample -> {
                        _uiState.update { it.copy(currentSampleMbps = event.mbps) }
                    }
                    is TestEvent.Finished -> {
                        val analysis = analyzer.analyze(event.measurement, _uiState.value.linkInfo, _uiState.value.settings)
                        _uiState.update {
                            it.copy(
                                isTesting = false,
                                measurement = event.measurement,
                                analysis = analysis
                            )
                        }
                    }
                    is TestEvent.Failed -> {
                        _uiState.update { it.copy(isTesting = false) }
                    }
                }
            }
        }
    }

    class Factory(
        private val connectionRepository: ConnectionRepository,
        private val settingsRepository: SettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UtilityViewModel(connectionRepository, settingsRepository) as T
        }
    }
}
