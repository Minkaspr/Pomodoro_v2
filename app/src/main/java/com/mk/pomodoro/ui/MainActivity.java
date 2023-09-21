package com.mk.pomodoro.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.mk.pomodoro.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;
public class MainActivity extends AppCompatActivity {

    private static final String ELEMENTO_SELECCIONADO = "id_elemento_seleccionado";
    private int idElementoSeleccionado;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aplicarConfiguraciones();
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Recuperar el estado guardado del fragmento seleccionado
        seleccionarFragmentoGuardado(savedInstanceState);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            seleccionarFragmento(item);
            return true;
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Guardar el estado del fragmento seleccionado
        outState.putInt(ELEMENTO_SELECCIONADO, idElementoSeleccionado);
        super.onSaveInstanceState(outState);
    }

    private void seleccionarFragmento(MenuItem item) {
        // Actualizar el elemento seleccionado
        idElementoSeleccionado = item.getItemId();

        Fragment fragment;
        if (idElementoSeleccionado == R.id.navigation_home) {
            fragment = new HomeFragment();
        } else if (idElementoSeleccionado == R.id.navigation_settings) {
            fragment = new SettingsFragment();
        } else {
            fragment = new HomeFragment();
        }

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.container_frame_layout, fragment).commit();
    }

    private void seleccionarFragmentoGuardado(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            idElementoSeleccionado = savedInstanceState.getInt(ELEMENTO_SELECCIONADO, 0);
            MenuItem selectedMenuItem = bottomNavigationView.getMenu().findItem(idElementoSeleccionado);
            seleccionarFragmento(selectedMenuItem);
        } else {
            // Si no hay un estado guardado, selecciona el primer fragmento
            seleccionarFragmento(bottomNavigationView.getMenu().getItem(0));
        }
    }

    private void aplicarConfiguraciones() {
        SharedPreferences sharedPreferences = getSharedPreferences("minka", MODE_PRIVATE);

        int tema = sharedPreferences.getInt("tema", 0);

        switch (tema) {
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