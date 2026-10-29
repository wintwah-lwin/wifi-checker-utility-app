package au.edu.jcu.assessment.utilityapp.domain.logic

import au.edu.jcu.assessment.utilityapp.domain.model.NetworkVerdict
import au.edu.jcu.assessment.utilityapp.domain.model.WifiInfo

class TipEngine {
    fun generateTips(verdict: NetworkVerdict, wifiInfo: WifiInfo): List<String> {
        val tips = mutableListOf<String>()

        when (verdict) {
            NetworkVerdict.OFFLINE -> {
                tips.add("Check if Wi-Fi is enabled on your phone settings.")
                tips.add("Ensure your router is powered on and cables are plugged in securely.")
            }
            NetworkVerdict.POOR -> {
                tips.add("Move closer to your Wi-Fi router to eliminate physical barriers.")
                tips.add("Restart your Wi-Fi router by unplugging it for 10 seconds.")
                tips.add("Disconnect unused background devices sharing the network.")
            }
            NetworkVerdict.FAIR -> {
                if (wifiInfo.frequencyMhz < 3000) {
                    tips.add("Switch from 2.4 GHz to 5 GHz Wi-Fi band if your router supports dual-band.")
                }
                tips.add("Close heavy bandwidth apps or background downloads.")
            }
            NetworkVerdict.GOOD, NetworkVerdict.EXCELLENT -> {
                tips.add("Your connection is performing optimal Wi-Fi speed and stability.")
                tips.add("Keep your router firmware updated for maximum performance.")
            }
        }

        return tips
    }
}
