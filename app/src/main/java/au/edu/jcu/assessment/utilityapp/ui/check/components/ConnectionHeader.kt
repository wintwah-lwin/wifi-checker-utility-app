package au.edu.jcu.assessment.utilityapp.ui.check.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.R
import au.edu.jcu.assessment.utilityapp.domain.model.LinkInfo
import au.edu.jcu.assessment.utilityapp.domain.model.LinkType

@Composable
fun ConnectionHeader(link: LinkInfo, modifier: Modifier = Modifier) {
    val fallback = when (link.type) {
        LinkType.WIFI -> R.string.link_wifi
        LinkType.CELLULAR -> R.string.link_cellular
        LinkType.ETHERNET -> R.string.link_ethernet
        LinkType.NONE -> R.string.no_connection
    }
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.connection_label), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(link.name ?: stringResource(fallback), style = MaterialTheme.typography.titleMedium)
            if (link.metered) AssistChip(onClick = {}, label = { Text(stringResource(R.string.metered)) })
        }
    }
}
