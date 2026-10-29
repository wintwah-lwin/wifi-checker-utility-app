package au.edu.jcu.assessment.utilityapp.ui.check.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.ui.theme.verdictColors

@Composable
fun PathLink(ok: Boolean, modifier: Modifier = Modifier) {
    val colors = verdictColors()
    Box(modifier.padding(top = 5.dp).height(3.dp).clip(RoundedCornerShape(50)).background(if (ok) colors.good else colors.bad))
}
