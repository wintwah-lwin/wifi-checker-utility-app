package au.edu.jcu.assessment.utilityapp

import org.junit.Test
import org.junit.Assert.*
import au.edu.jcu.assessment.utilityapp.domain.logic.*
import au.edu.jcu.assessment.utilityapp.domain.model.*

class VerdictCalculatorTest {
    private val fast = Measurement(latencyMs = 18, jitterMs = 4, downMbps = 180.0, upMbps = 60.0)

    @Test
    fun `fast connection is good for video calls`() {
        assertEquals(Grade.GOOD, VerdictCalculator.grade(fast, Usage.CALL, Picky.NORMAL).grade)
    }

    @Test
    fun `slow upload is the limit for live streaming`() {
        val verdict = VerdictCalculator.grade(fast.copy(upMbps = 0.8), Usage.LIVE, Picky.NORMAL)
        assertEquals(Metric.UPLOAD, verdict.limiting)
        assertEquals(Grade.POOR, verdict.grade)
    }

    @Test
    fun `strict mode asks for more headroom`() {
        val m = Measurement(latencyMs = 120, jitterMs = 10, downMbps = 4.0, upMbps = 2.5)
        assertEquals(Grade.GOOD, VerdictCalculator.grade(m, Usage.CALL, Picky.NORMAL).grade)
        assertEquals(Grade.STRUGGLES, VerdictCalculator.grade(m, Usage.CALL, Picky.STRICT).grade)
    }

    @Test
    fun `quick test is graded on latency and stability only`() {
        val quick = Measurement(latencyMs = 30, jitterMs = 5, downMbps = null, upMbps = null)
        val verdict = VerdictCalculator.grade(quick, Usage.GAME, Picky.NORMAL)
        assertEquals(Grade.GOOD, verdict.grade)
        assertEquals(Metric.LATENCY, verdict.limiting)
    }
}

class DiagnosisEngineTest {
    private val m = Measurement(latencyMs = 20, jitterMs = 4, downMbps = 100.0, upMbps = 40.0)

    @Test
    fun `weak signal blames the wifi`() {
        val d = DiagnosisEngine.diagnose(LinkInfo(LinkType.WIFI, signalDbm = -75), m)
        assertEquals(DiagnosisMessage.WEAK_WIFI, d.message)
    }

    @Test
    fun `strong signal but slow responses blames the internet`() {
        val d = DiagnosisEngine.diagnose(LinkInfo(LinkType.WIFI, signalDbm = -50), m.copy(latencyMs = 150))
        assertEquals(DiagnosisMessage.SLOW_INTERNET, d.message)
    }

    @Test
    fun `unknown signal never blames the wifi`() {
        val d = DiagnosisEngine.diagnose(LinkInfo(LinkType.WIFI, signalDbm = null), m)
        assertEquals(DiagnosisMessage.HEALTHY, d.message)
    }

    @Test
    fun `mobile data has no router leg`() {
        val d = DiagnosisEngine.diagnose(LinkInfo(LinkType.CELLULAR), m)
        assertNull(d.localOk)
    }
}

class TipEngineTest {
    private fun tips(m: Measurement, link: LinkInfo, s: Settings): List<Tip> {
        val v = VerdictCalculator.grade(m, s.usage, s.picky)
        return TipEngine.tips(m, link, DiagnosisEngine.diagnose(link, m), v, s)
    }

    @Test
    fun `weak wifi is named before anything else`() {
        val result = tips(Measurement(85, 25, 9.0, 2.5), LinkInfo(LinkType.WIFI, signalDbm = -74), Settings(usage = Usage.HD))
        assertEquals(TipKey.MOVE_CLOSER, result.first().key)
    }

    @Test
    fun `video limited by download suggests a lower quality`() {
        val result = tips(Measurement(20, 4, 6.0, 5.0), LinkInfo(LinkType.WIFI, signalDbm = -50), Settings(usage = Usage.HD))
        assertEquals(Tip(TipKey.LOWER_QUALITY, "720p"), result.first())
    }
}
