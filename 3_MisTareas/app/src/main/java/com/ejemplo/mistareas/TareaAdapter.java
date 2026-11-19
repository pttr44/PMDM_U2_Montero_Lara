package com.ejemplo.mistareas;

import android.content.Context;
import android.graphics.Paint;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ejemplo.mistareas.databinding.ItemTareaBinding;

import java.util.List;

/**
 * Adapter del RecyclerView: convierte cada Tarea en una tarjeta (item_tarea.xml).
 * Los eventos se delegan en la Activity mediante la interfaz OnTareaListener.
 */
public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {

    public interface OnTareaListener {
        void onTareaPulsada(int posicion);
        void onMenuTarea(View ancla, int posicion);
        void onCrearMenuContextual(ContextMenu menu, int posicion);
    }

    private final List<Tarea> tareas;
    private final OnTareaListener listener;

    public TareaAdapter(List<Tarea> tareas, OnTareaListener listener) {
        this.tareas = tareas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTareaBinding b = ItemTareaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TareaViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        holder.mostrar(tareas.get(position));
    }

    @Override
    public int getItemCount() {
        return tareas.size();
    }

    class TareaViewHolder extends RecyclerView.ViewHolder {

        private final ItemTareaBinding binding;
        private final Context ctx;

        TareaViewHolder(ItemTareaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.ctx = binding.getRoot().getContext();

            // Pulsación corta: marcar/desmarcar como completada
            binding.getRoot().setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) listener.onTareaPulsada(pos);
            });

            // Botón ⋮ de la tarjeta: elegir prioridad
            binding.btnMenu.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) listener.onMenuTarea(v, pos);
            });

            // Pulsación larga: menú contextual (Duplicar / Eliminar)
            binding.getRoot().setOnCreateContextMenuListener((menu, v, info) -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) listener.onCrearMenuContextual(menu, pos);
            });
        }

        void mostrar(Tarea tarea) {
            binding.tvTitulo.setText(tarea.getTitulo());

            // Descripción (opcional)
            String desc = tarea.getDescripcion();
            if (desc != null && !desc.isEmpty()) {
                binding.tvDescripcion.setText(desc);
                binding.tvDescripcion.setVisibility(View.VISIBLE);
            } else {
                binding.tvDescripcion.setVisibility(View.GONE);
            }

            // Prioridad: texto y color
            int textoPrioridad;
            int colorPrioridad;
            switch (tarea.getPrioridad()) {
                case Tarea.PRIORIDAD_ALTA:
                    textoPrioridad = R.string.prioridad_alta;
                    colorPrioridad = R.color.prioridad_alta;
                    break;
                case Tarea.PRIORIDAD_BAJA:
                    textoPrioridad = R.string.prioridad_baja;
                    colorPrioridad = R.color.prioridad_baja;
                    break;
                default:
                    textoPrioridad = R.string.prioridad_media;
                    colorPrioridad = R.color.prioridad_media;
                    break;
            }
            binding.tvPrioridad.setText(textoPrioridad);
            binding.tvPrioridad.setTextColor(ContextCompat.getColor(ctx, colorPrioridad));

            // Completada: título tachado y tarjeta atenuada.
            // (Hay que poner SIEMPRE el estado contrario también, por el reciclaje de vistas.)
            if (tarea.isCompletada()) {
                binding.tvTitulo.setPaintFlags(binding.tvTitulo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.getRoot().setAlpha(0.6f);
            } else {
                binding.tvTitulo.setPaintFlags(binding.tvTitulo.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                binding.getRoot().setAlpha(1f);
            }

            // NUEVO (Ficha 2): mostrar la fecha límite (y hora, ampliación 2) solo si existe
            if (tarea.getFechaLimite() != null) {
                binding.tvFecha.setText(ctx.getString(R.string.fecha_limite, tarea.getFechaHoraLimite()));
                binding.tvFecha.setVisibility(View.VISIBLE);
            } else {
                binding.tvFecha.setVisibility(View.GONE);
            }
        }
    }
}
