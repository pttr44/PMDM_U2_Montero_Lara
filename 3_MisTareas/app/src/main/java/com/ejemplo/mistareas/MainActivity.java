package com.ejemplo.mistareas;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ejemplo.mistareas.databinding.ActivityMainBinding;
import com.ejemplo.mistareas.databinding.DialogoNombreBinding;
import com.ejemplo.mistareas.databinding.DialogoTareaBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TareaAdapter.OnTareaListener {

    // ---- SharedPreferences: nombre del fichero y claves ----
    private static final String PREFS = "ajustes_mis_tareas";
    private static final String CLAVE_VISTA_CUADRICULA = "vista_cuadricula";
    private static final String CLAVE_NOMBRE = "nombre_usuario";
    private static final String CLAVE_MODO_OSCURO = "modo_oscuro"; // ampliación 4

    private ActivityMainBinding binding;
    private SharedPreferences prefs;
    private final List<Tarea> tareas = new ArrayList<>();
    private TareaAdapter adapter;

    private boolean vistaCuadricula = false;
    private int posicionSeleccionada = RecyclerView.NO_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // AMPLIACIÓN 4: el modo oscuro hay que aplicarlo ANTES de super.onCreate()
        // para que la pantalla arranque ya con el tema correcto (sin parpadeo).
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        aplicarModoOscuroGuardado();

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

        // ---- 1) LEER las preferencias guardadas ----
        vistaCuadricula = prefs.getBoolean(CLAVE_VISTA_CUADRICULA, false); // false si no existe
        String nombre = prefs.getString(CLAVE_NOMBRE, null);               // null si no existe

        cargarTareasDeEjemplo();
        adapter = new TareaAdapter(tareas, this);
        binding.rvTareas.setAdapter(adapter);
        aplicarLayoutManager();

        // FAB: ahora abre un diálogo para crear la tarea
        binding.fab.setOnClickListener(v -> mostrarDialogoNuevaTarea());

        // Primera vez que se abre la app: pedir el nombre
        if (nombre == null) {
            mostrarDialogoNombre();
        } else {
            mostrarSubtitulo(nombre);
        }
    }

    private void cargarTareasDeEjemplo() {
        tareas.add(new Tarea("Repasar layouts", "LinearLayout, ConstraintLayout y FrameLayout", Tarea.PRIORIDAD_MEDIA));
        tareas.add(new Tarea("Practicar RecyclerView", "Adapter + ViewHolder", Tarea.PRIORIDAD_ALTA));
        tareas.add(new Tarea("Probar los diálogos", "AlertDialog, DatePicker, Toast y Snackbar", Tarea.PRIORIDAD_ALTA));
        tareas.add(new Tarea("Guardar ajustes", "SharedPreferences", Tarea.PRIORIDAD_MEDIA));
        tareas.add(new Tarea("Entregar la práctica", "Subir el .zip a la plataforma", Tarea.PRIORIDAD_BAJA));
    }

    private void aplicarLayoutManager() {
        if (vistaCuadricula) {
            binding.rvTareas.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            binding.rvTareas.setLayoutManager(new LinearLayoutManager(this));
        }
        invalidateOptionsMenu();
    }

    private void mostrarSubtitulo(String nombre) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(getString(R.string.subtitulo_tareas_de, nombre));
        }
    }

    // =====================================================================
    // PREFERENCIAS + DIÁLOGO: pedir y guardar el nombre
    // =====================================================================
    private void mostrarDialogoNombre() {
        DialogoNombreBinding d = DialogoNombreBinding.inflate(getLayoutInflater());
        d.etNombre.setText(prefs.getString(CLAVE_NOMBRE, "")); // si ya existe, lo mostramos

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialogo_nombre_titulo)
                .setMessage(R.string.dialogo_nombre_mensaje)
                .setView(d.getRoot())
                .setPositiveButton(R.string.guardar, (dialog, which) -> {
                    String nombre = d.etNombre.getText() == null ? "" : d.etNombre.getText().toString().trim();
                    if (nombre.isEmpty()) return;
                    // ---- 2) ESCRIBIR en las preferencias ----
                    prefs.edit().putString(CLAVE_NOMBRE, nombre).apply();
                    mostrarSubtitulo(nombre);
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    // =====================================================================
    // ALERTDIALOG con vista propia + DATEPICKER + TIMEPICKER: crear una tarea
    // =====================================================================
    private void mostrarDialogoNuevaTarea() {
        DialogoTareaBinding d = DialogoTareaBinding.inflate(getLayoutInflater());
        final String[] fechaElegida = {null}; // array para poder modificarlo dentro de la lambda
        final String[] horaElegida = {null};  // ampliación 2

        // Botón "Fecha límite" -> DatePickerDialog
        d.btnElegirFecha.setOnClickListener(v -> {
            Calendar hoy = Calendar.getInstance();
            new DatePickerDialog(this,
                    (picker, anio, mes, dia) -> {
                        // OJO: el mes empieza en 0 (enero = 0)
                        fechaElegida[0] = String.format(Locale.getDefault(), "%02d/%02d/%d", dia, mes + 1, anio);
                        horaElegida[0] = null;
                        d.tvFechaElegida.setText(fechaElegida[0]);

                        // AMPLIACIÓN 2: tras elegir la fecha, pedimos también la hora
                        Calendar ahora = Calendar.getInstance();
                        new TimePickerDialog(this,
                                (tp, hora, minuto) -> {
                                    horaElegida[0] = String.format(Locale.getDefault(), "%02d:%02d", hora, minuto);
                                    d.tvFechaElegida.setText(fechaElegida[0] + " · " + horaElegida[0]);
                                },
                                ahora.get(Calendar.HOUR_OF_DAY), ahora.get(Calendar.MINUTE),
                                DateFormat.is24HourFormat(this))
                                .show();
                    },
                    hoy.get(Calendar.YEAR), hoy.get(Calendar.MONTH), hoy.get(Calendar.DAY_OF_MONTH))
                    .show();
        });

        AlertDialog dialogo = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.nueva_tarea)
                .setView(d.getRoot())
                .setPositiveButton(R.string.guardar, null) // el listener se asigna abajo
                .setNegativeButton(R.string.cancelar, null)
                .create();

        // Asignamos el listener DESPUÉS de mostrarlo para poder validar sin que se cierre solo
        dialogo.setOnShowListener(di ->
                dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    String titulo = d.etTitulo.getText() == null ? "" : d.etTitulo.getText().toString().trim();
                    String descripcion = d.etDescripcion.getText() == null ? "" : d.etDescripcion.getText().toString().trim();

                    if (titulo.isEmpty()) {
                        d.tilTitulo.setError(getString(R.string.error_titulo_vacio));
                        return; // el diálogo sigue abierto
                    }

                    // AMPLIACIÓN 1: prioridad elegida en el RadioGroup
                    int prioridad = Tarea.PRIORIDAD_MEDIA;
                    int marcado = d.rgPrioridad.getCheckedRadioButtonId();
                    if (marcado == R.id.rbAlta) {
                        prioridad = Tarea.PRIORIDAD_ALTA;
                    } else if (marcado == R.id.rbBaja) {
                        prioridad = Tarea.PRIORIDAD_BAJA;
                    }

                    Tarea nueva = new Tarea(titulo, descripcion, prioridad);
                    nueva.setFechaLimite(fechaElegida[0]);
                    nueva.setHoraLimite(horaElegida[0]);
                    tareas.add(nueva);
                    adapter.notifyItemInserted(tareas.size() - 1);
                    binding.rvTareas.scrollToPosition(tareas.size() - 1);

                    Toast.makeText(this, R.string.tarea_guardada, Toast.LENGTH_SHORT).show();
                    dialogo.dismiss();
                }));
        dialogo.show();
    }

    // =====================================================================
    // MENÚ DE OPCIONES
    // =====================================================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_cambiar_vista);
        if (vistaCuadricula) {
            item.setIcon(R.drawable.ic_view_list);
            item.setTitle(R.string.ver_lista);
        } else {
            item.setIcon(R.drawable.ic_grid_view);
            item.setTitle(R.string.ver_cuadricula);
        }
        // AMPLIACIÓN 4: casilla "Modo oscuro" reflejando el estado real
        menu.findItem(R.id.action_modo_oscuro).setChecked(modoOscuroActivo());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cambiar_vista) {
            vistaCuadricula = !vistaCuadricula;
            // Guardamos la preferencia: al volver a abrir la app se recordará
            prefs.edit().putBoolean(CLAVE_VISTA_CUADRICULA, vistaCuadricula).apply();
            aplicarLayoutManager();
            return true;
        } else if (id == R.id.action_borrar_completadas) {
            confirmarBorrarCompletadas();
            return true;
        } else if (id == R.id.action_cambiar_nombre) {
            mostrarDialogoNombre();
            return true;
        } else if (id == R.id.action_modo_oscuro) {
            // AMPLIACIÓN 4: un item "checkable" no cambia solo; lo invertimos nosotros
            boolean activar = !item.isChecked();
            item.setChecked(activar);
            prefs.edit().putBoolean(CLAVE_MODO_OSCURO, activar).apply();
            AppCompatDelegate.setDefaultNightMode(
                    activar ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ---- AMPLIACIÓN 4: modo oscuro recordado ----
    private void aplicarModoOscuroGuardado() {
        if (prefs.contains(CLAVE_MODO_OSCURO)) {
            boolean oscuro = prefs.getBoolean(CLAVE_MODO_OSCURO, false);
            AppCompatDelegate.setDefaultNightMode(
                    oscuro ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            // El usuario aún no ha elegido: se sigue el ajuste del sistema
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    private boolean modoOscuroActivo() {
        if (prefs.contains(CLAVE_MODO_OSCURO)) {
            return prefs.getBoolean(CLAVE_MODO_OSCURO, false);
        }
        int modo = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return modo == Configuration.UI_MODE_NIGHT_YES;
    }

    // AlertDialog de confirmación antes de una acción que borra varias cosas
    private void confirmarBorrarCompletadas() {
        int total = 0;
        for (Tarea t : tareas) if (t.isCompletada()) total++;

        if (total == 0) {
            Toast.makeText(this, R.string.sin_completadas, Toast.LENGTH_SHORT).show();
            return;
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.confirmar_borrar_titulo)
                .setMessage(getString(R.string.confirmar_borrar_mensaje, total))
                .setPositiveButton(R.string.borrar, (dialog, which) -> borrarCompletadas())
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    private void borrarCompletadas() {
        int borradas = 0;
        for (int i = tareas.size() - 1; i >= 0; i--) {
            if (tareas.get(i).isCompletada()) {
                tareas.remove(i);
                adapter.notifyItemRemoved(i);
                borradas++;
            }
        }
        Toast.makeText(this, getString(R.string.tareas_borradas, borradas), Toast.LENGTH_SHORT).show();
    }

    // =====================================================================
    // EVENTOS DE CADA FILA
    // =====================================================================
    @Override
    public void onTareaPulsada(int posicion) {
        Tarea t = tareas.get(posicion);
        t.setCompletada(!t.isCompletada());
        adapter.notifyItemChanged(posicion);
    }

    // AMPLIACIÓN 3: el menú popup de prioridad se sustituye por un AlertDialog
    // con selección única que marca la prioridad actual.
    @Override
    public void onMenuTarea(View ancla, int posicion) {
        Tarea t = tareas.get(posicion);
        String[] opciones = {
                getString(R.string.prioridad_alta),
                getString(R.string.prioridad_media),
                getString(R.string.prioridad_baja)
        };
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.elegir_prioridad)
                .setSingleChoiceItems(opciones, t.getPrioridad(), (dialog, which) -> {
                    // which = 0 alta, 1 media, 2 baja (coincide con las constantes de Tarea)
                    t.setPrioridad(which);
                    adapter.notifyItemChanged(posicion);
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    @Override
    public void onCrearMenuContextual(ContextMenu menu, int posicion) {
        posicionSeleccionada = posicion;
        getMenuInflater().inflate(R.menu.menu_contextual_tarea, menu);
        menu.setHeaderTitle(tareas.get(posicion).getTitulo());
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int pos = posicionSeleccionada;
        if (pos == RecyclerView.NO_POSITION || pos >= tareas.size()) {
            return super.onContextItemSelected(item);
        }
        int id = item.getItemId();
        if (id == R.id.action_duplicar) {
            Tarea original = tareas.get(pos);
            Tarea copia = new Tarea(getString(R.string.copia_de, original.getTitulo()),
                    original.getDescripcion(), original.getPrioridad());
            copia.setFechaLimite(original.getFechaLimite());
            copia.setHoraLimite(original.getHoraLimite());
            tareas.add(pos + 1, copia);
            adapter.notifyItemInserted(pos + 1);
            Toast.makeText(this, R.string.tarea_duplicada, Toast.LENGTH_SHORT).show(); // TOAST
            return true;
        } else if (id == R.id.action_eliminar) {
            confirmarEliminar(pos);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    // =====================================================================
    // ALERTDIALOG de confirmación + SNACKBAR con "Deshacer"
    // =====================================================================
    private void confirmarEliminar(int pos) {
        Tarea t = tareas.get(pos);
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.confirmar_eliminar_titulo)
                .setMessage(getString(R.string.confirmar_eliminar_mensaje, t.getTitulo()))
                .setPositiveButton(R.string.eliminar, (dialog, which) -> eliminarConDeshacer(pos))
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    private void eliminarConDeshacer(int pos) {
        Tarea borrada = tareas.remove(pos);
        adapter.notifyItemRemoved(pos);

        Snackbar.make(binding.getRoot(), R.string.tarea_eliminada, Snackbar.LENGTH_LONG)
                .setAnchorView(binding.fab)   // que no tape el botón flotante
                .setAction(R.string.deshacer, v -> {
                    int destino = Math.min(pos, tareas.size());
                    tareas.add(destino, borrada);
                    adapter.notifyItemInserted(destino);
                })
                .show();
    }
}
