package au.edu.jcu.assessment.utilityapp.domain.logic

import au.edu.jcu.assessment.utilityapp.domain.model.Analysis
import au.edu.jcu.assessment.utilityapp.domain.model.Grade
import au.edu.jcu.assessment.utilityapp.domain.model.LinkInfo
import au.edu.jcu.assessment.utilityapp.domain.model.Measurement
import au.edu.jcu.assessment.utilityapp.domain.model.Settings
import au.edu.jcu.assessment.utilityapp.domain.model.Usage

class ConnectionAnalyzer(
    private val verdictCalculator: VerdictCalculator = VerdictCalculator(),
    private val diagnosisEngine: DiagnosisEngine = DiagnosisEngine(),
    private val tipEngine: TipEngine = TipEngine(),
    private val estimateEngine: EstimateEngine = EstimateEngine()
) {
    fun analyze(measurement: Measurement, linkInfo: LinkInfo, settings: Settings): Analysis {
        val verdict = verdictCalculator.calculateVerdict(measurement, settings.usage, settings.picky)
        val diagnosis = diagnosisEngine.diagnose(linkInfo, measurement)
        val tips = tipEngine.generateTips(verdict, linkInfo, measurement)
        val estimate = estimateEngine.estimate(measurement, settings.usage)

        val chips = Usage.entries.associateWith { u ->
            verdictCalculator.calculateVerdict(measurement, u, settings.picky).grade
        }

        return Analysis(
            verdict = verdict,
            diagnosis = diagnosis,
            tips = tips,
            estimate = estimate,
            chips = chips
        )
    }
}
