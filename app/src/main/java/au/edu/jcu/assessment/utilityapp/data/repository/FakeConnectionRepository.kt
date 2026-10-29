package au.edu.jcu.assessment.utilityapp.data.repository

import au.edu.jcu.assessment.utilityapp.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeConnectionRepository(
    private val fakeLink: LinkInfo = LinkInfo(LinkType.WIFI, "Home Wi-Fi", signalDbm = -48),
    private val events: List<TestEvent> = listOf(
        TestEvent.Sample(80.0),
        TestEvent.Finished(Measurement(latencyMs = 18, jitterMs = 4, downMbps = 180.0, upMbps = 60.0))
    )
) : ConnectionRepository {
    override val link: Flow<LinkInfo> = flowOf(fakeLink)
    override fun refreshLink() = Unit
    override fun runTest(depth: TestDepth): Flow<TestEvent> = kotlinx.coroutines.flow.flow { events.forEach { emit(it) } }
}