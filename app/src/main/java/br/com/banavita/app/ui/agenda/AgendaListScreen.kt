package br.com.banavita.app.ui.agenda

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
import br.com.banavita.app.domain.model.extraction.ConfirmResult
import br.com.banavita.app.domain.model.extraction.VoiceScreenContext
import br.com.banavita.app.ui.common.BanavitaIcons
import br.com.banavita.app.ui.common.ListItemCard
import br.com.banavita.app.ui.common.RequestNotificationPermissionIfNeeded
import br.com.banavita.app.ui.common.formatTime
import br.com.banavita.app.ui.voice.VoiceEnabledScreen
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale
import java.util.UUID

@Composable
fun AgendaListRoute(
    onAddClick: (Long) -> Unit,
    onItemClick: (UUID) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AgendaListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    RequestNotificationPermissionIfNeeded()
    AgendaListScreen(
        uiState = uiState,
        onAddClick = { onAddClick(uiState.selectedEpochDay) },
        onItemClick = onItemClick,
        onSelectDay = viewModel::onSelectDay,
        onPreviousMonth = viewModel::onPreviousMonth,
        onNextMonth = viewModel::onNextMonth,
        onOpenMonthPicker = viewModel::onOpenMonthPicker,
        onDismissMonthPicker = viewModel::onDismissMonthPicker,
        onPickerYear = viewModel::onPickerYear,
        onPickMonth = viewModel::onPickMonth,
        onHintShown = viewModel::consumeMonthHint,
        onVoiceResult = viewModel::applyVoice,
        modifier = modifier,
    )
}

@Composable
fun AgendaListScreen(
    uiState: AgendaUiState,
    onAddClick: () -> Unit,
    onItemClick: (UUID) -> Unit,
    onSelectDay: (Long) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onOpenMonthPicker: () -> Unit,
    onDismissMonthPicker: () -> Unit,
    onPickerYear: (Int) -> Unit,
    onPickMonth: (Int) -> Unit,
    onHintShown: () -> Unit,
    onVoiceResult: (ConfirmResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val hintMessage = stringResource(R.string.agenda_month_overview_hint)
    LaunchedEffect(uiState.showMonthHint) {
        if (uiState.showMonthHint) {
            snackbarHostState.showSnackbar(hintMessage)
            onHintShown()
        }
    }
    if (uiState.showMonthPicker) {
        MonthYearPickerDialog(
            year = uiState.pickerYear,
            selectedMonth = uiState.visibleMonth.monthValue,
            onYearDelta = onPickerYear,
            onPickMonth = onPickMonth,
            onDismiss = onDismissMonthPicker,
        )
    }
    VoiceEnabledScreen(
        modifier = modifier,
        voiceContext = VoiceScreenContext.AGENDA,
        addContentDescription = stringResource(R.string.agenda_add),
        onAddClick = onAddClick,
        onVoiceSuccess = onVoiceResult,
    ) { contentModifier ->
        Box(modifier = contentModifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
            ) {
                CalendarHeader(
                    monthTitle = uiState.monthTitle,
                    onPreviousMonth = onPreviousMonth,
                    onNextMonth = onNextMonth,
                    onOpenMonthPicker = onOpenMonthPicker,
                )
                Spacer(Modifier.height(8.dp))
                WeekdayHeader()
                MonthGrid(
                    cells = uiState.cells,
                    onSelectDay = onSelectDay,
                )
                Spacer(Modifier.height(16.dp))
                if (uiState.eventosDoDia.isEmpty()) {
                    Text(
                        text = stringResource(R.string.agenda_empty_day),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.eventosDoDia.forEach { evento ->
                            ListItemCard(
                                title = evento.titulo,
                                subtitle = formatTime(evento.dataHora),
                                onClick = { onItemClick(evento.id) },
                            )
                        }
                    }
                }
            }
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 88.dp),
            )
        }
    }
}

@Composable
private fun CalendarHeader(
    monthTitle: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onOpenMonthPicker: () -> Unit,
) {
    val pickerCd = stringResource(R.string.agenda_pick_month_cd, monthTitle)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                imageVector = BanavitaIcons.ChevronLeft,
                contentDescription = stringResource(R.string.agenda_prev_month),
            )
        }
        Text(
            text = monthTitle,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .clip(MaterialTheme.shapes.small)
                .clickable(onClick = onOpenMonthPicker)
                .semantics {
                    role = Role.Button
                    contentDescription = pickerCd
                }
                .padding(vertical = 8.dp),
        )
        IconButton(onClick = onNextMonth) {
            Icon(
                imageVector = BanavitaIcons.ChevronRight,
                contentDescription = stringResource(R.string.agenda_next_month),
            )
        }
    }
}

@Composable
private fun WeekdayHeader() {
    val labels = stringArrayResource(R.array.agenda_weekdays)
    Row(modifier = Modifier.fillMaxWidth()) {
        labels.forEach { label ->
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MonthGrid(
    cells: List<AgendaDayCell>,
    onSelectDay: (Long) -> Unit,
) {
    val weeks = cells.chunked(7)
    Column(modifier = Modifier.fillMaxWidth()) {
        weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { cell ->
                    DayCell(
                        cell = cell,
                        onClick = { onSelectDay(cell.epochDay) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    cell: AgendaDayCell,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val description = dayContentDescription(cell)
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .then(
                when {
                    cell.isSelected -> Modifier.background(scheme.primary)
                    cell.isToday -> Modifier.border(2.dp, scheme.primary, CircleShape)
                    else -> Modifier
                },
            )
            .clickable(onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = cell.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    cell.isSelected -> scheme.onPrimary
                    else -> scheme.onSurface
                },
                modifier = Modifier.alpha(if (cell.inMonth) 1f else 0.38f),
            )
            if (cell.eventCount > 0) {
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(
                            if (cell.isSelected) scheme.onPrimary else scheme.primary,
                        ),
                )
            } else {
                Spacer(Modifier.height(7.dp))
            }
        }
    }
}

@Composable
private fun dayContentDescription(cell: AgendaDayCell): String {
    val parts = buildList {
        add(stringResource(R.string.agenda_cd_date, cell.dayOfMonth))
        if (cell.isToday) add(stringResource(R.string.agenda_cd_today))
        if (cell.isSelected) add(stringResource(R.string.agenda_cd_selected))
        when {
            cell.eventCount == 1 -> add(stringResource(R.string.agenda_cd_one_event))
            cell.eventCount > 1 -> add(stringResource(R.string.agenda_cd_events, cell.eventCount))
        }
    }
    return parts.joinToString(", ")
}

@Composable
private fun MonthYearPickerDialog(
    year: Int,
    selectedMonth: Int,
    onYearDelta: (Int) -> Unit,
    onPickMonth: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val locale = Locale("pt", "BR")
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.agenda_pick_month_title)) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onYearDelta(-1) }) {
                        Icon(
                            imageVector = BanavitaIcons.ChevronLeft,
                            contentDescription = stringResource(R.string.agenda_prev_year),
                        )
                    }
                    Text(
                        text = year.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                    IconButton(onClick = { onYearDelta(1) }) {
                        Icon(
                            imageVector = BanavitaIcons.ChevronRight,
                            contentDescription = stringResource(R.string.agenda_next_year),
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Month.entries.chunked(3).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        row.forEach { month ->
                            val label = month.getDisplayName(TextStyle.SHORT, locale)
                                .replaceFirstChar { it.titlecase(locale) }
                            FilterChip(
                                selected = month.value == selectedMonth,
                                onClick = { onPickMonth(month.value) },
                                label = { Text(label) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.form_cancel))
            }
        },
    )
}
