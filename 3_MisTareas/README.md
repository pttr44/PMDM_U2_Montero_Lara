# Mis Tareas · Ficha 2 · Preferencias y diálogos

App Android (Java + XML, Material 3) que recuerda ajustes con **SharedPreferences** y se comunica con el usuario con **AlertDialog, DatePickerDialog, TimePickerDialog, Toast y Snackbar**.

## Cómo abrirla en Android Studio
1. Descomprime el ZIP en una ruta corta **fuera de OneDrive** (p. ej. `C:\AndroidProjects\MisTareas_Ej2`).
2. Android Studio → **File → Open** → selecciona la carpeta `MisTareas_Ej2` (la que contiene `settings.gradle.kts`).
3. Espera a que termine el *Gradle Sync*. Si pide instalar **Android SDK 35**, acepta.
   Si sugiere actualizar el plugin de Gradle (AGP), puedes aceptar o ignorarlo: funciona con ambos.
4. Crea/elige un emulador y pulsa ▶ **Run**.

Requisitos: Android Studio reciente con JDK 17+ (incluido), Internet la primera vez (descarga Gradle y librerías). `minSdk 26`, `compileSdk 35`.

## Qué hace (mapa de la ficha)
| Acción | Resultado | Técnica |
|---|---|---|
| Primera apertura | Pregunta el nombre y muestra «Tareas de …» en la barra | AlertDialog + SharedPreferences |
| Cerrar y reabrir | Recuerda nombre y lista/cuadrícula (y modo oscuro) | SharedPreferences |
| Botón + | Formulario: título, descripción, fecha, hora y prioridad | AlertDialog con vista propia, DatePickerDialog, TimePickerDialog |
| Guardar con título vacío | Error en rojo y el diálogo no se cierra | Validación en `setOnShowListener` |
| Eliminar | Confirmación y después «Deshacer» | AlertDialog + Snackbar |
| Duplicar / guardar | Aviso breve | Toast |
| Tarjeta (⋮) | Elegir prioridad (ampliación 3) | AlertDialog `setSingleChoiceItems` |
| Menú ⋮ → Modo oscuro | Se aplica y se recuerda (ampliación 4) | SharedPreferences + AppCompatDelegate |

## Estructura
```
app/src/main/java/com/ejemplo/mistareas/
    Tarea.java          modelo (con fechaLimite y horaLimite)
    TareaAdapter.java   adapter del RecyclerView
    MainActivity.java   preferencias, diálogos, avisos y menús
app/src/main/res/
    layout/  activity_main, item_tarea, dialogo_nombre, dialogo_tarea
    menu/    menu_main, menu_contextual_tarea
    values/  strings, colors, themes      drawable/  iconos vectoriales
docs/RESPUESTAS_Y_PRUEBAS.md   respuestas del apartado 6 + registro de pruebas
```

## Para probar la «primera vez»
Desinstala la app del emulador o borra sus datos (Ajustes → Apps → Mis Tareas → Almacenamiento → Borrar datos).

## Limitaciones conocidas (previstas en la ficha)
- Las tareas están en memoria: se pierden al cerrar la app o girar el móvil (se resolverá con ViewModel y Room). Los **ajustes sí se conservan**.
- Al activar/desactivar el modo oscuro la pantalla se recrea, así que la lista vuelve a las tareas de ejemplo.
