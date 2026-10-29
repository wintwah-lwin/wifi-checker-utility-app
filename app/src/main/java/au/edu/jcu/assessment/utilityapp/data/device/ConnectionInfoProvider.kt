package au.edu.jcu.assessment.utilityapp.data.device

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import au.edu.jcu.assessment.utilityapp.domain.model.LinkInfo
import au.edu.jcu.assessment.utilityapp.domain.model.LinkType

class ConnectionInfoProvider(private val context: Context) {

    @Suppress("DEPRECATION")
    fun getLinkInfo(): LinkInfo {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

        val activeNetwork = connectivityManager?.activeNetwork
        val capabilities = connectivityManager?.getNetworkCapabilities(activeNetwork)

        if (capabilities == null) {
            return LinkInfo(type = LinkType.NONE)
        }

        val isWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        val isCellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        val isEthernet = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)

        val linkType = when {
            isWifi -> LinkType.WIFI
            isCellular -> LinkType.CELLULAR
            isEthernet -> LinkType.ETHERNET
            else -> LinkType.NONE
        }

        val wifiInfo = if (isWifi && wifiManager != null) wifiManager.connectionInfo else null
        val rawSsid = wifiInfo?.ssid?.replace("\"", "")
        val cleanSsid = if (rawSsid == "<unknown ssid>" || rawSsid.isNullOrBlank()) "Home Wi-Fi" else rawSsid
        val rssi = wifiInfo?.rssi ?: -65

        val isMetered = !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)

        return LinkInfo(
            type = linkType,
            name = if (isWifi) cleanSsid else if (isCellular) "Mobile Network" else "Ethernet",
            signalDbm = if (isWifi) rssi else null,
            metered = isMetered
        )
    }
}
