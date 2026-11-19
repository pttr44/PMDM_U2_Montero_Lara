package com.ejemplo.mibiblioteca;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * DAO (Data Access Object): las operaciones sobre la tabla.
 * Solo escribimos el QUÉ; Room genera el código que lo hace.
 */
@Dao
public interface LibroDao {

    @Query("SELECT * FROM libros ORDER BY titulo")
    List<Libro> obtenerTodos();

    @Query("SELECT COUNT(*) FROM libros WHERE leido = 1")
    int contarLeidos();

    // ---------- AMPLIACIÓN (Ejercicio 1 · Buscar) ----------
    // El texto se pasa ya con los comodines: "%" + texto + "%"
    @Query("SELECT * FROM libros WHERE titulo LIKE :texto ORDER BY titulo")
    List<Libro> buscarPorTitulo(String texto);

    // ---------- AMPLIACIÓN (Ejercicio 2 · Solo pendientes) ----------
    @Query("SELECT * FROM libros WHERE leido = 0 ORDER BY titulo")
    List<Libro> obtenerPendientes();

    // Combina los dos ejercicios: pendientes Y que coincidan con la búsqueda
    @Query("SELECT * FROM libros WHERE leido = 0 AND titulo LIKE :texto ORDER BY titulo")
    List<Libro> buscarPendientesPorTitulo(String texto);

    // Total de libros guardados (el subtítulo no debe cambiar al filtrar)
    @Query("SELECT COUNT(*) FROM libros")
    int contarTodos();

    @Insert
    long insertar(Libro libro);      // devuelve el id generado

    @Update
    void actualizar(Libro libro);

    @Delete
    void eliminar(Libro libro);
}
