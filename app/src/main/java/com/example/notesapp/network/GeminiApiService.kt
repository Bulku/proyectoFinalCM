package com.example.notesapp.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class GeminiApiService {

    suspend fun generateSuggestion(apiKey: String, prompt: String): String = withContext(Dispatchers.IO) {
        val connection = (URL(GEMINI_ENDPOINT).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            connectTimeout = 15_000
            readTimeout = 30_000
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("x-goog-api-key", apiKey)
        }

        try {
            val requestBody = createRequestBody(prompt).toString().toByteArray(Charsets.UTF_8)
            connection.outputStream.use { output -> output.write(requestBody) }

            val responseCode = connection.responseCode
            val responseBody = if (responseCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
            }

            if (responseCode !in 200..299) {
                throw IllegalStateException("Error $responseCode: ${extractErrorMessage(responseBody)}")
            }

            extractText(responseBody)
        } finally {
            connection.disconnect()
        }
    }

    private fun createRequestBody(prompt: String): JSONObject {
        val systemInstruction = JSONObject().put(
            "parts",
            JSONArray().put(
                JSONObject().put(
                    "text",
                    "Eres un asistente de productividad para una app movil de tareas, notas y calendario. " +
                        "Responde siempre en espanol, de forma breve, accionable y clara."
                )
            )
        )

        val contents = JSONArray().put(
            JSONObject()
                .put("role", "user")
                .put(
                    "parts",
                    JSONArray().put(JSONObject().put("text", prompt))
                )
        )

        val generationConfig = JSONObject()
            .put("temperature", 0.35)
            .put("maxOutputTokens", 600)

        return JSONObject()
            .put("system_instruction", systemInstruction)
            .put("contents", contents)
            .put("generationConfig", generationConfig)
    }

    private fun extractText(responseBody: String): String {
        val root = JSONObject(responseBody)
        val candidates = root.optJSONArray("candidates")
            ?: throw IllegalStateException("La API no devolvio candidatos de respuesta.")

        val parts = candidates
            .optJSONObject(0)
            ?.optJSONObject("content")
            ?.optJSONArray("parts")
            ?: throw IllegalStateException("La respuesta de la API no tiene texto disponible.")

        val result = buildString {
            for (index in 0 until parts.length()) {
                append(parts.optJSONObject(index)?.optString("text").orEmpty())
            }
        }.trim()

        if (result.isBlank()) {
            throw IllegalStateException("La IA respondio vacio. Intenta nuevamente.")
        }

        return result
    }

    private fun extractErrorMessage(responseBody: String): String {
        return runCatching {
            JSONObject(responseBody).optJSONObject("error")?.optString("message")
        }.getOrNull().orEmpty().ifBlank { "No fue posible procesar la solicitud." }
    }

    companion object {
        private const val GEMINI_ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"
    }
}
