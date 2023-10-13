package com.mk.pomodoro.controller;

import com.mk.pomodoro.models.Historial;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface HistorialApi {
    @GET("historial")
    Call<List<Historial>> getHistorial();
}
