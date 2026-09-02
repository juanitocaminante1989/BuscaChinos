package com.example.juan.buscachinos

import android.Manifest
import android.app.SearchManager
import android.content.ContentValues
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity() {
    private var mMapView: MapView? = null
    private var googleMap: GoogleMap? = null
    private var tageName: EditText? = null
    private var tagButton: Button? = null
    private var deleteButton: Button? = null
    private var gpsTracker: GPSTracker? = null
    private var locationManager: LocationManager? = null
    private var buscaChinosSqlHelper: BuscaChinosSqlHelper? = null
    private var controller: Controller? = null
    private var myMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mMapView = findViewById(R.id.mapViewProducts)
        mMapView!!.onCreate(savedInstanceState)
        tagButton = findViewById(R.id.tag_button)
        tageName = findViewById(R.id.tag_text)
        deleteButton = findViewById(R.id.delete_marker)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        gpsTracker = GPSTracker(this, locationManager)
        buscaChinosSqlHelper = BuscaChinosSqlHelper(this, "chinoBBDD", null, 1)
        Constants.database = buscaChinosSqlHelper!!.writableDatabase
        controller = Controller()
        mMapView!!.onResume()

        try {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                showExplanation(
                    "Permission Needed",
                    "Rationale",
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    1
                )
            } else {
                requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, 1)
            }
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mMapView!!.getMapAsync { mMap ->
                    googleMap = mMap

                    // For showing a move to my location button
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        mMap.isMyLocationEnabled = true
                    }

                    // For dropping a marker at a point on the Map
                    val sydney = LatLng(gpsTracker!!.getLatitude(), gpsTracker!!.getLongitude())
                    for (chino in controller!!.getChinos()) {
                        val coords = LatLng(chino.latitude, chino.longitud)
                        mMap.addMarker(addMarketOptions(coords, chino.chino_name, ""))
                    }

                    // For zooming automatically to the location of the marker
                    val cameraPosition = CameraPosition.Builder().target(sydney).zoom(12f).build()
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                    mMap.setOnMarkerClickListener { marker ->
                        myMarker = marker
                        false
                    }
                }
            }
        } catch (e: Exception) {
            DebugUtilities.writeLog("", e)
        }

        tagButton!!.setOnClickListener { setTag() }
        deleteButton!!.setOnClickListener { deleteMarker() }
    }

    fun addMarketOptions(place: LatLng, title: String?, snippet: String?): MarkerOptions {
        return MarkerOptions().position(place).title(title).snippet(snippet)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                val granted = grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                val msg = if (granted) "Permission Granted!" else "Permission Denied!"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showExplanation(
        title: String?,
        message: String?,
        permission: String,
        permissionRequestCode: Int
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                requestPermission(permission, permissionRequestCode)
            }
        builder.create().show()
    }

    private fun requestPermission(permissionName: String, permissionRequestCode: Int) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permissionName),
            permissionRequestCode
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as? SearchView
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    fun setTag() {
        val map = googleMap ?: return
        val db = Constants.database ?: return
        val sydney = LatLng(gpsTracker!!.getLatitude(), gpsTracker!!.getLongitude())
        map.addMarker(addMarketOptions(sydney, tageName!!.text.toString(), ""))
        val cant = controller!!.getCantidadCategorias() + 1
        val initialValues = ContentValues().apply {
            put("codChino", cant)
            put("chino_name", tageName!!.text.toString())
            put("longitud", sydney.longitude)
            put("latitud", sydney.latitude)
        }
        db.insert("chino", "codChino=?", initialValues)
        tageName!!.setText("")
    }

    fun deleteMarker() {
        val marker = myMarker ?: return
        val db = Constants.database ?: return
        val chino = controller!!.getChinobyCoords(
            marker.position.longitude,
            marker.position.latitude
        )
        try {
            if (chino != null) {
                db.delete("chino", "codChino=?", arrayOf(chino.codChino.toString()))
                marker.isVisible = false
            }
        } catch (e: Exception) {
            DebugUtilities.writeLog("", e)
        }
    }
}
