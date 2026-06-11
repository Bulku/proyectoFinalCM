package com.example.notesapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notesapp.R
import com.example.notesapp.model.Priority
import com.example.notesapp.model.Task
import com.example.notesapp.viewmodel.NotesViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(viewModel: NotesViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    val selectedDate by viewModel.selectedDate
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.Default.ChevronLeft, null, tint = Color.White)
            }
            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
                    .replaceFirstChar { it.uppercase() },
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.Default.ChevronRight, null, tint = Color.White)
            }
        }

        Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary)) {
            listOf("D", "L", "M", "M", "J", "V", "S").forEach { day ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        day,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }

        CalendarGrid(
            currentMonth = currentMonth,
            selectedDate = selectedDate,
            tasksPerDay = { date -> viewModel.tasksForDate(date).size },
            onDateSelected = { viewModel.selectedDate.value = it }
        )

        HorizontalDivider()

        val dayTasks = viewModel.tasksForDate(selectedDate)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMM d"))
                    .replaceFirstChar { it.uppercase() },
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
            FilledTonalButton(
                onClick = { editingTask = null; showDialog = true },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.add_task), fontSize = 13.sp)
            }
        }

        if (dayTasks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.no_tasks),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dayTasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onToggle = { viewModel.toggleTaskDone(task.id) },
                        onEdit = { editingTask = task; showDialog = true },
                        onDelete = { viewModel.deleteTask(task.id) }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    if (showDialog) {
        TaskDialog(
            task = editingTask,
            defaultDate = selectedDate,
            onDismiss = { showDialog = false },
            onSave = { title, desc, priority, date, time ->
                if (editingTask != null) {
                    viewModel.updateTask(editingTask!!.id, title, desc, priority, date, time)
                } else {
                    viewModel.addTask(title, desc, priority, date, time)
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    tasksPerDay: (LocalDate) -> Int,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDay = currentMonth.atDay(1)
    val startOffset = firstDay.dayOfWeek.value % 7
    val daysInMonth = currentMonth.lengthOfMonth()
    val today = LocalDate.now()

    val weeks = ((startOffset + daysInMonth) + 6) / 7

    Column(modifier = Modifier.fillMaxWidth().background(Color.White)) {
        for (week in 0 until weeks) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (dayOfWeek in 0..6) {
                    val dayNumber = week * 7 + dayOfWeek - startOffset + 1
                    Box(
                        modifier = Modifier.weight(1f).aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (dayNumber in 1..daysInMonth) {
                            val date = currentMonth.atDay(dayNumber)
                            val isSelected = date == selectedDate
                            val isToday = date == today
                            val taskCount = tasksPerDay(date)

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            isToday -> MaterialTheme.colorScheme.primaryContainer
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable { onDateSelected(date) },
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = dayNumber.toString(),
                                    fontSize = 13.sp,
                                    fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = when {
                                        isSelected -> Color.White
                                        isToday -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                if (taskCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .background(
                                                if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                                                CircleShape
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: Task, onToggle: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    var showConfirm by remember { mutableStateOf(false) }

    val priorityColor = when (task.priority) {
        Priority.HIGH -> Color(0xFFE53935)
        Priority.MEDIUM -> Color(0xFFFB8C00)
        Priority.LOW -> Color(0xFF43A047)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .background(priorityColor, RoundedCornerShape(2.dp))
            )
            Spacer(Modifier.width(12.dp))

            Checkbox(
                checked = task.isDone,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )
            Spacer(Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (task.isDone) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                if (task.description.isNotBlank()) {
                    Text(
                        task.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        maxLines = 1
                    )
                }
                if (task.time != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Schedule,
                            null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            task.time.format(DateTimeFormatter.ofPattern("h:mm a")),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = { showConfirm = true }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp), tint = Color(0xFFE53935))
            }
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text(stringResource(R.string.confirm_delete)) },
            confirmButton = {
                TextButton(onClick = { onDelete(); showConfirm = false }) {
                    Text(stringResource(R.string.delete), color = Color(0xFFE53935))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDialog(
    task: Task?,
    defaultDate: LocalDate,
    onDismiss: () -> Unit,
    onSave: (String, String, Priority, LocalDate, LocalTime?) -> Unit
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var priority by remember { mutableStateOf(task?.priority ?: Priority.MEDIUM) }
    var selectedDate by remember { mutableStateOf(task?.date ?: defaultDate) }
    var selectedTime by remember { mutableStateOf(task?.time) }
    var titleError by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(if (task != null) R.string.edit_task else R.string.add_task),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; titleError = false },
                    label = { Text(stringResource(R.string.task_title)) },
                    placeholder = { Text(stringResource(R.string.task_title_hint)) },
                    isError = titleError,
                    supportingText = {
                        if (titleError) {
                            Text(stringResource(R.string.field_required), color = MaterialTheme.colorScheme.error)
                        }
                    },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.task_description)) },
                    placeholder = { Text(stringResource(R.string.task_description_hint)) },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    maxLines = 3
                )

                Text(
                    stringResource(R.string.priority),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Priority.values().forEach { p ->
                        val (label, color) = when (p) {
                            Priority.LOW -> stringResource(R.string.priority_low) to Color(0xFF43A047)
                            Priority.MEDIUM -> stringResource(R.string.priority_medium) to Color(0xFFFB8C00)
                            Priority.HIGH -> stringResource(R.string.priority_high) to Color(0xFFE53935)
                        }
                        val isSelected = priority == p
                        OutlinedButton(
                            onClick = { priority = p },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) color else Color.Transparent,
                                contentColor = if (isSelected) Color.White else color
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, color),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f).height(36.dp)
                        ) {
                            Text(label, fontSize = 12.sp)
                        }
                    }
                }

                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")))
                }

                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Schedule, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        selectedTime?.format(DateTimeFormatter.ofPattern("h:mm a"))
                            ?: stringResource(R.string.set_time)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isBlank()) {
                    titleError = true
                    return@Button
                }
                onSave(title, description, priority, selectedDate, selectedTime)
            }) {
                Text(stringResource(R.string.save_task))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )

    if (showDatePicker) {
        AppDatePickerDialog(
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showTimePicker) {
        AppTimePickerDialog(
            selectedTime = selectedTime,
            onTimeSelected = { selectedTime = it },
            onDismiss = { showTimePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppDatePickerDialog(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val initialMillis = remember(selectedDate) {
        selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    onDateSelected(
                        Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    )
                }
                onDismiss()
            }) {
                Text(stringResource(R.string.accept))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    ) {
        DatePicker(
            state = datePickerState,
            title = { Text(stringResource(R.string.select_date)) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTimePickerDialog(
    selectedTime: LocalTime?,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = selectedTime?.hour ?: 8,
        initialMinute = selectedTime?.minute ?: 0,
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_time), fontWeight = FontWeight.Bold) },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TimePicker(state = timePickerState)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onTimeSelected(LocalTime.of(timePickerState.hour, timePickerState.minute))
                onDismiss()
            }) {
                Text(stringResource(R.string.accept))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}
