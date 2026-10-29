package au.edu.jcu.assessment.utilityapp.data.repository

import au.edu.jcu.assessment.utilityapp.data.device.ConnectionInfoProvider
import au.edu.jcu.assessment.utilityapp.data.remote.NetworkTester
import au.edu.jcu.assessment.utilityapp.domain.logic.ConnectionAnalyzer
import au.edu.jcu.assessment.utilityapp.domain.model.ConnectionMetrics
import au.edu.jcu.assessment.utilityapp.domain.model.TestEvent
import au.edu.jcu.assessment.utilityapp.domain.model.TestStage
import au.edu.jcu.assessment.utilityapp.domain.model.WifiInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class FakeConnectionRepository(
    private val connectionInfoProvider: ConnectionInfoProvider,
    private val networkTester: NetworkTester,
    private val analyzer: ConnectionAnalyzer = ConnectionAnalyzer(),
    private val settingsRepository: SettingsRepository? = null
) : ConnectionRepository {

    override fun getWifiInfo(): WifiInfo {
        return connectionInfoProvider.getWifiInfo()
    }

    override fun runSpeedTest(): Flow<TestEvent> = flow {
        emit(TestEvent.Started(TestStage.PING))
        val wifiInfo = getWifiInfo()

        val pingMs = networkTester.measurePing("https://speed.cloudflare.com/")
        emit(TestEvent.PingUpdated(pingMs))
        delay(200)

        emit(TestEvent.Started(TestStage.DOWNLOAD))
        val finalDownload = networkTester.measureDownloadSpeed("https://speed.cloudflare.com/") { _, _ -> }
        emit(TestEvent.DownloadUpdated(finalDownload, 1.0f))
        delay(200)

        emit(TestEvent.Started(TestStage.UPLOAD))
        val finalUpload = networkTester.measureUploadSpeed { _, _ -> }
        emit(TestEvent.UploadUpdated(finalUpload, 1.0f))
        delay(200)

        emit(TestEvent.Started(TestStage.DIAGNOSIS))
        val metrics = ConnectionMetrics(
            downloadSpeedMbps = finalDownload,
            uploadSpeedMbps = finalUpload,
            pingMs = pingMs,
            jitterMs = Random.nextLong(2, 12),
            signalStrengthDbm = wifiInfo.rssi,
            ssid = wifiInfo.ssid,
            ipAddress = "192.168.1." + Random.nextInt(2, 254),
            isWifi = wifiInfo.isConnected
        )

        val report = analyzer.analyze(metrics, wifiInfo)
        settingsRepository?.recordTestRun(finalDownload)

        emit(TestEvent.Completed(report))
    }
}
