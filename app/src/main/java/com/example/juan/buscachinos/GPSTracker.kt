package com.example.juan.buscachinos

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

/**
 * Created by Juan on 21/06/2017.
 */
class GPSTracker(
    private val context: Context,
    private val locationManager: LocationManager?
) : LocationListener {

    var isGPSEnabled: Boolean = false
    var isNetworkEnabled: Boolean = false
    var canGetLocation: Boolean = false

    private var currentLocation: Location? = null
    private var lat: Double = 0.0
    private var lon: Double = 0.0

    init {
        getLocation()
    }

    fun getLocation(): Location? {
        try {
            val hasPermission = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            val lm = locationManager
            if (hasPermission && lm != null) {
                isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                if (isGPSEnabled || isNetworkEnabled) {
                    canGetLocation = true
                    if (isNetworkEnabled) {
                        lm.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            this
                        )
                        Log.d("Network", "Network")
                        currentLocation =
                            lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        currentLocation?.let {
                            lat = it.latitude
                            lon = it.longitude
                        }
                    }
                    if (isGPSEnabled && currentLocation == null) {
                        lm.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            this
                        )
                        Log.d("GPS Enabled", "GPS Enabled")
                        currentLocation =
                            lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        currentLocation?.let {
                            lat = it.latitude
                            lon = it.longitude
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return currentLocation
    }

    fun stopUsingGPS() {
        locationManager?.removeUpdates(this)
    }

    fun getLatitude(): Double {
        currentLocation?.let { lat = it.latitude }
        return lat
    }

    fun getLongitude(): Double {
        currentLocation?.let { lon = it.longitude }
        return lon
    }

    fun canGetLocation(): Boolean = canGetLocation

    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("GPS is settings")
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?")
        alertDialog.setPositiveButton("Settings") { _, _ ->
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
        alertDialog.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        alertDialog.show()
    }

    override fun onLocationChanged(location: Location) {
    }

    override fun onProviderDisabled(provider: String) {
    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    companion object {
        // The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES = 10f // 10 meters

        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong() // 1 minute
    }
}
