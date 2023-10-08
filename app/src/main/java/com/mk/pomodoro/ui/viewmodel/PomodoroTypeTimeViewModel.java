package com.mk.pomodoro.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PomodoroTypeTimeViewModel extends ViewModel {
    private final MutableLiveData<Integer> tiempoTrabajo = new MutableLiveData<>();
    private final MutableLiveData<Integer> tiempoDescanso = new MutableLiveData<>();
    private final MutableLiveData<Integer> opcionSeleccionada = new MutableLiveData<>();
    private final MutableLiveData<Boolean> configurationChanged = new MutableLiveData<>(false);

    public void setTiempoTrabajo(int tiempo) {
        tiempoTrabajo.setValue(tiempo);
    }

    public LiveData<Integer> getTiempoTrabajo() {
        return tiempoTrabajo;
    }

    public void setTiempoDescanso(int tiempo) {
        tiempoDescanso.setValue(tiempo);
    }

    public LiveData<Integer> getTiempoDescanso() {
        return tiempoDescanso;
    }

    public void setOpcionSeleccionada(int opcion) {
        opcionSeleccionada.setValue(opcion);
    }

    public LiveData<Integer> getOpcionSeleccionada() {
        return opcionSeleccionada;
    }

    public LiveData<Boolean> isConfigurationChanged() {
        return configurationChanged;
    }

    public void setConfigurationChanged(boolean configurationChanged) {
        this.configurationChanged.setValue(configurationChanged);
    }
}
