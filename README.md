# Organiza tu Dia: Task & Calendar App

Aplicacion movil desarrollada en Kotlin con Jetpack Compose para organizar notas, tareas y eventos en un calendario interactivo.

## Funcionalidades principales

- Crear, editar y eliminar notas.
- Crear, editar, eliminar y completar tareas.
- Asignar prioridad, fecha y hora a cada tarea.
- Visualizar tareas por dia en un calendario mensual.
- Personalizar el color principal de la aplicacion.
- Personalizar el tipo de fuente de la interfaz.
- Consultar un asistente de IA para recibir sugerencias de organizacion y priorizacion.

## Tecnologias utilizadas

- Kotlin
- Android Studio
- Jetpack Compose
- Material 3
- ViewModel
- SharedPreferences para preferencias visuales
- Gemini API por REST para la funcionalidad de IA

## Configuracion de la API de IA

La app usa Gemini API para generar sugerencias de productividad.

1. Crea una clave en Google AI Studio.
2. En el archivo `local.properties`, agrega:

```properties
GEMINI_API_KEY=TU_CLAVE_AQUI
```

3. No subas `local.properties` a GitHub. Este archivo debe permanecer local.

> Nota: Para una entrega academica se puede usar la clave localmente. Para produccion, lo recomendado es consumir la API desde un backend propio para no exponer la clave en la app movil.

## Limpieza recomendada del repositorio

Antes de entregar o compartir el proyecto, ejecuta:

```bash
git rm --cached local.properties
```

Y confirma que existe un `.gitignore` con `local.properties`, `.gradle/`, `.idea/` y carpetas `build/`.

## Como ejecutar el proyecto

1. Clona el repositorio.
2. Abre el proyecto en Android Studio.
3. Sincroniza Gradle.
4. Agrega `GEMINI_API_KEY` en `local.properties` si quieres probar la seccion de IA.
5. Ejecuta la app en un emulador o dispositivo Android.

## Integrantes

- Juan Diego Calderon Bermeo
- Steven Alipio Berrio
- Leon Mateo Velez Gonzalez
