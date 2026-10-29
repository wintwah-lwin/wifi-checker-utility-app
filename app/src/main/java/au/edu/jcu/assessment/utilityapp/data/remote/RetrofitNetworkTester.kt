package au.edu.jcu.assessment.utilityapp.data.remote

import au.edu.jcu.assessment.utilityapp.domain.model.Measurement
import au.edu.jcu.assessment.utilityapp.domain.model.TestDepth
import au.edu.jcu.assessment.utilityapp.domain.model.TestEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.math.abs

class RetrofitNetworkTester(private val api: SpeedApi) : NetworkTester {

    override fun run(depth: TestDepth): Flow<TestEvent> = channelFlow {
        val latencies = measureLatencies()
        val down = if (depth.downloadBytes > 0) {
            measureDownload(depth.downloadBytes) { send(TestEvent.Sample(it)) }
        } else null
        val up = if (depth.uploadBytes > 0) measureUpload(depth.uploadBytes) else null
        send(TestEvent.Finished(Measurement(latencies.median(), latencies.jitter(), down, up)))
    }.flowOn(Dispatchers.IO)   // blocking reads must not run on the main thread

    /** Time-to-response of near-empty requests. The median ignores one-off spikes. */
    private suspend fun measureLatencies(): List<Int> = List(LATENCY_PROBES) {
        val start = System.nanoTime()
        api.download(0).close()
        ((System.nanoTime() - start) / 1_000_000).toInt()
    }

    /** Streams the file, reporting a speed sample about every 200 ms, and returns the overall Mbps. */
    private suspend fun measureDownload(bytes: Long, onSample: suspend (Double) -> Unit): Double =
        api.download(bytes).use { body ->
            val source = body.source()
            val buffer = ByteArray(BUFFER_SIZE)
            var total = 0L
            var windowBytes = 0L
            val start = System.nanoTime()
            var windowStart = start
            while (true) {
                currentCoroutineContext().ensureActive()      // stop promptly if the user cancels
                val read = source.read(buffer)
                if (read == -1) break
                total += read
                windowBytes += read
                val now = System.nanoTime()
                if (now - windowStart >= SAMPLE_WINDOW_NANOS) {
                    onSample(toMbps(windowBytes, now - windowStart))
                    windowBytes = 0
                    windowStart = now
                }
            }
            toMbps(total, System.nanoTime() - start)
        }

    private suspend fun measureUpload(bytes: Int): Double {
        val body = ByteArray(bytes).toRequestBody("application/octet-stream".toMediaType())
        val start = System.nanoTime()
        api.upload(body).close()
        return toMbps(bytes.toLong(), System.nanoTime() - start)
    }

    /** bits per microsecond equals megabits per second. */
    private fun toMbps(bytes: Long, nanos: Long): Double = bytes * 8.0 / (nanos / 1_000.0)

    private fun List<Int>.median(): Int = sorted()[size / 2]

    /** Average change between consecutive probes: a simple measure of how steady the connection is. */
    private fun List<Int>.jitter(): Int = zipWithNext { a, b -> abs(a - b) }.average().toInt()

    private companion object {
        const val LATENCY_PROBES = 5
        const val BUFFER_SIZE = 64 * 1024
        const val SAMPLE_WINDOW_NANOS = 200_000_000L
    }
}