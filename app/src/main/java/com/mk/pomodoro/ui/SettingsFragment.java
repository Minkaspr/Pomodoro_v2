package com.mk.pomodoro.ui;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
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
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.mk.pomodoro.R;
import com.mk.pomodoro.ui.viewmodel.PomodoroTypeTimeViewModel;

public class SettingsFragment extends Fragment implements CustomTimerBottomSheet.OnOptionChangeListener{

    private LinearLayoutCompat llcTema, llcSonido, llcVibracion;
    private ConstraintLayout clClasico, clExtendido, clCorto, clPersonalizado;
    private ImageView ivClasico, ivExtendido, ivCorto, ivPersonalizado;
    private MaterialSwitch sSonido, sVibracion;

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
        llcTema.setOnClickListener(this::mostrarDialogoTema);
        llcSonido = view.findViewById(R.id.llSound);
        llcVibracion = view.findViewById(R.id.llVibration);
        sSonido = view.findViewById(R.id.switch_sound);
        sVibracion = view.findViewById(R.id.switch_vibration);
        verificarSonidoVibracion();
        sSonido.setClickable(false);
        sVibracion.setClickable(false);

        llcSonido.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("minka", MODE_PRIVATE);
            boolean sonidoHabilitado = sharedPreferences.getBoolean("sonido", true);

            // Invierte el estado (activo a inactivo o viceversa)
            sonidoHabilitado = !sonidoHabilitado;
            actualizarSwitchSonido(sonidoHabilitado);
        });

        llcVibracion.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("minka", MODE_PRIVATE);
            boolean vibracionHabilitada = sharedPreferences.getBoolean("vibracion", true);

            // Invierte el estado (activo a inactivo o viceversa)
            vibracionHabilitada = !vibracionHabilitada;
            actualizarSwitchVibracion(vibracionHabilitada);
        });

        ivClasico = view.findViewById(R.id.ivClassic);
        ivExtendido = view.findViewById(R.id.ivExtended);
        ivCorto = view.findViewById(R.id.ivShort);
        ivPersonalizado = view.findViewById(R.id.ivPersonalized);

        clClasico = view.findViewById(R.id.clClassic);
        clExtendido = view.findViewById(R.id.clExtended);
        clCorto = view.findViewById(R.id.clShort);
        clPersonalizado = view.findViewById(R.id.clPersonalized);

        clExtendido.setOnClickListener(v -> {
            activarVisibilidad(View.VISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
            guardarTiempos(45, 15);
            guardarOpcionSeleccionada(1);
        });
        clClasico.setOnClickListener(v -> {
            activarVisibilidad(View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
            guardarOpcionSeleccionada(2);
            guardarTiempos(25, 5);
        });
        clCorto.setOnClickListener(v -> {
            activarVisibilidad(View.INVISIBLE, View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
            guardarOpcionSeleccionada(3);
            guardarTiempos(10, 2);
        });

        clPersonalizado.setOnClickListener(v -> {
            activarVisibilidad(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
            CustomTimerBottomSheet bottomSheet = CustomTimerBottomSheet.newInstance();
            bottomSheet.setOnOptionChangeListener(this);
            bottomSheet.setOnCloseListener(() -> {
                Activity activity = getActivity();
                if (activity != null) {
                    SharedPreferences sharedPreferences = activity.getSharedPreferences("minka", MODE_PRIVATE);
                    int ultimaOpcionSeleccionada = sharedPreferences.getInt("opcionSeleccionada", 2);
                    simularClick(ultimaOpcionSeleccionada);
                }
            });
            if (!bottomSheet.isAdded() && bottomSheet.getShouldShowBottomSheet()) {
                bottomSheet.show(getParentFragmentManager(), "CustomTimerBottomSheet");
            }
        });


        Activity activity = getActivity();
        if (activity != null) {
            SharedPreferences sharedPreferences = activity.getSharedPreferences("minka", MODE_PRIVATE);
            int opcionSeleccionada = sharedPreferences.getInt("opcionSeleccionada", 2);
            actualizarVisibilidad(opcionSeleccionada);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Vuelve a observar los cambios en la opción seleccionada en tiempo real
        PomodoroTypeTimeViewModel pomodoroTypeTime = new ViewModelProvider(requireActivity()).get(PomodoroTypeTimeViewModel.class);
        pomodoroTypeTime.getOpcionSeleccionada().observe(getViewLifecycleOwner(), this::actualizarVisibilidad);
    }

    @Override
    public void onOptionChange(int option) {
        simularClick(option);
    }

    private void activarVisibilidad(int visibilityExtendido, int visibilityClasico, int visibilityCorto, int visibilityPersonalizado) {
        animarVisibilidad(ivExtendido, visibilityExtendido);
        animarVisibilidad(ivClasico, visibilityClasico);
        animarVisibilidad(ivCorto, visibilityCorto);
        animarVisibilidad(ivPersonalizado, visibilityPersonalizado);
    }

    private void animarVisibilidad(ImageView imageView, int visibility) {
        if (visibility == View.VISIBLE && imageView.getVisibility() != View.VISIBLE) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setAlpha(0f);
            imageView.animate().alpha(1f).setDuration(250);
        } else if (visibility != View.VISIBLE) {
            imageView.setVisibility(View.INVISIBLE);
        }
    }

    private void mostrarDialogoTema(View v) {
        String[] temas = {"Sistema", "Claro", "Oscuro"};

        Context context = getContext();
        if (context != null) {
            // Obtener la preferencia de tema actual
            SharedPreferences sharedPreferences = context.getSharedPreferences("minka", MODE_PRIVATE);
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

        PomodoroTypeTimeViewModel pomodoroTypeTime = new ViewModelProvider(requireActivity()).get(PomodoroTypeTimeViewModel.class);
        pomodoroTypeTime.setTemaCambiado(true);

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

    private void guardarOpcionSeleccionada(int opcion) {
        Activity activity = getActivity();
        if (activity != null) {
            // Actualizar la opción seleccionada en el SharedPreferences
            SharedPreferences sharedPreferences = activity.getSharedPreferences("minka", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("opcionSeleccionada", opcion);

            // Actualiza los valores en el ViewModel
            PomodoroTypeTimeViewModel pomodoroType = new ViewModelProvider(requireActivity()).get(PomodoroTypeTimeViewModel.class);
            pomodoroType.setOpcionSeleccionada(opcion);

            editor.apply();
        }
    }

    private void guardarTiempos(int tiempoTrabajo, int tiempoDescanso) {
        Activity activity = getActivity();
        if (activity != null) {
            // Actualizar los tiempos de trabajo y descanso en el SharedPreferences
            SharedPreferences sharedPreferences = activity.getSharedPreferences("minka", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("tiempoTrabajo", tiempoTrabajo);
            editor.putInt("tiempoDescanso", tiempoDescanso);

            // Actualiza los valores en el ViewModel
            PomodoroTypeTimeViewModel pomodoroType = new ViewModelProvider(requireActivity()).get(PomodoroTypeTimeViewModel.class);
            pomodoroType.setTiempoTrabajo(tiempoTrabajo);
            pomodoroType.setTiempoDescanso(tiempoDescanso);
            pomodoroType.setConfigurationChanged(true);
            editor.apply();
        }
    }

    private void simularClick(int opcion) {
        switch (opcion) {
            case 1:
                clExtendido.performClick();  // Simula un clic en clExtendido
                break;
            case 2:
                clClasico.performClick();  // Simula un clic en clClasico
                break;
            case 3:
                clCorto.performClick();  // Simula un clic en clCorto
                break;
            case 4:
                // clPersonalizado.performClick();  // Simula un clic en clPersonalizado
                break;
        }
    }

    private void actualizarVisibilidad(int opcion) {
        switch (opcion) {
            case 1:
                activarVisibilidad(View.VISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                break;
            case 2:
                activarVisibilidad(View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                break;
            case 3:
                activarVisibilidad(View.INVISIBLE, View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
                break;
            case 4:
                activarVisibilidad(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
                break;
        }
    }

    private void verificarSonidoVibracion(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("minka", MODE_PRIVATE);
        boolean sonidoHabilitado = sharedPreferences.getBoolean("sonido", true);
        boolean vibracionHabilitada = sharedPreferences.getBoolean("vibracion", true);
        if (sSonido != null) {
            sSonido.setChecked(sonidoHabilitado);
        }
        if (sVibracion != null) {
            sVibracion.setChecked(vibracionHabilitada);
        }
    }

    private void actualizarSwitchSonido(boolean activarSonido) {
        sSonido.setChecked(activarSonido);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("minka", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("sonido", activarSonido);
        editor.apply();
    }

    private void actualizarSwitchVibracion(boolean activarVibracion) {
        sVibracion.setChecked(activarVibracion);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("minka", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("vibracion", activarVibracion);
        editor.apply();
    }
}