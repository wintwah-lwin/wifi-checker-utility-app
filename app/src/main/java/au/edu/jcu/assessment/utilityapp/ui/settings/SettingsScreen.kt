package au.edu.jcu.assessment.utilityapp.ui.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.jcu.assessment.utilityapp.R
import au.edu.jcu.assessment.utilityapp.domain.model.*

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    SettingsContent(settings, viewModel::update)
}

@Composable
fun SettingsContent(settings: Settings, onChange: ((Settings) -> Settings) -> Unit, modifier: Modifier = Modifier) {
    val onOff: @Composable (Boolean) -> String = { stringResource(if (it) R.string.on else R.string.off) }
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.headlineMedium)
        ChoiceGroup(R.string.setting_usage, null, Usage.entries, settings.usage, { stringResource(it.label) }) { picked ->
            onChange { it.copy(usage = picked) }
        }
        ChoiceGroup(R.string.setting_picky, R.string.setting_picky_hint, Picky.entries, settings.picky, { stringResource(it.label) }) { picked ->
            onChange { it.copy(picky = picked) }
        }
        ChoiceGroup(R.string.setting_depth, R.string.setting_depth_hint, TestDepth.entries, settings.depth,
            { "${stringResource(it.label)} (~${it.approxMb} MB)" }) { picked -> onChange { it.copy(depth = picked) } }
        ChoiceGroup(R.string.setting_data_saver, R.string.setting_data_saver_hint, listOf(true, false), settings.dataSaver, onOff) { picked ->
            onChange { it.copy(dataSaver = picked) }
        }
        ChoiceGroup(R.string.setting_haptics, null, listOf(true, false), settings.haptics, onOff) { picked ->
            onChange { it.copy(haptics = picked) }
        }
        ChoiceGroup(R.string.setting_units, null, listOf(true, false), settings.useMbps,
            { stringResource(if (it) R.string.units_mbps else R.string.units_mbs) }) { picked -> onChange { it.copy(useMbps = picked) } }
    }
}

/** One reusable settings row: a title, an optional hint and a wrapping row of choice chips. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> ChoiceGroup(
    @StringRes title: Int,
    @StringRes hint: Int?,
    options: List<T>,
    selected: T,
    label: @Composable (T) -> String,
    onSelect: (T) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(title), style = MaterialTheme.typography.titleMedium)
        hint?.let { Text(stringResource(it), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                FilterChip(selected = option == selected, onClick = { onSelect(option) }, label = { Text(label(option)) })
            }
        }
    }
}
