package au.edu.jcu.assessment.utilityapp.data.repository

import au.edu.jcu.assessment.utilityapp.domain.model.TestEvent
import au.edu.jcu.assessment.utilityapp.domain.model.WifiInfo
import kotlinx.coroutines.flow.Flow

interface ConnectionRepository {
    fun getWifiInfo(): WifiInfo
    fun runSpeedTest(): Flow<TestEvent>
}
