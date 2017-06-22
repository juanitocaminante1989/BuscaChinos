package com.example.juan.buscachinos;

/**
 * Created by Juan on 22/06/2017.
 */

public class Chino {

    private int codChino;
    private String chino_name;
    private double latitude;
    private double longitud;

    public Chino() {
    }

    public Chino(int codChino, String chino_name, double latitude, double longitud) {
        this.codChino = codChino;
        this.chino_name = chino_name;
        this.latitude = latitude;
        this.longitud = longitud;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public int getCodChino() {
        return codChino;
    }

    public void setCodChino(int codChino) {
        this.codChino = codChino;
    }

    public String getChino_name() {
        return chino_name;
    }

    public void setChino_name(String chino_name) {
        this.chino_name = chino_name;
    }
}
