package au.edu.jcu.assessment.utilityapp.di

import android.content.Context
import au.edu.jcu.assessment.utilityapp.data.device.ConnectionInfoProvider
import au.edu.jcu.assessment.utilityapp.data.remote.RetrofitNetworkTester
import au.edu.jcu.assessment.utilityapp.data.remote.SpeedApi
import au.edu.jcu.assessment.utilityapp.data.repository.ConnectionRepository
import au.edu.jcu.assessment.utilityapp.data.repository.DefaultConnectionRepository
import au.edu.jcu.assessment.utilityapp.data.repository.SettingsRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/** Manual dependency injection: builds each dependency once and shares it. */
class AppContainer(context: Context) {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)     // a stuck test ends in a TIMEOUT error, not a hang
        .build()

    private val speedApi: SpeedApi = Retrofit.Builder()
        .baseUrl("https://speed.cloudflare.com/")
        .client(httpClient)
        .build()
        .create(SpeedApi::class.java)

    val settingsRepository = SettingsRepository()

    val connectionRepository: ConnectionRepository = DefaultConnectionRepository(
        tester = RetrofitNetworkTester(speedApi),
        linkProvider = ConnectionInfoProvider(context)
    )
}