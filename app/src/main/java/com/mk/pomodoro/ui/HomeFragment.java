package com.mk.pomodoro.ui;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.mk.pomodoro.R;
import com.mk.pomodoro.controller.Temporizador;
import com.mk.pomodoro.ui.viewmodel.PomodoroTypeTimeViewModel;

import java.util.Locale;


public class HomeFragment extends Fragment {

    AppCompatTextView tiempo;
    TabLayout tabLayout;
    private CircularProgressIndicator barraProgresoCircular;
    private MaterialButton botonParar, botonIniciar, botonPausar,botonContinuar;
    private Temporizador temporizador;
    private boolean iniciarTemporizador = false;
    private SharedPreferences sharedPreferences;
    private int tiempoTrabajo;
    private int tiempoDescanso;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tabLayout = view.findViewById(R.id.tlOptionsTime);
        tiempo = view.findViewById(R.id.tvTime);
        barraProgresoCircular = view.findViewById(R.id.pbCircle);
        botonParar = view.findViewById(R.id.btnStop);
        botonIniciar = view.findViewById(R.id.btnPlay);
        botonPausar = view.findViewById(R.id.btnPause);
        botonContinuar = view.findViewById(R.id.btnContinue);

        Context context = getActivity();
        if (context != null) {
            sharedPreferences = context.getSharedPreferences("minka", MODE_PRIVATE);
            tiempoTrabajo = sharedPreferences.getInt("tiempoTrabajo", 25 );
            tiempoDescanso = sharedPreferences.getInt("tiempoDescanso", 5 );
        } else {
            tiempoTrabajo = 25;
            tiempoDescanso = 5;
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                iniciarTemporizador = false;
                if (position == 0) {
                    prepararTemporizador(tiempoTrabajo * 60);

                } else if (position == 1) {
                    prepararTemporizador(tiempoDescanso * 60);
                }
                botonIniciar.setVisibility(View.VISIBLE);
                botonPausar.setVisibility(View.GONE);
                botonContinuar.setVisibility(View.GONE);
                botonParar.setVisibility(View.GONE);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        prepararTemporizador(tiempoTrabajo * 60);
        botonParar.setOnClickListener(v -> {
            temporizador.reiniciarTemporizador(barraProgresoCircular, tiempo);
            botonIniciar.setVisibility(View.VISIBLE);
            botonPausar.setVisibility(View.GONE);
            botonContinuar.setVisibility(View.GONE);
            botonParar.setVisibility(View.GONE);
        });
        botonIniciar.setOnClickListener(v -> {
            iniciarTemporizador = true;
            temporizador.iniciarTemporizador();
            botonIniciar.setVisibility(View.GONE);
            botonPausar.setVisibility(View.VISIBLE);
            botonParar.setVisibility(View.VISIBLE);
        });
        botonPausar.setOnClickListener(v -> {
            temporizador.pausarTemporizador();
            botonPausar.setVisibility(View.GONE);
            botonContinuar.setVisibility(View.VISIBLE);
        });
        botonContinuar.setOnClickListener(v -> {
            temporizador.reanudarTemporizador();
            botonContinuar.setVisibility(View.GONE);
            botonPausar.setVisibility(View.VISIBLE);
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Observa los cambios en ViewModel
        PomodoroTypeTimeViewModel pomodoroTypeTime = new ViewModelProvider(requireActivity()).get(PomodoroTypeTimeViewModel.class);

        pomodoroTypeTime.getTiempoTrabajo().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer nuevoTiempoTrabajo) {
                // Actualiza tu variable tiempoTrabajo con el nuevo tiempo de trabajo
                tiempoTrabajo = nuevoTiempoTrabajo;
                // Comprueba si la opción de trabajo está seleccionada en tu TabLayout
                if (tabLayout.getSelectedTabPosition() == 0) {
                    // Si la opción de trabajo está seleccionada, actualiza el temporizador
                    prepararTemporizador(tiempoTrabajo * 60);
                }
            }
        });

        pomodoroTypeTime.getTiempoDescanso().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer nuevoTiempoDescanso) {
                // Actualiza tu variable tiempoDescanso con el nuevo tiempo de descanso
                tiempoDescanso = nuevoTiempoDescanso;
                // Comprueba si la opción de descanso está seleccionada en tu TabLayout
                if (tabLayout.getSelectedTabPosition() == 1) {
                    // Si la opción de descanso está seleccionada, actualiza el temporizador
                    prepararTemporizador(tiempoDescanso * 60);
                }
            }
        });
    }

    private void prepararTemporizador(int segundos) {
        if (temporizador != null) {
            temporizador.destruirTemporizador();
        }
        barraProgresoCircular.setMax(segundos);
        temporizador = new Temporizador(segundos * 1000L, 1000);
        temporizador.setEscuchadorTick(millisUntilFinished -> {
            int segundosRestantes = (int) (millisUntilFinished / 1000f);
            tiempo.setText(String.format(Locale.getDefault(), "%02d:%02d", segundosRestantes / 60, segundosRestantes % 60));
            barraProgresoCircular.setProgress(segundosRestantes);
            System.out.println(segundosRestantes);
        });
        temporizador.setEscuchadorFinalizacion(() -> tiempo.setText("00:00"));
        // Mostramos el tiempo inicial sin iniciar el temporizador
        int segundosRestantes = segundos;
        tiempo.setText(String.format(Locale.getDefault(), "%02d:%02d", segundosRestantes / 60, segundosRestantes % 60));
        barraProgresoCircular.setProgress(segundosRestantes);
        // Iniciamos el temporizador solo si iniciarTemporizador es verdadero
        if (iniciarTemporizador) {
            temporizador.iniciarTemporizador();
        }
    }
}