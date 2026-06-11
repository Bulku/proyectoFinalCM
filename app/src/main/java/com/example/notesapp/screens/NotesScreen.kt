package com.example.notesapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notesapp.R
import com.example.notesapp.model.Note
import com.example.notesapp.model.NoteColor
import com.example.notesapp.viewmodel.NotesViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(viewModel: NotesViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<Note?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { editingNote = null; showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            }
        }
    ) { padding ->
        if (viewModel.notes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_notes),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.notes, key = { it.id }) { note ->
                    NoteCard(
                        note = note,
                        onEdit = { editingNote = note; showDialog = true },
                        onDelete = { viewModel.deleteNote(note.id) }
                    )
                }
            }
        }
    }

    if (showDialog) {
        NoteDialog(
            note = editingNote,
            onDismiss = { showDialog = false },
            onSave = { title, content, color ->
                if (editingNote != null) viewModel.updateNote(editingNote!!.id, title, content, color)
                else viewModel.addNote(title, content, color)
                showDialog = false
            }
        )
    }
}

@Composable
fun NoteCard(note: Note, onEdit: () -> Unit, onDelete: () -> Unit) {
    val bgColor = Color(note.color.hex)
    var showConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onEdit() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title.ifBlank { "Sin título" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = { showConfirm = true }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, null, tint = Color(0xFFE53935), modifier = Modifier.size(18.dp))
                    }
                }
            }
            if (note.content.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = note.content,
                    fontSize = 14.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = note.createdAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
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
                TextButton(onClick = { showConfirm = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDialog(note: Note?, onDismiss: () -> Unit, onSave: (String, String, NoteColor) -> Unit) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }
    var selectedColor by remember { mutableStateOf(note?.color ?: NoteColor.WHITE) }
    var titleError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(if (note != null) R.string.edit_note else R.string.add_note),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; titleError = false },
                    label = { Text(stringResource(R.string.note_title)) },
                    placeholder = { Text(stringResource(R.string.note_title_hint)) },
                    isError = titleError,
                    supportingText = {
                        if (titleError) Text(stringResource(R.string.field_required), color = MaterialTheme.colorScheme.error)
                    },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text(stringResource(R.string.note_content)) },
                    placeholder = { Text(stringResource(R.string.note_content_hint)) },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5
                )
                // Color picker row
                Text("Color", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NoteColor.values().forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(color.hex), RoundedCornerShape(50))
                                .clickable { selectedColor = color }
                                .then(
                                    if (selectedColor == color)
                                        Modifier.padding(2.dp).background(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                            RoundedCornerShape(50)
                                        )
                                    else Modifier
                                )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isBlank()) { titleError = true; return@Button }
                onSave(title, content, selectedColor)
            }) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}
