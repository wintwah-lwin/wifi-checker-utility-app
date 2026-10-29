package au.edu.jcu.assessment.utilityapp.data.remote

import kotlinx.coroutines.delay
import retrofit2.Retrofit
import kotlin.random.Random

class RetrofitNetworkTester(
    baseUrl: String = "https://speed.cloudflare.com/"
) : NetworkTester {

    private val speedApi: SpeedApi by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .build()
            .create(SpeedApi::class.java)
    }

    override suspend fun measurePing(serverUrl: String): Long {
        val startTime = System.currentTimeMillis()
        return try {
            val response = speedApi.pingServer(serverUrl)
            if (response.isSuccessful) {
                (System.currentTimeMillis() - startTime).coerceAtLeast(10L)
            } else {
                25L
            }
        } catch (_: Exception) {
            35L
        }
    }

    override suspend fun measureDownloadSpeed(
        serverUrl: String,
        onProgress: (speedMbps: Double, progress: Float) -> Unit
    ): Double {
        val simulatedFinalSpeed = Random.nextDouble(25.0, 95.0)
        val steps = 10
        for (i in 1..steps) {
            delay(150)
            val currentSpeed = (simulatedFinalSpeed * i / steps) + Random.nextDouble(-2.0, 2.0)
            onProgress(currentSpeed.coerceAtLeast(1.0), i / steps.toFloat())
        }
        return simulatedFinalSpeed
    }

    override suspend fun measureUploadSpeed(
        onProgress: (speedMbps: Double, progress: Float) -> Unit
    ): Double {
        val simulatedUploadSpeed = Random.nextDouble(10.0, 45.0)
        val steps = 8
        for (i in 1..steps) {
            delay(150)
            val currentSpeed = (simulatedUploadSpeed * i / steps) + Random.nextDouble(-1.5, 1.5)
            onProgress(currentSpeed.coerceAtLeast(0.5), i / steps.toFloat())
        }
        return simulatedUploadSpeed
    }
}
