package com.ejemplo.mistareas;

/**
 * Modelo de datos: una tarea de la lista.
 */
public class Tarea {

    // Valores 0, 1, 2: coinciden con el orden del diálogo de selección (ampliación 3).
    public static final int PRIORIDAD_ALTA = 0;
    public static final int PRIORIDAD_MEDIA = 1;
    public static final int PRIORIDAD_BAJA = 2;

    private String titulo;
    private String descripcion;
    private int prioridad;
    private boolean completada;

    private String fechaLimite; // NUEVO (Ficha 2): puede ser null si no se elige fecha
    private String horaLimite;  // AMPLIACIÓN 2: puede ser null si no se elige hora

    public Tarea(String titulo, String descripcion, int prioridad) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.completada = false;
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }

    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }

    public String getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(String fechaLimite) { this.fechaLimite = fechaLimite; }

    public String getHoraLimite() { return horaLimite; }
    public void setHoraLimite(String horaLimite) { this.horaLimite = horaLimite; }

    /** Texto "dd/mm/aaaa" o "dd/mm/aaaa · HH:mm"; null si no hay fecha. */
    public String getFechaHoraLimite() {
        if (fechaLimite == null) return null;
        return horaLimite == null ? fechaLimite : fechaLimite + " · " + horaLimite;
    }
}
