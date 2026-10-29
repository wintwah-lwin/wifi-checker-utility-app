package au.edu.jcu.assessment.utilityapp.ui.check

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import au.edu.jcu.assessment.utilityapp.data.repository.ConnectionRepository
import au.edu.jcu.assessment.utilityapp.domain.model.AnalysisReport
import au.edu.jcu.assessment.utilityapp.domain.model.TestEvent
import au.edu.jcu.assessment.utilityapp.domain.model.TestStage
import au.edu.jcu.assessment.utilityapp.domain.model.WifiInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UtilityUiState(
    val isTesting: Boolean = false,
    val stage: TestStage = TestStage.IDLE,
    val wifiInfo: WifiInfo = WifiInfo(),
    val pingMs: Long = 0,
    val downloadMbps: Double = 0.0,
    val uploadMbps: Double = 0.0,
    val progress: Float = 0.0f,
    val report: AnalysisReport = AnalysisReport()
)

class UtilityViewModel(
    private val repository: ConnectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UtilityUiState())
    val uiState: StateFlow<UtilityUiState> = _uiState.asStateFlow()

    init {
        refreshWifiInfo()
    }

    fun refreshWifiInfo() {
        val currentInfo = repository.getWifiInfo()
        _uiState.update { it.copy(wifiInfo = currentInfo) }
    }

    fun startSpeedTest() {
        if (_uiState.value.isTesting) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isTesting = true,
                    stage = TestStage.PING,
                    pingMs = 0,
                    downloadMbps = 0.0,
                    uploadMbps = 0.0,
                    progress = 0.1f
                )
            }

            repository.runSpeedTest().collect { event ->
                when (event) {
                    is TestEvent.Started -> {
                        _uiState.update { it.copy(stage = event.stage) }
                    }
                    is TestEvent.PingUpdated -> {
                        _uiState.update { it.copy(pingMs = event.pingMs, progress = 0.3f) }
                    }
                    is TestEvent.DownloadUpdated -> {
                        _uiState.update {
                            it.copy(downloadMbps = event.speedMbps, progress = 0.6f)
                        }
                    }
                    is TestEvent.UploadUpdated -> {
                        _uiState.update {
                            it.copy(uploadMbps = event.speedMbps, progress = 0.85f)
                        }
                    }
                    is TestEvent.Completed -> {
                        _uiState.update {
                            it.copy(
                                isTesting = false,
                                stage = TestStage.FINISHED,
                                progress = 1.0f,
                                report = event.report
                            )
                        }
                    }
                    is TestEvent.Failed -> {
                        _uiState.update {
                            it.copy(isTesting = false, stage = TestStage.ERROR)
                        }
                    }
                }
            }
        }
    }

    class Factory(private val repository: ConnectionRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UtilityViewModel(repository) as T
        }
    }
}
