package com.mk.pomodoro.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mk.pomodoro.R;

public class SettingsFragment extends Fragment {

    private LinearLayoutCompat llcTema;
    private ConstraintLayout clClasico, clExtendido, clCorto, clPersonalizado;
    private ImageView ivClasico, ivExtendido, ivCorto, ivPersonalizado;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        llcTema = view.findViewById(R.id.llTheme);
        llcTema.setOnClickListener(v -> mostrarDialogoTema(v));

        ivClasico = view.findViewById(R.id.ivClassic);
        ivExtendido = view.findViewById(R.id.ivExtended);
        ivCorto = view.findViewById(R.id.ivShort);
        ivPersonalizado = view.findViewById(R.id.ivPersonalized);

        clClasico = view.findViewById(R.id.clClassic);
        clExtendido = view.findViewById(R.id.clExtended);
        clCorto = view.findViewById(R.id.clShort);
        clPersonalizado = view.findViewById(R.id.clPersonalized);


        clClasico.setOnClickListener(v -> activarVisibilidad(View.VISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE));
        clExtendido.setOnClickListener(v -> activarVisibilidad(View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE));
        clCorto.setOnClickListener(v -> activarVisibilidad(View.INVISIBLE, View.INVISIBLE, View.VISIBLE, View.INVISIBLE));
        clPersonalizado.setOnClickListener(v -> activarVisibilidad(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE));
    }

    private void activarVisibilidad(int visibilityClasico, int visibilityExtendido, int visibilityCorto, int visibilityPersonalizado) {
        animarVisibilidad(ivClasico, visibilityClasico);
        animarVisibilidad(ivExtendido, visibilityExtendido);
        animarVisibilidad(ivCorto, visibilityCorto);
        animarVisibilidad(ivPersonalizado, visibilityPersonalizado);
    }

    private void animarVisibilidad(ImageView imageView, int visibility) {
        if (visibility == View.VISIBLE && imageView.getVisibility() != View.VISIBLE) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setAlpha(0f);
            imageView.animate().alpha(1f).setDuration(750);
        } else if (visibility != View.VISIBLE) {
            imageView.setVisibility(View.INVISIBLE);
        }
    }




    private void mostrarDialogoTema(View v) {
        String[] temas = {"Sistema", "Claro", "Oscuro"};

        Context context = getContext();
        if (context != null) {
            // Obtener la preferencia de tema actual
            SharedPreferences sharedPreferences = context.getSharedPreferences("minka", Context.MODE_PRIVATE);
            int temaActual = sharedPreferences.getInt("tema", 0);

            // Crear un nuevo diálogo
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Tema")
                    .setSingleChoiceItems(temas, temaActual, null)
                    .setPositiveButton("Aplicar", (dialogInterface, i) -> {
                        int selectedPosition = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();

                        if (selectedPosition != -1) {
                            actualizarTema(selectedPosition, sharedPreferences);
                        }
                        v.setPressed(false);
                    })
                    .setNegativeButton("Cancelar", (dialogInterface, i) -> v.setPressed(false))
                    .show();
        }
    }

    private void actualizarTema(int temaSeleccionado, SharedPreferences sharedPreferences) {
        // Guardar la preferencia de tema
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tema", temaSeleccionado);
        editor.apply();

        switch (temaSeleccionado) {
            case 0: // Sistema
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case 1: // Claro
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 2: // Oscuro
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }
}