package au.edu.jcu.assessment.utilityapp.ui.check

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import au.edu.jcu.assessment.utilityapp.UtilityApplication
import au.edu.jcu.assessment.utilityapp.data.repository.ConnectionRepository
import au.edu.jcu.assessment.utilityapp.data.repository.SettingsRepository
import au.edu.jcu.assessment.utilityapp.domain.logic.ConnectionAnalyzer
import au.edu.jcu.assessment.utilityapp.domain.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Where the test is. Each state is a different screen of content. */
sealed interface TestState {
    data object Idle : TestState
    data class Running(val liveMbps: Double, val samples: List<Double>) : TestState
    data class Done(val measurement: Measurement, val testedOn: LinkInfo, val depth: TestDepth) : TestState
    data class Failed(val reason: ErrorReason) : TestState
}

/** One immutable snapshot of everything the main screen draws. */
data class UtilityUiState(
    val settings: Settings = Settings(),
    val link: LinkInfo = LinkInfo(),
    val test: TestState = TestState.Idle,
    val analysis: Analysis? = null,
    val stale: Boolean = false
)

class UtilityViewModel(
    private val connection: ConnectionRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val test = MutableStateFlow<TestState>(TestState.Idle)
    private val link = connection.link.stateIn(viewModelScope, SharingStarted.Eagerly, LinkInfo())
    private var testJob: Job? = null

    /**
     * Combines settings, connection and test state. Analysis is recomputed whenever a setting
     * changes, so switching activity re-grades the last result without running another test.
     */
    val uiState: StateFlow<UtilityUiState> = combine(settingsRepository.settings, link, test) { settings, link, test ->
        val done = test as? TestState.Done
        UtilityUiState(
            settings = settings,
            link = link,
            test = test,
            analysis = done?.let { ConnectionAnalyzer.analyze(it.measurement, it.testedOn, settings) },
            // The result is stale if the phone moved to a different network after the test.
            stale = done != null && (done.testedOn.type != link.type || done.testedOn.name != link.name)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UtilityUiState())

    fun startTest() {
        val settings = settingsRepository.settings.value
        val currentLink = link.value
        if (currentLink.type == LinkType.NONE) {
            test.value = TestState.Failed(ErrorReason.NO_NETWORK)
            return
        }
        // Data saver: never run a heavy test on a metered connection.
        val depth = if (currentLink.metered && settings.dataSaver) TestDepth.QUICK else settings.depth

        testJob?.cancel()
        test.value = TestState.Running(0.0, emptyList())
        testJob = viewModelScope.launch {
            val samples = mutableListOf<Double>()
            connection.runTest(depth).collect { event ->
                test.value = when (event) {
                    is TestEvent.Sample -> {
                        samples += event.mbps
                        TestState.Running(event.mbps, samples.toList())
                    }
                    is TestEvent.Finished -> TestState.Done(event.measurement.copy(samples = samples.toList()), currentLink, depth)
                    is TestEvent.Failed -> TestState.Failed(event.reason)
                }
            }
        }
    }

    fun selectUsage(usage: Usage) = settingsRepository.update { it.copy(usage = usage) }

    fun onLocationPermissionResult() = connection.refreshLink()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = (this[APPLICATION_KEY] as UtilityApplication).container
                UtilityViewModel(container.connectionRepository, container.settingsRepository)
            }
        }
    }
}