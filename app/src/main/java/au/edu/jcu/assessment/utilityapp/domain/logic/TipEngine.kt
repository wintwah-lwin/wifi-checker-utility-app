package au.edu.jcu.assessment.utilityapp.domain.logic

import au.edu.jcu.assessment.utilityapp.domain.model.Grade
import au.edu.jcu.assessment.utilityapp.domain.model.LinkInfo
import au.edu.jcu.assessment.utilityapp.domain.model.LinkType
import au.edu.jcu.assessment.utilityapp.domain.model.Measurement
import au.edu.jcu.assessment.utilityapp.domain.model.Metric
import au.edu.jcu.assessment.utilityapp.domain.model.Tip
import au.edu.jcu.assessment.utilityapp.domain.model.TipKey
import au.edu.jcu.assessment.utilityapp.domain.model.Verdict

class TipEngine {
    fun generateTips(verdict: Verdict, linkInfo: LinkInfo, measurement: Measurement): List<Tip> {
        val tips = mutableListOf<Tip>()

        if (verdict.grade == Grade.GOOD) {
            tips.add(Tip(TipKey.ALL_GOOD))
            if (verdict.ratio > 2.0) {
                tips.add(Tip(TipKey.PLENTY_HEADROOM))
            }
            return tips
        }

        if (linkInfo.type == LinkType.WIFI && (linkInfo.signalDbm ?: -100) < -75) {
            tips.add(Tip(TipKey.MOVE_CLOSER))
        }

        when (verdict.limiting) {
            Metric.UPLOAD -> tips.add(Tip(TipKey.PAUSE_BACKUPS))
            Metric.DOWNLOAD -> tips.add(Tip(TipKey.PAUSE_DOWNLOADS))
            Metric.LATENCY -> tips.add(Tip(TipKey.RESTART_ROUTER))
            Metric.STABILITY -> tips.add(Tip(TipKey.UNSTEADY))
        }

        if (linkInfo.type == LinkType.CELLULAR) {
            tips.add(Tip(TipKey.SWITCH_TO_WIFI))
        }

        return tips.ifEmpty { listOf(Tip(TipKey.RUN_STANDARD)) }
    }
}
