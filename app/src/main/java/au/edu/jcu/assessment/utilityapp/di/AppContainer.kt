package au.edu.jcu.assessment.utilityapp.di

import android.content.Context
import au.edu.jcu.assessment.utilityapp.data.device.ConnectionInfoProvider
import au.edu.jcu.assessment.utilityapp.data.remote.RetrofitNetworkTester
import au.edu.jcu.assessment.utilityapp.data.remote.SpeedApi
import au.edu.jcu.assessment.utilityapp.data.repository.ConnectionRepository
import au.edu.jcu.assessment.utilityapp.data.repository.DefaultConnectionRepository
import au.edu.jcu.assessment.utilityapp.data.repository.SettingsRepository
import retrofit2.Retrofit

class AppContainer(private val context: Context) {

    val connectionInfoProvider: ConnectionInfoProvider by lazy {
        ConnectionInfoProvider(context)
    }

    val speedApi: SpeedApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://speed.cloudflare.com/")
            .build()
            .create(SpeedApi::class.java)
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository()
    }

    val connectionRepository: ConnectionRepository by lazy {
        DefaultConnectionRepository(
            tester = RetrofitNetworkTester(speedApi),
            linkProvider = connectionInfoProvider
        )
    }
}
