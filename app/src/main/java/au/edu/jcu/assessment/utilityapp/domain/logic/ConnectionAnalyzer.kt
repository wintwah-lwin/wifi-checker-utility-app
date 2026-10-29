package au.edu.jcu.assessment.utilityapp.domain.logic

import au.edu.jcu.assessment.utilityapp.domain.model.*

/** Single entry point the ViewModel calls after a test or when a setting changes. */
object ConnectionAnalyzer {
    fun analyze(m: Measurement, link: LinkInfo, s: Settings): Analysis {
        val verdict = VerdictCalculator.grade(m, s.usage, s.picky)
        val diagnosis = DiagnosisEngine.diagnose(link, m)
        return Analysis(
            verdict = verdict,
            diagnosis = diagnosis,
            tips = TipEngine.tips(m, link, diagnosis, verdict, s),
            estimate = EstimateEngine.estimate(m, s),
            chips = Usage.entries.associateWith { VerdictCalculator.grade(m, it, s.picky).grade }
        )
    }
}