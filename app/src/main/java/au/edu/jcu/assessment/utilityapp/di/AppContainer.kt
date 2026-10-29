package au.edu.jcu.assessment.utilityapp.di

import android.content.Context
import au.edu.jcu.assessment.utilityapp.data.device.ConnectionInfoProvider
import au.edu.jcu.assessment.utilityapp.data.repository.ConnectionRepository
import au.edu.jcu.assessment.utilityapp.data.repository.FakeConnectionRepository
import au.edu.jcu.assessment.utilityapp.data.repository.SettingsRepository
import au.edu.jcu.assessment.utilityapp.domain.logic.ConnectionAnalyzer

class AppContainer(private val context: Context) {

    val connectionInfoProvider: ConnectionInfoProvider by lazy {
        ConnectionInfoProvider(context)
    }

    val analyzer: ConnectionAnalyzer by lazy {
        ConnectionAnalyzer()
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository()
    }

    val connectionRepository: ConnectionRepository by lazy {
        FakeConnectionRepository(
            connectionInfoProvider = connectionInfoProvider
        )
    }
}
