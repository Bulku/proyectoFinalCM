package com.example.notesapp.model

import java.time.LocalDate
import java.time.LocalTime

data class Note(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val content: String,
    val color: NoteColor = NoteColor.WHITE,
    val createdAt: LocalDate = LocalDate.now()
)

enum class NoteColor(val hex: Long) {
    WHITE(0xFFFFFFFF),
    YELLOW(0xFFFFF9C4),
    BLUE(0xFFE3F2FD),
    GREEN(0xFFE8F5E9),
    PINK(0xFFFCE4EC)
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
