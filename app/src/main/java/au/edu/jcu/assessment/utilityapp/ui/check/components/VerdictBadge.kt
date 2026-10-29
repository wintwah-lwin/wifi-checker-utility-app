package au.edu.jcu.assessment.utilityapp.ui.check.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import au.edu.jcu.assessment.utilityapp.R
import au.edu.jcu.assessment.utilityapp.domain.model.Grade
import au.edu.jcu.assessment.utilityapp.domain.model.Verdict
import au.edu.jcu.assessment.utilityapp.ui.theme.color

@Composable
fun VerdictBadge(
    verdict: Verdict,
    usageName: String,
    modifier: Modifier = Modifier
) {
    val textRes = when (verdict.grade) {
        Grade.GOOD -> R.string.verdict_good
        Grade.STRUGGLES -> R.string.verdict_struggles
        Grade.POOR -> R.string.verdict_poor
    }

    Text(
        text = stringResource(textRes, usageName),
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        modifier = modifier
            .background(verdict.grade.color(), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}
