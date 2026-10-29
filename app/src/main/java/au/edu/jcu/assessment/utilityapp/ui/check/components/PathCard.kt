package au.edu.jcu.assessment.utilityapp.ui.check.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.R
import au.edu.jcu.assessment.utilityapp.domain.model.Diagnosis
import au.edu.jcu.assessment.utilityapp.domain.model.LinkInfo
import au.edu.jcu.assessment.utilityapp.domain.model.LinkType
import au.edu.jcu.assessment.utilityapp.domain.model.Measurement

/** Phone to router to internet, with each link coloured by whether it looks healthy. */
@Composable
fun PathCard(link: LinkInfo, diagnosis: Diagnosis, measurement: Measurement, modifier: Modifier = Modifier) {
    val middleOk = diagnosis.localOk ?: diagnosis.internetOk
    val middleLabel = if (link.type == LinkType.CELLULAR) R.string.node_mobile else R.string.node_router
    Card(modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                PathNode(stringResource(R.string.node_phone), link.signalDbm?.let { "$it dBm" }, null)
                PathLink(middleOk, Modifier.weight(1f))
                PathNode(stringResource(middleLabel), null, middleOk)
                PathLink(diagnosis.internetOk, Modifier.weight(1f))
                PathNode(stringResource(R.string.node_internet), "${measurement.latencyMs} ms", diagnosis.internetOk)
            }
            Text(stringResource(diagnosis.message.res), fontWeight = FontWeight.SemiBold)
        }
    }
}
