package com.example.juan.buscachinos.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.example.juan.buscachinos.domain.model.GeoPoint
import com.example.juan.buscachinos.domain.repository.LocationRepository

/**
 * Implementacion basada en [LocationManager]. Si no hay permiso concedido o ningun
 * proveedor tiene una ultima ubicacion conocida, devuelve null (antes: 0.0/0.0 por
 * defecto en [com.example.juan.buscachinos.GPSTracker]).
 */
class AndroidLocationTracker(
    private val context: Context,
    private val locationManager: LocationManager?
) : LocationRepository {

    override fun getLastKnownLocation(): GeoPoint? {
        val lm = locationManager ?: return null
        val hasPermission = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) return null

        val providers = listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)
        for (provider in providers) {
            if (!lm.isProviderEnabled(provider)) continue
            val location = try {
                lm.getLastKnownLocation(provider)
            } catch (e: SecurityException) {
                null
            }
            if (location != null) {
                return GeoPoint(latitude = location.latitude, longitude = location.longitude)
            }
        }
        return null
    }
}
