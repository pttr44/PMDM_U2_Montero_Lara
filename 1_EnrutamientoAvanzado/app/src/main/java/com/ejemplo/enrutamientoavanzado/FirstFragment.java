package com.ejemplo.enrutamientoavanzado;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ejemplo.enrutamientoavanzado.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // (A) Inflar el layout con View Binding
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // (B) Evento: pulsar el botón
        binding.buttonFirst.setOnClickListener(v -> {
            String textoNota = binding.etInputNota.getText().toString().trim();

            // (C) Validación
            if (textoNota.isEmpty()) {
                Toast.makeText(getContext(), R.string.error_titulo_vacio,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // (D) SafeArgs: ruta + argumento.
            // Como en el ejercicio 2 "tituloNota" tiene valor por defecto, ya no es un
            // parámetro obligatorio del método: se asigna con el setter generado.
            FirstFragmentDirections.ActionFirstFragmentToSecondFragment action =
                    FirstFragmentDirections.actionFirstFragmentToSecondFragment();
            action.setTituloNota(textoNota);

            // (E) Navegar
            NavHostFragment.findNavController(FirstFragment.this).navigate(action);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // (F) Evitar fugas de memoria
    }
}
