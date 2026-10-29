package au.edu.jcu.assessment.utilityapp.ui.check

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.R
import au.edu.jcu.assessment.utilityapp.domain.model.Usage
import au.edu.jcu.assessment.utilityapp.ui.check.components.DiagnosisCard
import au.edu.jcu.assessment.utilityapp.ui.check.components.GaugeCard
import au.edu.jcu.assessment.utilityapp.ui.check.components.VerdictBadge

@Composable
fun UtilityScreen(viewModel: UtilityViewModel) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = state.linkInfo.name ?: stringResource(R.string.no_connection),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            IconButton(onClick = { viewModel.refreshLinkInfo() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }

        // Usage Selector Chips
        Text(
            text = stringResource(R.string.setting_usage),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Usage.entries.forEach { usage ->
                FilterChip(
                    selected = state.settings.usage == usage,
                    onClick = { viewModel.selectUsage(usage) },
                    label = { Text(stringResource(usage.label)) }
                )
            }
        }

        // Verdict Summary Card
        state.analysis?.let { analysis ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(state.settings.usage.label),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        VerdictBadge(
                            verdict = analysis.verdict,
                            usageName = stringResource(state.settings.usage.phrase)
                        )
                    }

                    analysis.estimate?.let { est ->
                        val estText = if (est.arg != null) stringResource(est.key.res, est.arg) else stringResource(est.key.res)
                        Text(
                            text = estText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        // Progress bar during testing
        if (state.isTesting) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.testing),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }

        // Metrics Row
        val m = state.measurement
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GaugeCard(
                title = stringResource(R.string.metric_download),
                value = if (state.isTesting) String.format("%.1f", state.currentSampleMbps) else m?.downMbps?.let { String.format("%.1f", it) } ?: "--",
                subtitle = stringResource(R.string.units_mbps),
                modifier = Modifier.weight(1f)
            )
            GaugeCard(
                title = stringResource(R.string.metric_upload),
                value = m?.upMbps?.let { String.format("%.1f", it) } ?: "--",
                subtitle = stringResource(R.string.units_mbps),
                modifier = Modifier.weight(1f)
            )
            GaugeCard(
                title = stringResource(R.string.metric_latency),
                value = m?.latencyMs?.toString() ?: "--",
                subtitle = stringResource(R.string.unit_ms),
                modifier = Modifier.weight(1f)
            )
        }

        // Run Test Action Button
        Button(
            onClick = { viewModel.startTest() },
            enabled = !state.isTesting,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = if (state.isTesting) stringResource(R.string.testing) else stringResource(R.string.test_again),
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Diagnosis & Tips Section
        state.analysis?.let { analysis ->
            DiagnosisCard(
                diagnosis = analysis.diagnosis,
                tips = analysis.tips
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
