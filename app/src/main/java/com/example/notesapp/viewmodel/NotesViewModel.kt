package com.example.notesapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.notesapp.model.Note
import com.example.notesapp.model.NoteColor
import com.example.notesapp.model.Priority
import com.example.notesapp.model.Task
import java.time.LocalDate
import java.time.LocalTime

class NotesViewModel : ViewModel() {

    // ── Notes ────────────────────────────────────────────────────────────────
    val notes = mutableStateListOf<Note>()

    fun addNote(title: String, content: String, color: NoteColor) {
        if (title.isBlank() && content.isBlank()) return
        notes.add(0, Note(title = title, content = content, color = color))
    }

    fun updateNote(id: Long, title: String, content: String, color: NoteColor) {
        val index = notes.indexOfFirst { it.id == id }
        if (index != -1) notes[index] = notes[index].copy(title = title, content = content, color = color)
    }

    fun deleteNote(id: Long) {
        notes.removeIf { it.id == id }
    }

    // ── Tasks ────────────────────────────────────────────────────────────────
    val tasks = mutableStateListOf<Task>()

    // Selected date in Calendar tab
    val selectedDate = mutableStateOf(LocalDate.now())

    fun tasksForDate(date: LocalDate) = tasks.filter { it.date == date }

    fun addTask(
        title: String,
        description: String,
        priority: Priority,
        date: LocalDate,
        time: LocalTime?
    ) {
        if (title.isBlank()) return
        tasks.add(Task(title = title, description = description, priority = priority, date = date, time = time))
        tasks.sortWith(compareBy({ it.date }, { it.time ?: LocalTime.MAX }))
    }

    fun updateTask(
        id: Long,
        title: String,
        description: String,
        priority: Priority,
        date: LocalDate,
        time: LocalTime?
    ) {
        val index = tasks.indexOfFirst { it.id == id }
        if (index != -1) {
            tasks[index] = tasks[index].copy(
                title = title, description = description,
                priority = priority, date = date, time = time
            )
            tasks.sortWith(compareBy({ it.date }, { it.time ?: LocalTime.MAX }))
        }
    }

    fun toggleTaskDone(id: Long) {
        val index = tasks.indexOfFirst { it.id == id }
        if (index != -1) tasks[index] = tasks[index].copy(isDone = !tasks[index].isDone)
    }

    fun deleteTask(id: Long) {
        tasks.removeIf { it.id == id }
    }
}
