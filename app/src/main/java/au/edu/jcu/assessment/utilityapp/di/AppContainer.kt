package au.edu.jcu.assessment.utilityapp.di

import android.content.Context
import au.edu.jcu.assessment.utilityapp.data.device.ConnectionInfoProvider
import au.edu.jcu.assessment.utilityapp.data.repository.ConnectionRepository
import au.edu.jcu.assessment.utilityapp.data.repository.FakeConnectionRepository
import au.edu.jcu.assessment.utilityapp.data.repository.SettingsRepository

class AppContainer(private val context: Context) {

    val connectionInfoProvider: ConnectionInfoProvider by lazy {
        ConnectionInfoProvider(context)
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
