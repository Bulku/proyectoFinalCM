package com.example.notesapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.BuildConfig
import com.example.notesapp.model.Priority
import com.example.notesapp.model.Task
import com.example.notesapp.network.GeminiApiService
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

data class AiUiState(
    val isLoading: Boolean = false,
    val response: String? = null,
    val error: String? = null
)

class AiViewModel : ViewModel() {

    private val apiService = GeminiApiService()

    var uiState by mutableStateOf(AiUiState())
        private set

    fun generateSuggestion(userRequest: String, currentTasks: List<Task>) {
        if (userRequest.isBlank() && currentTasks.isEmpty()) {
            uiState = AiUiState(error = "Escribe una solicitud o crea tareas para que la IA pueda analizarlas.")
            return
        }

        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank()) {
            uiState = AiUiState(
                error = "Falta configurar GEMINI_API_KEY en local.properties. No subas esa clave a GitHub."
            )
            return
        }

        viewModelScope.launch {
            uiState = AiUiState(isLoading = true)
            runCatching {
                apiService.generateSuggestion(
                    apiKey = apiKey,
                    prompt = buildPrompt(userRequest, currentTasks)
                )
            }.onSuccess { suggestion ->
                uiState = AiUiState(response = suggestion)
            }.onFailure { throwable ->
                uiState = AiUiState(error = throwable.message ?: "No fue posible consultar la IA.")
            }
        }
    }

    fun clearResult() {
        uiState = AiUiState()
    }

    private fun buildPrompt(userRequest: String, currentTasks: List<Task>): String {
        val tasksSummary = if (currentTasks.isEmpty()) {
            "Sin tareas registradas."
        } else {
            currentTasks
                .take(15)
                .joinToString(separator = "\n") { task ->
                    val date = task.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    val time = task.time?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "sin hora"
                    val status = if (task.isDone) "completada" else "pendiente"
                    "- ${task.title}: prioridad ${task.priority.toSpanish()}, fecha $date, hora $time, estado $status. ${task.description}"
                }
        }

        val request = userRequest.ifBlank {
            "Analiza mis tareas actuales y sugiere como priorizarlas para organizar mejor mi dia."
        }

        return """
            Solicitud del usuario:
            $request

            Tareas actuales de la aplicacion:
            $tasksSummary

            Responde con este formato:
            1. Prioridad recomendada.
            2. Acciones concretas para hoy.
            3. Sugerencia de fecha u hora si aplica.
            4. Riesgo o advertencia si alguna tarea parece urgente.

            No inventes datos que no aparezcan en la solicitud o en las tareas.
            Maximo 8 lineas.
        """.trimIndent()
    }

    private fun Priority.toSpanish(): String = when (this) {
        Priority.LOW -> "baja"
        Priority.MEDIUM -> "media"
        Priority.HIGH -> "alta"
    }
}
