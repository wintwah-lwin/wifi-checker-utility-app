package au.edu.jcu.assessment.utilityapp.ui.check.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import au.edu.jcu.assessment.utilityapp.R
import au.edu.jcu.assessment.utilityapp.domain.model.Measurement
import au.edu.jcu.assessment.utilityapp.ui.formatSpeed

@Composable
fun StatsRow(m: Measurement, useMbps: Boolean, modifier: Modifier = Modifier) {
    val speedUnit = stringResource(if (useMbps) R.string.units_mbps else R.string.units_mbs)
    val ms = stringResource(R.string.unit_ms)
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Stat(stringResource(R.string.stat_response), "${m.latencyMs}", ms)
        Stat(stringResource(R.string.stat_upload), m.upMbps?.let { formatSpeed(it, useMbps) } ?: "--", speedUnit)
        Stat(stringResource(R.string.stat_stability), "${m.jitterMs}", ms)
    }
}
