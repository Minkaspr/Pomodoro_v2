package com.mk.pomodoro.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.mk.pomodoro.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mk.pomodoro.ui.adapter.ViewPagerAdapter;
import com.mk.pomodoro.ui.viewmodel.PomodoroTypeTimeViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String ELEMENTO_SELECCIONADO = "id_elemento_seleccionado";
    private int idElementoSeleccionado;
    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager;
    private final static String CHANNEL_ID = "TemporizadorServiceChannel";
    private PomodoroTypeTimeViewModel pomodoroType;

    private BroadcastReceiver temporizadorTerminadoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.mk.pomodoro.TEMPORIZADOR_TERMINADO")) {
                showFinishedNotification(CHANNEL_ID);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aplicarConfiguraciones();
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.view_pager);
        // Registrar el BroadcastReceiver
        LocalBroadcastManager.getInstance(this).registerReceiver(temporizadorTerminadoReceiver, new IntentFilter("com.mk.pomodoro.TEMPORIZADOR_TERMINADO"));
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

        pomodoroType = new ViewModelProvider(this).get(PomodoroTypeTimeViewModel.class);
        pomodoroType.getEstadoTemporizador().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String estadoTemporizador) {
                if (pomodoroType.getTemporizadorTerminado().getValue()) {
                    showNotification();
                }
            }
        });
        pomodoroType.getTemporizadorTerminado().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean temporizadorTerminado) {
                if (temporizadorTerminado) {
                    showNotification();
                }
            }
        });
    }

    /*@Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            showNotification();
        } else {
            showNewNotificacion(CHANNEL_ID);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this.getApplicationContext());
        managerCompat.cancel(1);
    }*/

    @Override
    public void onPause() {
        super.onPause();
        showNotification();
    }

    @Override
    public void onResume() {
        super.onResume();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this.getApplicationContext());
        managerCompat.cancel(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Anular el registro del BroadcastReceiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(temporizadorTerminadoReceiver);
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

    /*
    private void showNotification() {
        String CHANNEL_ID = "TemporizadorServiceChannel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Temporizador en ejecución", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
        showNewNotificacion(CHANNEL_ID);
    }


    private void showNewNotificacion(String CHANNEL_ID) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Temporizador en ejecución")
                .setContentText("El temporizador está funcionando en segundo plano.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this.getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        managerCompat.notify(1, builder.build());
    }
    */
    private void showNotification() {
        String estadoTemporizador = pomodoroType.getEstadoTemporizador().getValue();
        Boolean temporizadorTerminado = pomodoroType.getTemporizadorTerminado().getValue();
        Boolean temporizadorIniciado = pomodoroType.getTemporizadorIniciado().getValue();

        String title;
        String text;
        if (temporizadorTerminado) {
            title = "Tiempo de " + estadoTemporizador + " completado";
            text = "El tiempo de " + estadoTemporizador + " ha terminado.";
            // Reset the timer state
            pomodoroType.setTemporizadorTerminado(false);
            showNewNotificacion(CHANNEL_ID, title, text);
        }
        if (temporizadorIniciado) {
            title = "Tiempo de " + estadoTemporizador + " iniciado";
            text = "El temporizador está funcionando en segundo plano.";
            showNewNotificacion(CHANNEL_ID, title, text);
        }
    }

    private void showNewNotificacion(String CHANNEL_ID, String title, String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this.getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        managerCompat.notify(1, builder.build());
    }

    private void showFinishedNotification(String CHANNEL_ID) {
        String estadoTemporizador = pomodoroType.getEstadoTemporizador().getValue();

        String title = "Tiempo de " + estadoTemporizador + " completado";
        String text = "El tiempo de " + estadoTemporizador + " ha terminado.";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Reemplaza esto con el ícono que quieras usar para la notificación
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Mostrar la notificación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }
}
