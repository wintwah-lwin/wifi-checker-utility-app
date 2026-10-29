package au.edu.jcu.assessment.utilityapp.data.remote

interface NetworkTester {
    suspend fun measurePing(serverUrl: String): Long
    suspend fun measureDownloadSpeed(
        serverUrl: String,
        onProgress: (speedMbps: Double, progress: Float) -> Unit
    ): Double
    suspend fun measureUploadSpeed(
        onProgress: (speedMbps: Double, progress: Float) -> Unit
    ): Double
}
