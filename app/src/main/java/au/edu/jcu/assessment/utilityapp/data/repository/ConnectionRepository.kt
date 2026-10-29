package au.edu.jcu.assessment.utilityapp.data.repository

import au.edu.jcu.assessment.utilityapp.domain.model.LinkInfo
import au.edu.jcu.assessment.utilityapp.domain.model.TestDepth
import au.edu.jcu.assessment.utilityapp.domain.model.TestEvent
import kotlinx.coroutines.flow.Flow

interface ConnectionRepository {
    fun getLinkInfo(): LinkInfo
    fun runSpeedTest(depth: TestDepth): Flow<TestEvent>
}
