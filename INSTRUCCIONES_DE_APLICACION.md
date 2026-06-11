# Instrucciones para aplicar la mejora fase 1

Esta carpeta contiene los archivos modificados y nuevos para mejorar el proyecto `Proyecto_mobile`.

## Cambios incluidos

1. Personalizacion visual:
   - Nueva pantalla `Ajustes`.
   - Seleccion de color principal de la app.
   - Seleccion de tipo de fuente.
   - Preferencias guardadas con `SharedPreferences`.

2. Correccion de colores en calendario/hora:
   - Se reemplazan los dialogos nativos `android.app.DatePickerDialog` y `android.app.TimePickerDialog` por componentes Material 3 de Compose.
   - Asi el selector de fecha y hora toma el color del tema de la app y no el verde por defecto del sistema.

3. API de IA:
   - Nueva pantalla `IA`.
   - Consumo de Gemini API por REST.
   - Sugerencias para priorizar tareas y organizar el dia.
   - API key cargada desde `local.properties` mediante `BuildConfig.GEMINI_API_KEY`.

4. Limpieza del proyecto:
   - Se agrega `.gitignore`.
   - Se agrega `README.md`.
   - Se recomienda retirar `local.properties` del repositorio.

## Archivos que debes copiar o reemplazar

Copia estos archivos sobre el proyecto original respetando las rutas:

- `.gitignore`
- `README.md`
- `app/build.gradle`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/example/notesapp/MainActivity.kt`
- `app/src/main/java/com/example/notesapp/ui/theme/Theme.kt`
- `app/src/main/java/com/example/notesapp/preferences/AppPreferencesManager.kt`
- `app/src/main/java/com/example/notesapp/network/GeminiApiService.kt`
- `app/src/main/java/com/example/notesapp/viewmodel/AiViewModel.kt`
- `app/src/main/java/com/example/notesapp/screens/AiAssistantScreen.kt`
- `app/src/main/java/com/example/notesapp/screens/SettingsScreen.kt`
- `app/src/main/java/com/example/notesapp/screens/CalendarScreen.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-es/strings.xml`

## Configurar Gemini API

En `local.properties` agrega:

```properties
GEMINI_API_KEY=TU_CLAVE_DE_GOOGLE_AI_STUDIO
```

No subas ese archivo a GitHub.

## Despues de copiar

1. Abre Android Studio.
2. Ejecuta `Sync Project with Gradle Files`.
3. Revisa que compile.
4. Prueba:
   - Cambio de color desde Ajustes.
   - Cambio de fuente desde Ajustes.
   - Selector de fecha y hora en tareas.
   - Pantalla IA con una solicitud como: `Tengo parcial el viernes y dos trabajos pendientes. Como me organizo?`
