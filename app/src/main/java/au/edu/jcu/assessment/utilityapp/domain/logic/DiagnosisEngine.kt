package au.edu.jcu.assessment.utilityapp.domain.logic

import au.edu.jcu.assessment.utilityapp.domain.model.*

/** Walks phone, router, internet and names the weakest link. */
object DiagnosisEngine {
    private const val WEAK_SIGNAL_DBM = -70
    private const val SLOW_INTERNET_MS = 120

    fun diagnose(link: LinkInfo, m: Measurement): Diagnosis {
        val internetOk = m.latencyMs <= SLOW_INTERNET_MS
        if (link.type == LinkType.CELLULAR) {
            val msg = if (internetOk) DiagnosisMessage.MOBILE_OK else DiagnosisMessage.WEAK_MOBILE
            return Diagnosis(localOk = null, internetOk = internetOk, message = msg)
        }
        // Signal strength may be unknown, in which case we cannot blame the Wi-Fi.
        val localOk = link.signalDbm?.let { it >= WEAK_SIGNAL_DBM }
        val msg = when {
            localOk == false -> DiagnosisMessage.WEAK_WIFI
            !internetOk -> DiagnosisMessage.SLOW_INTERNET
            else -> DiagnosisMessage.HEALTHY
        }
        return Diagnosis(localOk, internetOk, msg)
    }
}