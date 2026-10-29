package au.edu.jcu.assessment.utilityapp.ui.check.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.domain.model.Estimate
import au.edu.jcu.assessment.utilityapp.domain.model.EstimateKey
import au.edu.jcu.assessment.utilityapp.ui.formatDuration

@Composable
fun EstimateCard(estimate: Estimate, modifier: Modifier = Modifier) {
    val arg = when (estimate.key) {
        EstimateKey.DOWNLOAD_TIME, EstimateKey.UPLOAD_TIME -> formatDuration(estimate.arg?.toDoubleOrNull() ?: 0.0)
        else -> estimate.arg ?: ""
    }
    Card(modifier.fillMaxWidth()) {
        Text(stringResource(estimate.key.res, arg), Modifier.padding(14.dp), fontWeight = FontWeight.SemiBold)
    }
}
