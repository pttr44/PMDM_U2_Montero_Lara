package com.ejemplo.mibiblioteca;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ejemplo.mibiblioteca.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements LibroAdapter.OnLibroListener {

    private ActivityMainBinding binding;
    private LibroDao dao;
    private LibroAdapter adapter;

    // HILO SECUNDARIO: aquí se hace TODO el trabajo con la base de datos
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // ---------- AMPLIACIÓN: estado de los filtros ----------
    // volatile: se escriben en el hilo principal y se leen desde el hilo secundario
    private volatile String textoBusqueda = "";      // Ejercicio 1 (Buscar)
    private volatile boolean soloPendientes = false; // Ejercicio 2 (Solo pendientes)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });
        setSupportActionBar(binding.toolbar);

        // 1) Obtener la base de datos y su DAO
        dao = AppDatabase.getInstance(this).libroDao();

        // 2) Preparar la lista
        adapter = new LibroAdapter(this);
        binding.rvLibros.setLayoutManager(new LinearLayoutManager(this));
        binding.rvLibros.setAdapter(adapter);

        binding.btnAnadir.setOnClickListener(v -> anadirLibro());

        // Ejercicio 1: cada vez que cambia el texto del buscador, se vuelve a leer la lista filtrada
        binding.etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                textoBusqueda = s.toString().trim();
                cargarLibros();
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        // 3) Leer lo que ya había guardado
        cargarLibros();
    }

    // =====================================================================
    // MENÚ (Ejercicio 2): «Ver solo pendientes»
    // =====================================================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_pendientes) {
            item.setChecked(!item.isChecked());   // marca / desmarca la opción del menú
            soloPendientes = item.isChecked();
            cargarLibros();                       // la consulta se hace en el hilo secundario
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // =====================================================================
    // LEER: hilo secundario -> resultado al hilo principal
    // =====================================================================
    private void cargarLibros() {
        executor.execute(() -> {
            // Esto se ejecuta en el HILO SECUNDARIO (no puede tocar la interfaz)
            String busqueda = textoBusqueda;      // copia local: el valor no cambia a mitad de la consulta
            boolean pendientes = soloPendientes;
            boolean hayFiltro = pendientes || !busqueda.isEmpty();
            String patron = "%" + busqueda + "%";

            List<Libro> lista;
            if (pendientes) {
                lista = busqueda.isEmpty() ? dao.obtenerPendientes()
                                           : dao.buscarPendientesPorTitulo(patron);
            } else {
                lista = busqueda.isEmpty() ? dao.obtenerTodos()
                                           : dao.buscarPorTitulo(patron);
            }
            int total = dao.contarTodos();
            int leidos = dao.contarLeidos();

            // Volvemos al HILO PRINCIPAL para pintar
            runOnUiThread(() -> mostrar(lista, total, leidos, hayFiltro));
        });
    }

    private void mostrar(List<Libro> lista, int total, int leidos, boolean hayFiltro) {
        adapter.setLibros(lista);

        // Si la lista está vacía distinguimos: no hay libros / ningún libro cumple el filtro
        binding.tvVacio.setText(hayFiltro ? R.string.sin_resultados : R.string.sin_libros);
        binding.tvVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);

        // El subtítulo siempre muestra los totales de la biblioteca, no de la lista filtrada
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(getString(R.string.resumen_libros, total, leidos));
        }
    }

    // =====================================================================
    // INSERTAR
    // =====================================================================
    private void anadirLibro() {
        String titulo = texto(binding.etTitulo.getText());
        String autor = texto(binding.etAutor.getText());
        String paginasTexto = texto(binding.etPaginas.getText());

        binding.tilTitulo.setError(null);
        binding.tilAutor.setError(null);
        binding.tilPaginas.setError(null);

        boolean correcto = true;
        if (titulo.isEmpty()) { binding.tilTitulo.setError(getString(R.string.error_obligatorio)); correcto = false; }
        if (autor.isEmpty()) { binding.tilAutor.setError(getString(R.string.error_obligatorio)); correcto = false; }
        int paginas = 0;
        try { paginas = Integer.parseInt(paginasTexto); } catch (NumberFormatException ignored) { }
        if (paginas <= 0) { binding.tilPaginas.setError(getString(R.string.error_paginas)); correcto = false; }
        if (!correcto) return;

        Libro nuevo = new Libro(titulo, autor, paginas);

        executor.execute(() -> {
            dao.insertar(nuevo);                          // hilo secundario
            runOnUiThread(() -> {                         // hilo principal
                binding.etTitulo.setText("");
                binding.etAutor.setText("");
                binding.etPaginas.setText("");
                binding.etTitulo.requestFocus();
                Toast.makeText(this, R.string.libro_guardado, Toast.LENGTH_SHORT).show();
            });
            cargarLibros();                               // volver a leer para refrescar la lista
        });
    }

    // =====================================================================
    // ACTUALIZAR y ELIMINAR (avisos que llegan desde el adaptador)
    // =====================================================================
    @Override
    public void onCambiarLeido(Libro libro, boolean leido) {
        libro.leido = leido;
        executor.execute(() -> {
            dao.actualizar(libro);
            cargarLibros();   // con «Solo pendientes» activo, el libro marcado desaparece de la lista
        });
    }

    @Override
    public void onEliminar(Libro libro) {
        executor.execute(() -> {
            dao.eliminar(libro);
            cargarLibros();
            runOnUiThread(() ->
                    Snackbar.make(binding.getRoot(), R.string.libro_eliminado, Snackbar.LENGTH_LONG)
                            .setAction(R.string.deshacer, v -> executor.execute(() -> {
                                dao.insertar(libro);      // se vuelve a insertar con el mismo id
                                cargarLibros();
                            }))
                            .show());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();   // liberar el hilo al cerrar la pantalla
    }

    private String texto(CharSequence cs) {
        return cs == null ? "" : cs.toString().trim();
    }
}
