package au.edu.jcu.assessment.utilityapp.ui.check.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.domain.model.Grade
import au.edu.jcu.assessment.utilityapp.domain.model.Usage
import au.edu.jcu.assessment.utilityapp.ui.theme.color

/** Horizontally scrolling activity picker. A tick, tilde or cross shows how the last result grades for each. */
@Composable
fun UsageChips(selected: Usage, grades: Map<Usage, Grade>, onSelect: (Usage) -> Unit, modifier: Modifier = Modifier) {
    LazyRow(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(Usage.entries) { usage ->
            val grade = grades[usage]
            FilterChip(
                selected = usage == selected,
                onClick = { onSelect(usage) },
                label = { Text(stringResource(usage.label)) },
                leadingIcon = if (grade != null) {
                    { Text(when (grade) { Grade.GOOD -> "✔"; Grade.STRUGGLES -> "~"; Grade.POOR -> "✖" }, color = grade.color()) }
                } else null
            )
        }
    }
}
