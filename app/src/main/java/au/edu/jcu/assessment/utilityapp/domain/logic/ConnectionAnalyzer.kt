package au.edu.jcu.assessment.utilityapp.domain.logic

import au.edu.jcu.assessment.utilityapp.domain.model.AnalysisReport
import au.edu.jcu.assessment.utilityapp.domain.model.ConnectionMetrics
import au.edu.jcu.assessment.utilityapp.domain.model.WifiInfo

class ConnectionAnalyzer(
    private val verdictCalculator: VerdictCalculator = VerdictCalculator(),
    private val diagnosisEngine: DiagnosisEngine = DiagnosisEngine(),
    private val tipEngine: TipEngine = TipEngine(),
    private val estimateEngine: EstimateEngine = EstimateEngine()
) {
    fun analyze(metrics: ConnectionMetrics, wifiInfo: WifiInfo): AnalysisReport {
        val verdict = verdictCalculator.calculateVerdict(metrics, wifiInfo.isConnected)
        val diagnosisList = diagnosisEngine.diagnose(metrics, wifiInfo)
        val tipsList = tipEngine.generateTips(verdict, wifiInfo)
        val estimates = estimateEngine.estimate(metrics)

        return AnalysisReport(
            verdict = verdict,
            metrics = metrics,
            wifiInfo = wifiInfo,
            diagnosisList = diagnosisList,
            tipsList = tipsList,
            estimates = estimates
        )
    }
}
