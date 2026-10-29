package au.edu.jcu.assessment.utilityapp.domain.logic

import au.edu.jcu.assessment.utilityapp.domain.model.*

/** Rule-based suggestions, most useful first. Returns at most three. */
object TipEngine {
    private val videoUsages = setOf(Usage.HD, Usage.UHD)
    private const val MAX_TIPS = 3
    private const val PLENTY_RATIO = 2.0

    fun tips(m: Measurement, link: LinkInfo, d: Diagnosis, v: Verdict, s: Settings): List<Tip> = buildList {
        if (m.downMbps == null) add(Tip(TipKey.RUN_STANDARD))

        // Name the weakest link first, because fixing it helps every activity.
        if (d.localOk == false) add(Tip(TipKey.MOVE_CLOSER))
        else if (!d.internetOk) add(Tip(TipKey.RESTART_ROUTER))

        // Then address the metric that limits this particular activity.
        if (v.grade != Grade.GOOD) {
            when (v.limiting) {
                Metric.UPLOAD -> add(Tip(TipKey.PAUSE_BACKUPS))
                Metric.DOWNLOAD -> add(
                    if (s.usage in videoUsages) Tip(TipKey.LOWER_QUALITY, EstimateEngine.bestQuality(m, s.picky) ?: "480p")
                    else Tip(TipKey.PAUSE_DOWNLOADS)
                )
                Metric.LATENCY -> add(Tip(TipKey.USE_CABLE))
                Metric.STABILITY -> add(Tip(TipKey.UNSTEADY))
            }
        }
        if (link.type == LinkType.CELLULAR || link.metered) add(Tip(TipKey.SWITCH_TO_WIFI))
        if (isEmpty()) add(Tip(if (v.ratio >= PLENTY_RATIO) TipKey.PLENTY_HEADROOM else TipKey.ALL_GOOD))
    }.take(MAX_TIPS)
}