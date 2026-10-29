package au.edu.jcu.assessment.utilityapp.ui.check

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.domain.model.TestStage
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
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Wi-Fi Checker",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Analyze network speed & health",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            IconButton(onClick = { viewModel.refreshWifiInfo() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh Wi-Fi")
            }
        }

        // Connection Summary Card
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
                    Column {
                        Text(
                            text = state.wifiInfo.ssid,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Signal: ${state.wifiInfo.rssi} dBm • Link: ${state.wifiInfo.linkSpeedMbps} Mbps",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    VerdictBadge(verdict = state.report.verdict)
                }
            }
        }

        // Progress bar when testing
        if (state.isTesting) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Testing Stage: ${state.stage.name}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                LinearProgressIndicator(
                    progress = { state.progress },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Speed Metrics Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GaugeCard(
                title = "DOWNLOAD",
                value = String.format("%.1f", state.downloadMbps),
                subtitle = "Mbps",
                modifier = Modifier.weight(1f)
            )
            GaugeCard(
                title = "UPLOAD",
                value = String.format("%.1f", state.uploadMbps),
                subtitle = "Mbps",
                modifier = Modifier.weight(1f)
            )
            GaugeCard(
                title = "PING",
                value = "${state.pingMs}",
                subtitle = "ms",
                modifier = Modifier.weight(1f)
            )
        }

        // Action Button
        Button(
            onClick = { viewModel.startSpeedTest() },
            enabled = !state.isTesting,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = if (state.isTesting) "Testing Wi-Fi..." else "Start Connection Test",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Estimates Section
        if (state.stage == TestStage.FINISHED) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Activity Suitability",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text("• Video Calls: ${state.report.estimates.videoCallQuality}")
                    Text("• 4K Streaming: ${state.report.estimates.streaming4k}")
                    Text("• 1 GB Download Time: ${state.report.estimates.download1GbTimeMinutes}")
                }
            }
        }

        // Diagnostics & Tips Cards
        DiagnosisCard(
            title = "Network Diagnostics",
            items = state.report.diagnosisList
        )

        DiagnosisCard(
            title = "Optimization Tips",
            items = state.report.tipsList
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
