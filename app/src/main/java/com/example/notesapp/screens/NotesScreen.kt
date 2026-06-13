package com.example.notesapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import com.example.notesapp.model.NoteFolder
import com.example.notesapp.ui.theme.AppColorOption
import com.example.notesapp.ui.theme.noteColorOrder
import com.example.notesapp.ui.theme.resolveNoteColor
import com.example.notesapp.viewmodel.NotesViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(viewModel: NotesViewModel, colorOption: AppColorOption) {
    var showDialog by remember { mutableStateOf(false) }
    var showFolderDialog by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<Note?>(null) }
    var targetFolderId by remember { mutableStateOf<Long?>(null) }
    val expandedFolders = remember { mutableStateMapOf<Long, Boolean>() }

    val rootNotes = viewModel.notesWithoutFolder()
    val isEmpty = viewModel.notes.isEmpty() && viewModel.noteFolders.isEmpty()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingNote = null
                    targetFolderId = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            }
        }
    ) { padding ->
        if (isEmpty) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.no_notes),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                OutlinedButton(onClick = { showFolderDialog = true }) {
                    Icon(Icons.Default.CreateNewFolder, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.add_folder))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(key = "add-folder") {
                    OutlinedButton(
                        onClick = { showFolderDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CreateNewFolder, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.add_folder))
                    }
                }

                viewModel.noteFolders.forEach { folder ->
                    val folderNotes = viewModel.notesInFolder(folder.id)
                    val expanded = expandedFolders[folder.id] == true

                    item(key = "folder-${folder.id}") {
                        FolderCard(
                            folder = folder,
                            noteCount = folderNotes.size,
                            expanded = expanded,
                            onToggle = {
                                expandedFolders[folder.id] = !expanded
                            },
                            onDelete = { viewModel.deleteNoteFolder(folder.id) }
                        )
                    }

                    item(key = "folder-content-${folder.id}") {
                        AnimatedVisibility(
                            visible = expanded,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier.padding(start = 12.dp, top = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                TextButton(
                                    onClick = {
                                        editingNote = null
                                        targetFolderId = folder.id
                                        showDialog = true
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(stringResource(R.string.add_note_to_folder))
                                }

                                if (folderNotes.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.no_notes),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                } else {
                                    folderNotes.forEach { note ->
                                        NoteCard(
                                            note = note,
                                            colorOption = colorOption,
                                            onEdit = {
                                                editingNote = note
                                                targetFolderId = note.folderId
                                                showDialog = true
                                            },
                                            onDelete = { viewModel.deleteNote(note.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (rootNotes.isNotEmpty() && viewModel.noteFolders.isNotEmpty()) {
                    item(key = "root-header") {
                        Text(
                            text = stringResource(R.string.notes_without_folder),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                items(rootNotes, key = { it.id }) { note ->
                    NoteCard(
                        note = note,
                        colorOption = colorOption,
                        onEdit = {
                            editingNote = note
                            targetFolderId = note.folderId
                            showDialog = true
                        },
                        onDelete = { viewModel.deleteNote(note.id) }
                    )
                }
            }
        }
    }

    if (showFolderDialog) {
        FolderDialog(
            onDismiss = { showFolderDialog = false },
            onSave = { name ->
                viewModel.addNoteFolder(name)
                showFolderDialog = false
            }
        )
    }

    if (showDialog) {
        NoteDialog(
            note = editingNote,
            colorOption = colorOption,
            folders = viewModel.noteFolders,
            initialFolderId = editingNote?.folderId ?: targetFolderId,
            onDismiss = { showDialog = false },
            onSave = { title, content, color, folderId ->
                if (editingNote != null) {
                    viewModel.updateNote(editingNote!!.id, title, content, color, folderId)
                } else {
                    viewModel.addNote(title, content, color, folderId)
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun FolderCard(
    folder: NoteFolder,
    noteCount: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    var showConfirm by remember { mutableStateOf(false) }
    val primary = MaterialTheme.colorScheme.primary
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 220),
        label = "folderChevron"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onToggle() },
        shape = RoundedCornerShape(18.dp),
        color = if (expanded) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        tonalElevation = if (expanded) 1.dp else 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(
            width = 1.dp,
            color = if (expanded) primary.copy(alpha = 0.28f)
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.14f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(primary.copy(alpha = 0.1f), RoundedCornerShape(13.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Folder,
                    contentDescription = null,
                    tint = primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = folder.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(5.dp))
                Surface(
                    shape = RoundedCornerShape(50),
                    color = primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = stringResource(R.string.notes_count, noteCount),
                        style = MaterialTheme.typography.labelSmall,
                        color = primary,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                    )
                }
            }
            IconButton(
                onClick = { showConfirm = true },
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFFEBEE))
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(17.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                        RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(chevronRotation)
                )
            }
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text(stringResource(R.string.confirm_delete)) },
            text = { Text(stringResource(R.string.confirm_delete_folder)) },
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

@Composable
fun FolderDialog(onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.add_folder), fontWeight = FontWeight.Bold)
        },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = false },
                label = { Text(stringResource(R.string.folder_name)) },
                placeholder = { Text(stringResource(R.string.folder_name_hint)) },
                isError = nameError,
                supportingText = {
                    if (nameError) Text(stringResource(R.string.field_required), color = MaterialTheme.colorScheme.error)
                },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = {
                if (name.isBlank()) {
                    nameError = true
                    return@Button
                }
                onSave(name)
            }) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

@Composable
fun NoteCard(note: Note, colorOption: AppColorOption, onEdit: () -> Unit, onDelete: () -> Unit) {
    val bgColor = colorOption.resolveNoteColor(note.color)
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
fun NoteDialog(
    note: Note?,
    colorOption: AppColorOption,
    folders: List<NoteFolder>,
    initialFolderId: Long?,
    onDismiss: () -> Unit,
    onSave: (String, String, NoteColor, Long?) -> Unit
) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }
    var selectedColor by remember { mutableStateOf(note?.color ?: NoteColor.NEUTRAL) }
    var selectedFolderId by remember { mutableStateOf(initialFolderId) }
    var titleError by remember { mutableStateOf(false) }
    var folderExpanded by remember { mutableStateOf(false) }

    val selectedFolderName = folders.find { it.id == selectedFolderId }?.name
        ?: stringResource(R.string.no_folder)

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
                if (folders.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = folderExpanded,
                        onExpandedChange = { folderExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedFolderName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.note_folder)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = folderExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = folderExpanded,
                            onDismissRequest = { folderExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.no_folder)) },
                                onClick = {
                                    selectedFolderId = null
                                    folderExpanded = false
                                }
                            )
                            folders.forEach { folder ->
                                DropdownMenuItem(
                                    text = { Text(folder.name) },
                                    onClick = {
                                        selectedFolderId = folder.id
                                        folderExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Text("Color", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    noteColorOrder.forEach { color ->
                        val isSelected = selectedColor == color
                        val swatchColor = colorOption.resolveNoteColor(color)
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(50))
                                .background(swatchColor)
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)
                                    },
                                    shape = RoundedCornerShape(50)
                                )
                                .clickable { selectedColor = color },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primary,
                                            RoundedCornerShape(50)
                                        )
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isBlank()) { titleError = true; return@Button }
                onSave(title, content, selectedColor, selectedFolderId)
            }) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}
