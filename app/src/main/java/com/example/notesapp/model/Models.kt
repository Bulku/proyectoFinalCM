package com.example.notesapp.model

import java.time.LocalDate
import java.time.LocalTime

data class NoteFolder(
    val id: Long = System.currentTimeMillis(),
    val name: String
)

data class Note(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val content: String,
    val color: NoteColor = NoteColor.NEUTRAL,
    val folderId: Long? = null,
    val createdAt: LocalDate = LocalDate.now()
)

enum class NoteColor {
    NEUTRAL,
    LIGHT,
    SOFT,
    TINT,
    WARM,
    COOL
}

data class Task(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime? = null,
    val isDone: Boolean = false
)

enum class Priority { LOW, MEDIUM, HIGH }
