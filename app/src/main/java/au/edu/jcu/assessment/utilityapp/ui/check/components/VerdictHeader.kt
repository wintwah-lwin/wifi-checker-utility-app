package au.edu.jcu.assessment.utilityapp.ui.check.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import au.edu.jcu.assessment.utilityapp.R
import au.edu.jcu.assessment.utilityapp.domain.model.*
import au.edu.jcu.assessment.utilityapp.ui.check.TestState
import au.edu.jcu.assessment.utilityapp.ui.check.UtilityUiState
import au.edu.jcu.assessment.utilityapp.ui.theme.color
import au.edu.jcu.assessment.utilityapp.ui.theme.verdictColors

/** Headline and one-line reason. AnimatedContent cross-fades when the verdict changes. */
@Composable
fun VerdictHeader(state: UtilityUiState, modifier: Modifier = Modifier) {
    val phrase = stringResource(state.settings.usage.phrase)
    val primary = MaterialTheme.colorScheme.primary
    val muted = MaterialTheme.colorScheme.onSurfaceVariant
    val (title, subtitle, color) = when (val test = state.test) {
        is TestState.Running -> Triple(stringResource(R.string.testing), stringResource(R.string.testing_sub), primary)
        is TestState.Failed -> when (test.reason) {
            ErrorReason.TIMEOUT -> Triple(stringResource(R.string.error_timeout), stringResource(R.string.error_timeout_sub), verdictColors().bad)
            ErrorReason.SERVER -> Triple(stringResource(R.string.error_server), stringResource(R.string.error_server_sub), verdictColors().bad)
            ErrorReason.NO_NETWORK -> Triple(stringResource(R.string.no_connection), stringResource(R.string.no_connection_sub), muted)
        }
        is TestState.Done -> state.analysis?.verdict?.let { v ->
            val metric = stringResource(v.limiting.label)
            Triple(
                stringResource(when (v.grade) { Grade.GOOD -> R.string.verdict_good; Grade.STRUGGLES -> R.string.verdict_struggles; Grade.POOR -> R.string.verdict_poor }, phrase),
                when (v.grade) {
                    Grade.GOOD -> if (v.ratio < 1.3) stringResource(R.string.reason_tight, metric.lowercase()) else stringResource(R.string.reason_room)
                    Grade.STRUGGLES -> stringResource(R.string.reason_low, metric)
                    Grade.POOR -> stringResource(R.string.reason_problem, metric)
                },
                v.grade.color()
            )
        } ?: Triple("", "", muted)
        TestState.Idle -> if (state.link.type == LinkType.NONE)
            Triple(stringResource(R.string.no_connection), stringResource(R.string.no_connection_sub), muted)
        else Triple(stringResource(R.string.ready_title), stringResource(R.string.ready_sub, phrase), muted)
    }
    AnimatedContent(targetState = title to subtitle, modifier = modifier, label = "verdict") { (t, s) ->
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(t, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = color, textAlign = TextAlign.Center)
            Text(s, style = MaterialTheme.typography.bodyMedium, color = muted, textAlign = TextAlign.Center)
        }
    }
}
