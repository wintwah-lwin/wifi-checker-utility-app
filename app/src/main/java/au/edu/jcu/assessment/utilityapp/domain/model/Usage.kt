package au.edu.jcu.assessment.utilityapp.domain.model

import androidx.annotation.StringRes
import au.edu.jcu.assessment.utilityapp.R

/** Minimum network quality an activity needs to be smooth */
data class Needs(
    val downMbps: Double,
    val upMbps: Double,
    val maxLatencyMs: Int,
    val maxJitterMs: Int
)

/** Everything a person might be doing online */
enum class Usage(
    @StringRes val label: Int,
    @StringRes val phrase: Int,
    val needs: Needs
) {
    CALL(R.string.usage_call, R.string.phrase_call, Needs(3.0, 2.0, 150, 30)),
    MEETING(R.string.usage_meeting, R.string.phrase_meeting, Needs(6.0, 4.0, 150, 30)),
    LIVE(R.string.usage_live, R.string.phrase_live, Needs(5.0, 8.0, 150, 20)),
    MUSIC(R.string.usage_music, R.string.phrase_music, Needs(1.0, 0.1, 400, 200)),
    HD(R.string.usage_hd, R.string.phrase_hd, Needs(8.0, 0.5, 300, 60)),
    UHD(R.string.usage_uhd, R.string.phrase_uhd, Needs(25.0, 0.5, 300, 60)),
    GAME(R.string.usage_game, R.string.phrase_game, Needs(5.0, 2.0, 50, 15)),
    CLOUD(R.string.usage_cloud, R.string.phrase_cloud, Needs(35.0, 5.0, 40, 10)),
    WEB(R.string.usage_web, R.string.phrase_web, Needs(3.0, 1.0, 300, 100)),
    WORK(R.string.usage_work, R.string.phrase_work, Needs(10.0, 20.0, 300, 100)),
    DOWNLOAD(R.string.usage_download, R.string.phrase_download, Needs(50.0, 1.0, 400, 200))
}