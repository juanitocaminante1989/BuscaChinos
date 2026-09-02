package com.example.juan.buscachinos

/**
 * Created by Juan on 22/06/2017.
 */
class Controller {

    fun getCantidadCategorias(): Int {
        val db = Constants.database ?: return 0
        db.rawQuery("SELECT * FROM chino", null).use { c ->
            return c.count
        }
    }

    fun getChinos(): ArrayList<Chino> {
        val chinos = ArrayList<Chino>()
        val db = Constants.database ?: return chinos
        db.rawQuery("SELECT * FROM chino", null).use { c ->
            if (c.moveToFirst()) {
                do {
                    chinos.add(
                        Chino(
                            codChino = c.getString(0).toInt(),
                            chino_name = c.getString(1),
                            longitud = c.getString(2).toDouble(),
                            latitude = c.getString(3).toDouble()
                        )
                    )
                } while (c.moveToNext())
            }
        }
        return chinos
    }

    fun getChinobyCoords(longitud: Double, latitud: Double): Chino? {
        val db = Constants.database ?: return null
        var chino: Chino? = null
        db.rawQuery(
            "SELECT * FROM chino WHERE longitud = $longitud AND latitud = $latitud",
            null
        ).use { c ->
            if (c.moveToFirst()) {
                do {
                    chino = Chino(
                        codChino = c.getString(0).toInt(),
                        chino_name = c.getString(1),
                        longitud = c.getString(2).toDouble(),
                        latitude = c.getString(3).toDouble()
                    )
                } while (c.moveToNext())
            }
        }
        return chino
    }

    fun getChinoCod(cod: Int): Chino {
        val chino = Chino()
        val db = Constants.database ?: return chino
        db.rawQuery("SELECT * FROM chino WHERE codChino = $cod", null).use { c ->
            if (c.moveToFirst()) {
                do {
                    chino.codChino = c.getString(0).toInt()
                    chino.chino_name = c.getString(1)
                    chino.longitud = c.getString(2).toDouble()
                    chino.latitude = c.getString(3).toDouble()
                } while (c.moveToNext())
            }
        }
        return chino
    }
}
