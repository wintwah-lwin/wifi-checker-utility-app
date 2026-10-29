package au.edu.jcu.assessment.utilityapp.domain.logic

import au.edu.jcu.assessment.utilityapp.domain.model.Diagnosis
import au.edu.jcu.assessment.utilityapp.domain.model.DiagnosisMessage
import au.edu.jcu.assessment.utilityapp.domain.model.LinkInfo
import au.edu.jcu.assessment.utilityapp.domain.model.LinkType
import au.edu.jcu.assessment.utilityapp.domain.model.Measurement

class DiagnosisEngine {
    fun diagnose(linkInfo: LinkInfo, measurement: Measurement): Diagnosis {
        val localOk = when (linkInfo.type) {
            LinkType.WIFI -> (linkInfo.signalDbm ?: -100) > -75
            LinkType.CELLULAR -> (linkInfo.signalDbm ?: -100) > -105
            else -> true
        }

        val internetOk = (measurement.downMbps ?: 0.0) >= 5.0 && measurement.latencyMs < 150

        val msg = when {
            localOk && internetOk -> if (linkInfo.type == LinkType.CELLULAR) DiagnosisMessage.MOBILE_OK else DiagnosisMessage.HEALTHY
            !localOk && linkInfo.type == LinkType.WIFI -> DiagnosisMessage.WEAK_WIFI
            !localOk && linkInfo.type == LinkType.CELLULAR -> DiagnosisMessage.WEAK_MOBILE
            else -> DiagnosisMessage.SLOW_INTERNET
        }

        return Diagnosis(localOk = localOk, internetOk = internetOk, message = msg)
    }
}
