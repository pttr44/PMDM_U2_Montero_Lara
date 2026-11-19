# Mi Biblioteca · Room e hilos (con ampliaciones 1 y 2)

Ampliaciones incluidas:
- Ejercicio 1 · Buscar por título (campo de búsqueda + @Query con LIKE)
- Ejercicio 2 · Ver solo pendientes (menú de opciones + @Query con leido = 0)

## Cómo abrirlo en Android Studio
1. Crea el proyecto como indica el paso 1 de la ficha (Empty Views Activity, nombre MiBiblioteca,
   paquete com.ejemplo.mibiblioteca, Java, SDK mínimo API 24), FUERA de OneDrive.
   Así Android Studio genera el wrapper de Gradle, el tema y los iconos.
2. Copia el contenido de app/src/main/ de este zip sobre el de tu proyecto (sobrescribe MainActivity,
   activity_main.xml y strings.xml; añade el resto de ficheros).
3. Aplica lo de la carpeta gradle_anadir/ (libs.versions.toml y app/build.gradle.kts) y pulsa Sync Now.
4. Ejecuta en el emulador.

## Estructura
app/src/main/java/com/ejemplo/mibiblioteca/
    Libro.java · LibroDao.java · AppDatabase.java · LibroAdapter.java · MainActivity.java
app/src/main/res/
    layout/activity_main.xml · layout/item_libro.xml · menu/menu_main.xml
    values/strings.xml · drawable/ic_delete.xml
