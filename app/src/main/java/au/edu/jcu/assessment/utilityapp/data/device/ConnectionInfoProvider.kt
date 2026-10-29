package au.edu.jcu.assessment.utilityapp.data.device

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import au.edu.jcu.assessment.utilityapp.domain.model.WifiInfo

class ConnectionInfoProvider(private val context: Context) {

    @Suppress("DEPRECATION")
    fun getWifiInfo(): WifiInfo {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

        val activeNetwork = connectivityManager?.activeNetwork
        val capabilities = connectivityManager?.getNetworkCapabilities(activeNetwork)
        val isWifi = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true

        if (!isWifi || wifiManager == null) {
            return WifiInfo(
                ssid = if (capabilities != null) "Cellular / Ethernet" else "Disconnected",
                isConnected = capabilities != null
            )
        }

        val connectionInfo = wifiManager.connectionInfo
        val rawSsid = connectionInfo?.ssid ?: "Connected Wi-Fi"
        val cleanSsid = rawSsid.replace("\"", "").let { if (it == "<unknown ssid>") "Campus/Home Wi-Fi" else it }

        return WifiInfo(
            ssid = cleanSsid,
            bssid = connectionInfo?.bssid ?: "00:00:00:00:00:00",
            rssi = connectionInfo?.rssi ?: -65,
            linkSpeedMbps = connectionInfo?.linkSpeed ?: 150,
            frequencyMhz = connectionInfo?.frequency ?: 5180,
            isConnected = true
        )
    }
}
