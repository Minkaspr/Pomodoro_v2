package com.mk.pomodoro.models;

public class Historial {
    private int id;
    private String tipo;
    private String opcion_tiempo;
    private int tiempo_designado;
    private int tiempo_transcurrido;
    private String hora_inicio;
    private String hora_fin;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getOpcion_tiempo() {
        return opcion_tiempo;
    }

    public void setOpcion_tiempo(String opcion_tiempo) {
        this.opcion_tiempo = opcion_tiempo;
    }

    public int getTiempo_designado() {
        return tiempo_designado;
    }

    public void setTiempo_designado(int tiempo_designado) {
        this.tiempo_designado = tiempo_designado;
    }

    public int getTiempo_transcurrido() {
        return tiempo_transcurrido;
    }

    public void setTiempo_transcurrido(int tiempo_transcurrido) {
        this.tiempo_transcurrido = tiempo_transcurrido;
    }

    public String getHora_inicio() {
        return hora_inicio;
    }

    public void setHora_inicio(String hora_inicio) {
        this.hora_inicio = hora_inicio;
    }

    public String getHora_fin() {
        return hora_fin;
    }

    public void setHora_fin(String hora_fin) {
        this.hora_fin = hora_fin;
    }
}
