package au.edu.jcu.assessment.utilityapp.domain.model

enum class ErrorReason { NO_NETWORK, TIMEOUT, SERVER }

/** Events a running test emit */
sealed interface TestEvent {
    data class Sample(val mbps: Double) : TestEvent
    data class Finished(val measurement: Measurement) : TestEvent
    data class Failed(val reason: ErrorReason) : TestEvent
}