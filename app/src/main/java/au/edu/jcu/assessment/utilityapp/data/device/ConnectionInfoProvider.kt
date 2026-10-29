package au.edu.jcu.assessment.utilityapp.data.device

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat
import au.edu.jcu.assessment.utilityapp.domain.model.LinkInfo
import au.edu.jcu.assessment.utilityapp.domain.model.LinkType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/** Reads the phone's current connection and reports changes as they happen. */
class ConnectionInfoProvider(context: Context) {
    private val appContext = context.applicationContext
    private val connectivity = requireNotNull(appContext.getSystemService(ConnectivityManager::class.java))
    private val wifi = appContext.getSystemService(WifiManager::class.java)
    private val refreshRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /** Emits the current link now, then again whenever the network or its capabilities change. */
    fun observe(): Flow<LinkInfo> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { trySend(current()) }
            override fun onLost(network: Network) { trySend(LinkInfo()) }
            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                trySend(current())
            }
        }
        connectivity.registerDefaultNetworkCallback(callback)
        val refresher = launch { refreshRequests.collect { trySend(current()) } }
        trySend(current())
        awaitClose {
            connectivity.unregisterNetworkCallback(callback)   // avoid leaking the callback
            refresher.cancel()
        }
    }.distinctUntilChanged()

    /** Call after the location permission result so the Wi-Fi name can appear. */
    fun refresh() { refreshRequests.tryEmit(Unit) }

    private fun current(): LinkInfo {
        val caps = connectivity.getNetworkCapabilities(connectivity.activeNetwork) ?: return LinkInfo()
        val type = when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> LinkType.WIFI
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> LinkType.CELLULAR
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> LinkType.ETHERNET
            else -> LinkType.NONE
        }
        val isWifi = type == LinkType.WIFI
        return LinkInfo(
            type = type,
            name = if (isWifi) wifiName() else null,
            signalDbm = if (isWifi) wifiSignal() else null,
            metered = !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        )
    }

    /** Signal strength needs no runtime permission. -127 means "unknown". */
    @Suppress("DEPRECATION")
    private fun wifiSignal(): Int? = wifi?.connectionInfo?.rssi?.takeIf { it > -127 }

    /** The network name is only readable with location permission, so it is optional. */
    @Suppress("DEPRECATION")
    private fun wifiName(): String? {
        val granted = ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        if (!granted) return null
        val ssid = wifi?.connectionInfo?.ssid?.trim('"')
        return ssid?.takeIf { it.isNotBlank() && it != "<unknown ssid>" }
    }
}