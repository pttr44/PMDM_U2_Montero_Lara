package com.ejemplo.enrutamientoavanzado;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ejemplo.enrutamientoavanzado.databinding.FragmentThirdBinding;

/** Ejercicio 3: pantalla de confirmación. */
public class ThirdFragment extends Fragment {

    private FragmentThirdBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentThirdBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Recibir el título con SafeArgs
        if (getArguments() != null) {
            ThirdFragmentArgs args = ThirdFragmentArgs.fromBundle(getArguments());
            binding.textviewThird.setText(
                    getString(R.string.nota_confirmada, args.getTituloNota()));
        }

        // Volver directamente al inicio (la acción del grafo hace popUpTo FirstFragment)
        binding.buttonThird.setOnClickListener(v ->
                NavHostFragment.findNavController(ThirdFragment.this)
                        .navigate(ThirdFragmentDirections.actionThirdFragmentToFirstFragment()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
