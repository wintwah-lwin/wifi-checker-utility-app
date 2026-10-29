package au.edu.jcu.assessment.utilityapp.ui.check.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import au.edu.jcu.assessment.utilityapp.domain.model.NetworkVerdict
import au.edu.jcu.assessment.utilityapp.ui.theme.VerdictColors

@Composable
fun VerdictBadge(
    verdict: NetworkVerdict,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, label) = when (verdict) {
        NetworkVerdict.EXCELLENT -> VerdictColors.Excellent to "EXCELLENT"
        NetworkVerdict.GOOD -> VerdictColors.Good to "GOOD"
        NetworkVerdict.FAIR -> VerdictColors.Fair to "FAIR"
        NetworkVerdict.POOR -> VerdictColors.Poor to "POOR"
        NetworkVerdict.OFFLINE -> VerdictColors.Offline to "OFFLINE"
    }

    Text(
        text = label,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}
