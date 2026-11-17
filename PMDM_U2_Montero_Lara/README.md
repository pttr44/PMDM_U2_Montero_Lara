Integrantes: Pablo Lara (persona B) y Peter Montero (persona A)

Fase 0: Preparación del entorno (15 minutos - Juntos)
Persona A crea el repositorio PMDM_U2_Apellido1_Apellido2 en GitHub (vacío, sin README), invita a Persona B y a la profesora (si es privado).

Persona A clona el repo, crea el archivo .gitignore con las reglas exactas del enunciado, crea el README.md con vuestros nombres y la carpeta vacía evidencias/, y hace el primer git push.

Persona B clona el repositorio en local (git clone) y verifica que recibe el .gitignore y el README.md.

Fase 1: Desarrollo en paralelo (Bloque 1)
Persona A desarrolla 1_EnrutamientoAvanzado:

Configura nav_graph.xml, fragmentos, SafeArgs y el intent-filter para el deep link notas://detalle/....

Commits recomendados: estructura inicial → navegación y SafeArgs → validación campo vacío y deep link.

Persona B desarrolla 2_MisTareas:

Implementa RecyclerView, Adapter, ViewHolder, tarjetas con colores de prioridad y los 3 menús (opciones, contextual y popup).

Commits recomendados: estructura inicial → RecyclerView y prioridades → menú opciones/cuadrícula → menús contextual y popup.

Fase 2: Punto de control y bifurcación (Bloque 2)
Persona A comienza 4_MiBiblioteca:

Crea Entidad, DAO, Room Database y la gestión de hilos/corutinas para no bloquear la UI, más el Snackbar con deshacer.

Commits recomendados: estructura y dependencias Room → Entidad, DAO y DB → UI y operaciones CRUD en hilos → validación y Snackbar.

Persona B duplica y evoluciona a 3_MisTareas2:

Importante: Copia la carpeta 2_MisTareas completa, la renombra a 3_MisTareas2 (asegurándose de no arrastrar carpetas build/ ocultas o limpiar el proyecto antes) y hace un commit solo con la copia base.

Añade SharedPreferences (nombre de usuario y persistencia de vista cuadrícula), AlertDialog y DatePicker.

Commits recomendados: copia base desde MisTareas → diálogo inicial y SharedPreferences → diálogo nueva tarea con DatePicker → confirmación borrar y Snackbar.

Fase 3: Recta final y QA cruzado (Bloque 3)
Persona B desarrolla 5_NoticiasFP:

Implementa tema (incluyendo values-night para modo oscuro), Toolbar, NavigationDrawer, CardView, Chip y FAB dinámico.

Commits recomendados: estructura y tema claro/oscuro → Drawer y Toolbar → tarjetas, chips y filtrado → FAB dinámico y favoritos.

Persona A (que ya habrá terminado P4) hace QA y capturas de P1, P2 y P3:

Hace git pull, abre los proyectos de Persona B en su Android Studio para garantizar que compilan en otro equipo, ejecuta todas las pruebas de la guía y toma las capturas del emulador (Ctrl + S).

Fase 4: Documentación PDF y verificación final (Juntos)
Persona B saca las capturas de P4 y P5 (probando también la app de Persona A en su equipo).

Montaje del PDF (Hito2_Evidencias_Apellido1_Apellido2.pdf):

Persona A monta la portada (con el enlace al repo), el apartado de P1, P2 y P3 en un Google Doc compartido.

Persona B añade P4, P5 y la captura final de la pestaña Commits de GitHub donde se ven los commits de ambos.

Exportamos a PDF, lo guardamos dentro de evidencias/ y hacéis el git push final.