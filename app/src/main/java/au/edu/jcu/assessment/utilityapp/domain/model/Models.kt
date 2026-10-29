package au.edu.jcu.assessment.utilityapp.domain.model

import androidx.annotation.StringRes
import au.edu.jcu.assessment.utilityapp.R

enum class LinkType { WIFI, CELLULAR, ETHERNET, NONE }

/** What the phone knows about its current connection */
data class LinkInfo(
    val type: LinkType = LinkType.NONE,
    val name: String? = null,        // Wi-Fi name, only when location permission is granted
    val signalDbm: Int? = null,      // Wi-Fi signal strength (RSSI)
    val metered: Boolean = false
)

/** Result of one test run */
data class Measurement(
    val latencyMs: Int,
    val jitterMs: Int,
    val downMbps: Double?,
    val upMbps: Double?,
    val samples: List<Double> = emptyList()   // live download speeds
)

enum class Metric(@StringRes val label: Int) {
    DOWNLOAD(R.string.metric_download),
    UPLOAD(R.string.metric_upload),
    LATENCY(R.string.metric_latency),
    STABILITY(R.string.metric_stability)
}

enum class Grade { GOOD, STRUGGLES, POOR }

/** how good, and which metric holds the connection back. */
data class Verdict(val grade: Grade, val limiting: Metric, val ratio: Double)

enum class DiagnosisMessage(@StringRes val res: Int) {
    HEALTHY(R.string.diag_healthy),
    WEAK_WIFI(R.string.diag_weak_wifi),
    SLOW_INTERNET(R.string.diag_slow_internet),
    WEAK_MOBILE(R.string.diag_weak_mobile),
    MOBILE_OK(R.string.diag_mobile_ok)
}

/** Where the weakest link is */
data class Diagnosis(val localOk: Boolean?, val internetOk: Boolean, val message: DiagnosisMessage)

enum class TipKey(@StringRes val res: Int) {
    RUN_STANDARD(R.string.tip_run_standard),
    MOVE_CLOSER(R.string.tip_move_closer),
    RESTART_ROUTER(R.string.tip_restart_router),
    PAUSE_BACKUPS(R.string.tip_pause_backups),
    LOWER_QUALITY(R.string.tip_lower_quality),
    PAUSE_DOWNLOADS(R.string.tip_pause_downloads),
    USE_CABLE(R.string.tip_use_cable),
    UNSTEADY(R.string.tip_unsteady),
    SWITCH_TO_WIFI(R.string.tip_switch_wifi),
    PLENTY_HEADROOM(R.string.tip_plenty),
    ALL_GOOD(R.string.tip_all_good)
}

data class Tip(val key: TipKey, val arg: String? = null)

enum class EstimateKey(@StringRes val res: Int) {
    BEST_QUALITY(R.string.est_best_quality),
    TOO_SLOW(R.string.est_too_slow),
    DOWNLOAD_TIME(R.string.est_download_time),
    UPLOAD_TIME(R.string.est_upload_time),
    SAFE_BITRATE(R.string.est_safe_bitrate),
    GROUP_VIDEO(R.string.est_group_video),
    ONE_TO_ONE(R.string.est_one_to_one),
    AUDIO_ONLY(R.string.est_audio_only)
}

/** A concrete number for the chosen activity */
data class Estimate(val key: EstimateKey, val arg: String? = null)

/** UI needs after a test */
data class Analysis(
    val verdict: Verdict,
    val diagnosis: Diagnosis,
    val tips: List<Tip>,
    val estimate: Estimate?,
    val chips: Map<Usage, Grade>      // how the same result grades for every activity
)