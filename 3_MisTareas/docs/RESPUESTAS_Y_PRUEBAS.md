# Mis Tareas · Ficha 2 · Respuestas y pruebas

Alumno/a: ____________________   Fecha: ____________

## 1. Registro de pruebas (apartado 10 de la ficha)
Marca ✔ y pega la captura donde veas 📷 (captura de pantalla del emulador).

| ✔ | Prueba | Resultado esperado | Captura |
|---|---|---|---|
| ☐ | 📷 Primer arranque | Aparece «¿Cómo te llamas?». Al guardar, la barra muestra «Tareas de …» | |
| ☐ | Cambiar a cuadrícula, cerrar la app (quitarla de recientes) y reabrir | No pregunta el nombre y sigue en cuadrícula | |
| ☐ | 📷 Botón + con título vacío → Guardar | Campo en rojo «Escribe un título»; el diálogo no se cierra | |
| ☐ | 📷 Botón + → Fecha límite → elegir día (y hora) → Guardar | La tarea aparece al final con «Fecha límite: dd/mm/aaaa · HH:mm» | |
| ☐ | Mantener pulsada → Duplicar | Aparece la copia y Toast «Tarea duplicada» | |
| ☐ | 📷 Mantener pulsada → Eliminar → Cancelar | No se borra nada | |
| ☐ | 📷 Mantener pulsada → Eliminar → Eliminar → Deshacer | Desaparece y vuelve a su sitio al pulsar Deshacer | |
| ☐ | Borrar completadas sin ninguna marcada | Toast «No hay tareas completadas» | |
| ☐ | Menú ⋮ → Cambiar nombre | El diálogo muestra el nombre actual y permite cambiarlo | |

Pruebas de las ampliaciones:

| ✔ | Prueba | Resultado esperado | Captura |
|---|---|---|---|
| ☐ | Ampl. 1: Botón + → elegir prioridad Alta → Guardar | La tarjeta muestra «Prioridad alta» en rojo | |
| ☐ | Ampl. 2: elegir fecha y luego hora | La tarjeta muestra fecha y hora | |
| ☐ | Ampl. 3: pulsar ⋮ de una tarjeta | Diálogo con la prioridad actual marcada; al elegir se actualiza | |
| ☐ | Ampl. 4: Menú ⋮ → Modo oscuro, cerrar y reabrir | El tema oscuro se mantiene | |

Opcional (apartado 11): ver `shared_prefs/ajustes_mis_tareas.xml` en *View → Tool Windows → Device Explorer*
(`/data/data/com.ejemplo.mistareas/shared_prefs/`). Contendrá `nombre_usuario`, `vista_cuadricula` y, si lo activas, `modo_oscuro`.

## 2. Ampliaciones realizadas (apartado 5)
| Ejercicio | Qué se ha cambiado |
|---|---|
| **1 · Prioridad en el formulario** | `dialogo_tarea.xml`: `RadioGroup` (Alta/Media/Baja, Media marcada por defecto). `MainActivity.mostrarDialogoNuevaTarea()`: lee `getCheckedRadioButtonId()` y crea la `Tarea` con esa prioridad. |
| **2 · Hora límite** | `Tarea`: nuevo atributo `horaLimite` y `getFechaHoraLimite()`. Tras elegir la fecha en el `DatePickerDialog` se abre un `TimePickerDialog` (formato 12/24 h según el sistema). `TareaAdapter` y «Duplicar» incluyen la hora. |
| **3 · Diálogo de selección** | Se elimina el menú popup. `onMenuTarea()` abre un `MaterialAlertDialogBuilder` con `setSingleChoiceItems(opciones, prioridadActual, …)`; las constantes de prioridad valen 0/1/2 para coincidir con el índice. |
| **4 · Modo oscuro recordado** | `menu_main.xml`: item `checkable`. Se guarda `modo_oscuro` en SharedPreferences y se aplica al arrancar con `AppCompatDelegate.setDefaultNightMode(...)` antes de `super.onCreate()`. Si el usuario no ha elegido, se sigue el modo del sistema. |

## 3. Preguntas de comprobación (apartado 6)

**1. ¿Qué datos conviene guardar en SharedPreferences y cuáles no? Dos ejemplos de cada.**
Conviene guardar ajustes y datos pequeños y sencillos (clave–valor): el nombre del usuario, si la vista es lista o cuadrícula, el tema/idioma, el modo oscuro. No conviene guardar listas o colecciones grandes de datos (como las tareas: para eso se usa una base de datos como Room) ni información sensible como contraseñas o tokens, porque el fichero es XML en texto plano (para eso se usa el Keystore).

**2. ¿Para qué sirve el segundo parámetro de `getBoolean(clave, false)`?**
Es el valor por defecto: lo que devuelve si la clave todavía no existe (por ejemplo, la primera vez que se abre la app). Así nunca hay un valor nulo o un error.

**3. ¿Qué diferencia hay entre `apply()` y `commit()`?**
`apply()` actualiza la copia en memoria al instante y escribe en disco en segundo plano; no bloquea la pantalla y no devuelve resultado. `commit()` escribe de forma síncrona, bloquea el hilo hasta terminar y devuelve un `boolean` indicando si salió bien. En el hilo principal se usa `apply()`.

**4. ¿Cuándo usarías un AlertDialog, un Toast y un Snackbar?**
- **AlertDialog**: cuando hay que pedir una decisión o datos y la acción es importante o irreversible (confirmar eliminar, pedir el nombre, formulario de nueva tarea).
- **Toast**: para informar de algo sin importancia y sin acción posible («Tarea guardada», «Tarea duplicada»).
- **Snackbar**: para informar de algo que se puede deshacer, con un botón de acción («Tarea eliminada · DESHACER»).

**5. ¿Por qué en «Nueva tarea» el listener de Guardar se asigna en `setOnShowListener`?**
Porque si el listener se pone en `setPositiveButton(...)`, Android cierra el diálogo automáticamente después de ejecutarlo, incluso si el título está vacío. Con `setPositiveButton(texto, null)` y asignando el `OnClickListener` al botón cuando el diálogo ya se ha mostrado, somos nosotros quienes decidimos cuándo llamar a `dismiss()`; así se puede validar y dejar el diálogo abierto mostrando el error.

**6. ¿Por qué se suma 1 al mes que devuelve el DatePicker?**
Porque en `Calendar` y en `DatePickerDialog` los meses empiezan en 0 (enero = 0, diciembre = 11). Para mostrar el número de mes que usamos las personas (1–12) hay que sumar 1.

**7. Si cierras la app, ¿qué se conserva y qué se pierde? ¿Por qué?**
Se conservan los ajustes guardados en SharedPreferences (nombre, vista lista/cuadrícula, modo oscuro), porque están en un fichero XML en el almacenamiento privado de la app. Se pierden las tareas que el usuario ha creado, duplicado o eliminado, porque viven solo en memoria (un `ArrayList` en la Activity) y desaparecen cuando el proceso se destruye o la Activity se recrea. Se resolverá con ViewModel y Room.

## 4. Relación con los criterios de evaluación
Preferencias (claves como constantes `PREFS`, `CLAVE_*`) · Diálogos (nombre, nueva tarea con validación, confirmaciones) · Fecha (DatePicker + fecha en la tarjeta) · Avisos (Toast y Snackbar con Deshacer) · Pruebas y documentación (este documento) · Ampliación (4 ejercicios).
