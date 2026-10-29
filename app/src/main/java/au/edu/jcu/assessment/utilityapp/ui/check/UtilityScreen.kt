package au.edu.jcu.assessment.utilityapp.ui.check

import android.Manifest
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.jcu.assessment.utilityapp.R
import au.edu.jcu.assessment.utilityapp.domain.logic.ConnectionAnalyzer
import au.edu.jcu.assessment.utilityapp.domain.model.*
import au.edu.jcu.assessment.utilityapp.ui.formatSpeed
import au.edu.jcu.assessment.utilityapp.ui.speedFraction
import au.edu.jcu.assessment.utilityapp.ui.check.components.*
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme
import au.edu.jcu.assessment.utilityapp.ui.theme.color

/** Stateful wrapper: owns the ViewModel, the permission launcher and the haptic effect. */
@Composable
fun UtilityScreen(viewModel: UtilityViewModel = viewModel(factory = UtilityViewModel.Factory)) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        viewModel.onLocationPermissionResult()   // granted or denied, re-read the link
    }

    // Vibrate once when a test finishes, if the user left haptics on.
    LaunchedEffect(state.test) {
        if (state.test is TestState.Done && state.settings.haptics) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    UtilityContent(
        state = state,
        onRunTest = viewModel::startTest,
        onSelectUsage = viewModel::selectUsage,
        onAllowLocation = { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
    )
}

private data class GaugeModel(val fill: Float, val tick: Float?, val text: String, val unit: String, val color: Color)

/** Stateless: everything it draws comes from [state], which makes it trivial to preview. */
@Composable
fun UtilityContent(
    state: UtilityUiState,
    onRunTest: () -> Unit,
    onSelectUsage: (Usage) -> Unit,
    onAllowLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings = state.settings
    val analysis = state.analysis
    val done = state.test as? TestState.Done
    val running = state.test is TestState.Running
    val verdictColor = analysis?.verdict?.grade.color()
    val needMbps = settings.usage.needs.downMbps * settings.picky.scale
    val unit = stringResource(if (settings.useMbps) R.string.units_mbps else R.string.units_mbs)

    val gauge = when (val test = state.test) {
        is TestState.Running -> GaugeModel(speedFraction(test.liveMbps), speedFraction(needMbps),
            formatSpeed(test.liveMbps, settings.useMbps), "↓ $unit", MaterialTheme.colorScheme.primary)
        is TestState.Done -> test.measurement.downMbps?.let {
            GaugeModel(speedFraction(it), speedFraction(needMbps), formatSpeed(it, settings.useMbps), "↓ $unit", verdictColor)
        } ?: GaugeModel(1f - (test.measurement.latencyMs / 300f).coerceIn(0f, 1f), null,   // quick test: no speed
            "${test.measurement.latencyMs}", stringResource(R.string.unit_ms), verdictColor)
        else -> GaugeModel(0f, null, "--", "↓ $unit", MaterialTheme.colorScheme.onSurfaceVariant)
    }

    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ConnectionHeader(state.link)
        if (state.link.type == LinkType.WIFI && state.link.name == null) {
            InfoBanner(R.string.allow_location_msg, R.string.allow, onAllowLocation)
        }
        if (state.stale) InfoBanner(R.string.stale_msg)
        UsageChips(settings.usage, analysis?.chips.orEmpty(), onSelectUsage)

        // Dim the old result if the phone has moved to another network since the test.
        Column(
            Modifier.alpha(if (state.stale) 0.4f else 1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SpeedGauge(gauge.fill, gauge.tick, gauge.text, gauge.unit, gauge.color)
            VerdictHeader(state)
            analysis?.estimate?.let { EstimateCard(it) }
            analysis?.let { TipsCard(it.tips) }
            (state.test as? TestState.Running)?.let { SparkLine(it.samples) }
            done?.let { SparkLine(it.measurement.samples) }
            if (analysis != null && done != null) {
                PathCard(done.testedOn, analysis.diagnosis, done.measurement)
                StatsRow(done.measurement, settings.useMbps)
            }
        }

        Button(onClick = onRunTest, enabled = !running, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(if (running) R.string.testing else R.string.test_again))
        }
    }
}

private val sample = Measurement(18, 4, 180.0, 60.0, samples = listOf(40.0, 90.0, 140.0, 175.0, 180.0))
private val homeWifi = LinkInfo(LinkType.WIFI, "Home Wi-Fi", signalDbm = -48)

private fun previewState(test: TestState, link: LinkInfo = homeWifi, settings: Settings = Settings()) = UtilityUiState(
    settings = settings, link = link, test = test,
    analysis = (test as? TestState.Done)?.let { ConnectionAnalyzer.analyze(it.measurement, link, settings) }
)

@Composable
private fun PreviewScreen(state: UtilityUiState) {
    MobileTech_UtilityAppTheme { Surface { UtilityContent(state, {}, {}, {}) } }
}

@Preview(showBackground = true, heightDp = 1100) @Composable private fun IdlePreview() = PreviewScreen(previewState(TestState.Idle))
@Preview(showBackground = true, heightDp = 1100) @Composable private fun RunningPreview() = PreviewScreen(previewState(TestState.Running(96.0, sample.samples)))
@Preview(showBackground = true, heightDp = 1100) @Composable private fun GoodPreview() = PreviewScreen(previewState(TestState.Done(sample, homeWifi, TestDepth.STANDARD)))
@Preview(showBackground = true, heightDp = 1100) @Composable private fun PoorPreview() = PreviewScreen(
    previewState(TestState.Done(Measurement(85, 25, 9.0, 0.8), homeWifi.copy(signalDbm = -74), TestDepth.STANDARD), settings = Settings(usage = Usage.LIVE))
)
@Preview(showBackground = true) @Composable private fun ErrorPreview() = PreviewScreen(previewState(TestState.Failed(ErrorReason.TIMEOUT)))
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, heightDp = 1100) @Composable private fun DarkPreview() = PreviewScreen(previewState(TestState.Done(sample, homeWifi, TestDepth.STANDARD)))
