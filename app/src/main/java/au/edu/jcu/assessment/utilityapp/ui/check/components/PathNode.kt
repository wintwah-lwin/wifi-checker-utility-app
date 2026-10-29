package au.edu.jcu.assessment.utilityapp.ui.check.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.ui.theme.verdictColors

@Composable
fun PathNode(label: String, detail: String?, ok: Boolean?) {
    val colors = verdictColors()
    val dot = when (ok) { true -> colors.good; false -> colors.bad; null -> MaterialTheme.colorScheme.outline }
    Column(Modifier.width(84.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(12.dp).clip(CircleShape).background(dot))
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(detail ?: " ", style = MaterialTheme.typography.labelMedium)
    }
}
