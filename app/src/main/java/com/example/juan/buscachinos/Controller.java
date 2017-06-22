package com.example.juan.buscachinos;

import android.database.Cursor;
import android.util.SparseArray;

import java.util.ArrayList;

/**
 * Created by Juan on 22/06/2017.
 */

public class Controller {

    public int getCantidadCategorias() {
        int cont = 0;
        if (Constants.database != null) {
            Cursor c = Constants.database.rawQuery("SELECT * FROM chino", null);

            cont = c.getCount();
        }
        return cont;
    }

    public ArrayList<Chino> getChinos(){
        ArrayList<Chino> chinos = new ArrayList<Chino>();
        Chino chino = null;
        if (Constants.database != null) {
            Cursor c = Constants.database.rawQuery("SELECT * FROM chino ", null);
            int i = 0;
            if (c.moveToFirst()) {
                do {
                    chino = new Chino();

                    final int codChino = Integer.parseInt(c.getString(0));
                    final String chino_name = c.getString(1);
                    final double longitud = Double.parseDouble(c.getString(2));
                    final double latitude = Double.parseDouble(c.getString(3));

                    chino.setCodChino(codChino);
                    chino.setChino_name(chino_name);
                    chino.setLongitud(longitud);
                    chino.setLatitude(latitude);
                    chinos.add(chino);
                    i++;
                } while (c.moveToNext());
            }
        }
        return chinos;
    }

    public Chino getChinobyCoords(double longitud, double latitud){
        Chino chino = null;
        if (Constants.database != null) {
            final Cursor c = Constants.database.rawQuery("SELECT * FROM chino WHERE longitud = "+longitud+" AND latitud = " + latitud, null);
            //txtResultado.setText("");


            int i = 0;
            if (c.moveToFirst()) {
                do {
                    chino = new Chino();
                    chino.setCodChino(Integer.parseInt(c.getString(0)));
                    chino.setChino_name(c.getString(1));
                    chino.setLongitud(Double.parseDouble(c.getString(2)));
                    chino.setLatitude(Double.parseDouble(c.getString(3)));

                    i++;
                } while (c.moveToNext());
            }
        }
        return chino;
    }

    public Chino getChinoCod(int cod){
        Chino chino = new Chino();
        if (Constants.database != null) {
            final Cursor c = Constants.database.rawQuery("SELECT * FROM chino WHERE codChino = "+cod, null);
            //txtResultado.setText("");


            int i = 0;
            if (c.moveToFirst()) {
                do {
                    chino.setCodChino(Integer.parseInt(c.getString(0)));
                    chino.setChino_name(c.getString(1));
                    chino.setLongitud(Double.parseDouble(c.getString(2)));
                    chino.setLatitude(Double.parseDouble(c.getString(3)));

                    i++;
                } while (c.moveToNext());
            }
        }
        return chino;
    }

}
