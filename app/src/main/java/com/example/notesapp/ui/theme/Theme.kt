package com.example.notesapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontFamily
import com.example.notesapp.model.NoteColor

val Blue500 = Color(0xFF1976D2)
val Blue600 = Color(0xFF1565C0)
val Blue100 = Color(0xFFBBDEFB)
val BlueBg = Color(0xFFF0F6FF)

/**
 * Opciones de color visibles desde la pantalla Ajustes.
 * La opcion BLUE mantiene la identidad visual original de la app.
 */
enum class AppColorOption(
    val label: String,
    val primary: Color,
    val primaryContainer: Color,
    val secondary: Color,
    val background: Color
) {
    BLUE("Azul", Blue600, Blue100, Blue500, BlueBg),
    PURPLE("Morado", Color(0xFF6A1B9A), Color(0xFFE1BEE7), Color(0xFF8E24AA), Color(0xFFF8F1FF)),
    GREEN("Verde", Color(0xFF2E7D32), Color(0xFFC8E6C9), Color(0xFF43A047), Color(0xFFF1FFF4)),
    ORANGE("Naranja", Color(0xFFE65100), Color(0xFFFFCC80), Color(0xFFFB8C00), Color(0xFFFFF7ED)),
    PINK("Rosa", Color(0xFFC2185B), Color(0xFFF8BBD0), Color(0xFFE91E63), Color(0xFFFFF1F6))
}

val noteColorOrder = listOf(
    NoteColor.NEUTRAL,
    NoteColor.LIGHT,
    NoteColor.SOFT,
    NoteColor.TINT,
    NoteColor.WARM,
    NoteColor.COOL
)

fun AppColorOption.noteColors(): Map<NoteColor, Color> {
    val warmAccent = when (this) {
        AppColorOption.BLUE -> Color(0xFFFFF6E8)
        AppColorOption.PURPLE -> Color(0xFFFFEEF6)
        AppColorOption.GREEN -> Color(0xFFFFF4E6)
        AppColorOption.ORANGE -> Color(0xFFEAF4FF)
        AppColorOption.PINK -> Color(0xFFE9FFF5)
    }
    val coolAccent = when (this) {
        AppColorOption.BLUE -> Color(0xFFE9EDFF)
        AppColorOption.PURPLE -> Color(0xFFE7F8F1)
        AppColorOption.GREEN -> Color(0xFFE8F2FF)
        AppColorOption.ORANGE -> Color(0xFFF1E8FF)
        AppColorOption.PINK -> Color(0xFFEDE8FF)
    }

    return mapOf(
        NoteColor.NEUTRAL to lerp(Color.White, background, 0.18f),
        NoteColor.LIGHT to background,
        NoteColor.SOFT to lerp(background, primaryContainer, 0.62f),
        NoteColor.TINT to lerp(primaryContainer, secondary, 0.22f),
        NoteColor.WARM to warmAccent,
        NoteColor.COOL to coolAccent
    )
}

fun AppColorOption.resolveNoteColor(noteColor: NoteColor): Color =
    noteColors()[noteColor] ?: noteColors().getValue(NoteColor.NEUTRAL)

enum class AppFontOption(val label: String, val fontFamily: FontFamily) {
    DEFAULT("Predeterminada", FontFamily.Default),
    SANS_SERIF("Sans Serif", FontFamily.SansSerif),
    SERIF("Serif", FontFamily.Serif),
    MONOSPACE("Monospace", FontFamily.Monospace),
    CURSIVE("Cursiva", FontFamily.Cursive)
}

private fun appColorScheme(option: AppColorOption) = lightColorScheme(
    primary = option.primary,
    onPrimary = Color.White,
    primaryContainer = option.primaryContainer,
    secondary = option.secondary,
    background = option.background,
    surface = Color.White,
    error = Color(0xFFB00020),
    onBackground = Color(0xFF1A1A2E),
    onSurface = Color(0xFF1A1A2E),
)

private fun Typography.withFontFamily(fontFamily: FontFamily): Typography = Typography(
    displayLarge = displayLarge.copy(fontFamily = fontFamily),
    displayMedium = displayMedium.copy(fontFamily = fontFamily),
    displaySmall = displaySmall.copy(fontFamily = fontFamily),
    headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
    headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
    headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
    titleLarge = titleLarge.copy(fontFamily = fontFamily),
    titleMedium = titleMedium.copy(fontFamily = fontFamily),
    titleSmall = titleSmall.copy(fontFamily = fontFamily),
    bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
    bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
    bodySmall = bodySmall.copy(fontFamily = fontFamily),
    labelLarge = labelLarge.copy(fontFamily = fontFamily),
    labelMedium = labelMedium.copy(fontFamily = fontFamily),
    labelSmall = labelSmall.copy(fontFamily = fontFamily),
)

@Composable
fun NotesAppTheme(
    colorOption: AppColorOption = AppColorOption.BLUE,
    fontOption: AppFontOption = AppFontOption.DEFAULT,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = appColorScheme(colorOption),
        typography = Typography().withFontFamily(fontOption.fontFamily),
        content = content
    )
}
