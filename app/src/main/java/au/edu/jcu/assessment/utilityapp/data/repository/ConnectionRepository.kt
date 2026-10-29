package au.edu.jcu.assessment.utilityapp.data.repository

import au.edu.jcu.assessment.utilityapp.data.device.ConnectionInfoProvider
import au.edu.jcu.assessment.utilityapp.data.remote.NetworkTester
import au.edu.jcu.assessment.utilityapp.domain.model.ErrorReason
import au.edu.jcu.assessment.utilityapp.domain.model.LinkInfo
import au.edu.jcu.assessment.utilityapp.domain.model.TestDepth
import au.edu.jcu.assessment.utilityapp.domain.model.TestEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

interface ConnectionRepository {
    val link: Flow<LinkInfo>
    fun refreshLink()
    fun runTest(depth: TestDepth): Flow<TestEvent>
}

class DefaultConnectionRepository(
    private val tester: NetworkTester,
    private val linkProvider: ConnectionInfoProvider
) : ConnectionRepository {

    override val link: Flow<LinkInfo> = linkProvider.observe()

    override fun refreshLink() = linkProvider.refresh()

    override fun runTest(depth: TestDepth): Flow<TestEvent> =
        tester.run(depth).catch { emit(TestEvent.Failed(it.toReason())) }

    /** Order matters: SocketTimeoutException is also an IOException. */
    private fun Throwable.toReason(): ErrorReason = when (this) {
        is SocketTimeoutException -> ErrorReason.TIMEOUT
        is HttpException -> ErrorReason.SERVER
        is IOException -> ErrorReason.NO_NETWORK
        else -> throw this
    }
}