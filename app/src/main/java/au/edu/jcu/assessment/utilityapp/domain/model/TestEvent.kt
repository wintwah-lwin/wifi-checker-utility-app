package au.edu.jcu.assessment.utilityapp.domain.model

sealed interface TestEvent {
    data class Started(val stage: TestStage) : TestEvent
    data class PingUpdated(val pingMs: Long) : TestEvent
    data class DownloadUpdated(val speedMbps: Double, val progress: Float) : TestEvent
    data class UploadUpdated(val speedMbps: Double, val progress: Float) : TestEvent
    data class Completed(val report: AnalysisReport) : TestEvent
    data class Failed(val reason: String) : TestEvent
}
