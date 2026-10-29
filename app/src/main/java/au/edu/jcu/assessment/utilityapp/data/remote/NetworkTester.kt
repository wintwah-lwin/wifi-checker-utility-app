package au.edu.jcu.assessment.utilityapp.data.remote

import au.edu.jcu.assessment.utilityapp.domain.model.TestDepth
import au.edu.jcu.assessment.utilityapp.domain.model.TestEvent
import kotlinx.coroutines.flow.Flow

/** Runs a connection test. Implemented for real by Retrofit and by a fake for previews and tests. */
interface NetworkTester {
    fun run(depth: TestDepth): Flow<TestEvent>
}