package au.edu.jcu.assessment.utilityapp.data.repository

import au.edu.jcu.assessment.utilityapp.data.device.ConnectionInfoProvider
import au.edu.jcu.assessment.utilityapp.domain.model.LinkInfo
import au.edu.jcu.assessment.utilityapp.domain.model.Measurement
import au.edu.jcu.assessment.utilityapp.domain.model.TestDepth
import au.edu.jcu.assessment.utilityapp.domain.model.TestEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class FakeConnectionRepository(
    private val connectionInfoProvider: ConnectionInfoProvider
) : ConnectionRepository {

    override fun getLinkInfo(): LinkInfo {
        return connectionInfoProvider.getLinkInfo()
    }

    override fun runSpeedTest(depth: TestDepth): Flow<TestEvent> = flow {
        val samples = mutableListOf<Double>()
        val targetSpeed = Random.nextDouble(25.0, 85.0)

        val sampleCount = when (depth) {
            TestDepth.QUICK -> 3
            TestDepth.STANDARD -> 8
            TestDepth.THOROUGH -> 15
        }

        for (i in 1..sampleCount) {
            delay(150)
            val sampleMbps = (targetSpeed * i / sampleCount) + Random.nextDouble(-2.0, 2.0)
            val currentMbps = sampleMbps.coerceAtLeast(1.0)
            samples.add(currentMbps)
            emit(TestEvent.Sample(currentMbps))
        }

        val finalDown = targetSpeed
        val finalUp = targetSpeed * 0.4
        val measurement = Measurement(
            latencyMs = Random.nextInt(15, 45),
            jitterMs = Random.nextInt(2, 10),
            downMbps = finalDown,
            upMbps = finalUp,
            samples = samples
        )

        emit(TestEvent.Finished(measurement))
    }
}
