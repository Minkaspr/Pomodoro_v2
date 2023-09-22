package com.mk.pomodoro.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.mk.pomodoro.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mk.pomodoro.ui.adapter.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String ELEMENTO_SELECCIONADO = "id_elemento_seleccionado";
    private int idElementoSeleccionado;
    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aplicarConfiguraciones();
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.view_pager);

        // Configurar el ViewPager
        viewPager.setAdapter(new ViewPagerAdapter(this));
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }
        });

        // Recuperar el estado guardado del fragmento seleccionado
        seleccionarFragmentoGuardado(savedInstanceState);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (item.getItemId() == R.id.navigation_settings) {
                viewPager.setCurrentItem(1);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Guardar el estado del fragmento seleccionado
        outState.putInt(ELEMENTO_SELECCIONADO, idElementoSeleccionado);
        super.onSaveInstanceState(outState);
    }

    private void seleccionarFragmentoGuardado(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            idElementoSeleccionado = savedInstanceState.getInt(ELEMENTO_SELECCIONADO, 0);
            MenuItem elementoSeleccionadoMenu = bottomNavigationView.getMenu().findItem(idElementoSeleccionado);
            if (elementoSeleccionadoMenu != null) {
                viewPager.setCurrentItem(elementoSeleccionadoMenu.getOrder());
            }
        } else {
            // Si no hay un estado guardado, selecciona el primer fragmento
            viewPager.setCurrentItem(0);
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
