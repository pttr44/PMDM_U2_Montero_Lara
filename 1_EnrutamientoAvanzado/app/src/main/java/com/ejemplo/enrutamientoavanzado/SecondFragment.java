package com.ejemplo.enrutamientoavanzado;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ejemplo.enrutamientoavanzado.databinding.FragmentSecondBinding;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private String tituloNota = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // (A) Recibir el argumento con SafeArgs
        if (getArguments() != null) {
            SecondFragmentArgs args = SecondFragmentArgs.fromBundle(getArguments());
            tituloNota = args.getTituloNota();
            binding.textviewSecond.setText(tituloNota);
        }

        // (B) Volver: sacar esta pantalla de la pila
        binding.buttonSecond.setOnClickListener(v ->
                NavHostFragment.findNavController(SecondFragment.this)
                        .popBackStack());

        // (C) Ejercicio 3: ir a la pantalla de confirmación pasando el título
        binding.buttonConfirm.setOnClickListener(v ->
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(SecondFragmentDirections
                                .actionSecondFragmentToThirdFragment(tituloNota)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
