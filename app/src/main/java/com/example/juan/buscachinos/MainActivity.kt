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
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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
    private var deleteButton: Button? = null
    private var buttonBar: View? = null
    private var systemBarInsets: Insets? = null
    private var gpsTracker: GPSTracker? = null
    private var locationManager: LocationManager? = null
    private var buscaChinosSqlHelper: BuscaChinosSqlHelper? = null
    private var controller: Controller? = null
    private var myMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        mMapView = findViewById(R.id.mapViewProducts)
        mMapView!!.onCreate(savedInstanceState)
        deleteButton = findViewById(R.id.delete_marker)
        buttonBar = findViewById(R.id.linearLayout)
        setupEdgeToEdge()
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        gpsTracker = GPSTracker(this, locationManager)
        buscaChinosSqlHelper = BuscaChinosSqlHelper(this, "chinoBBDD", null, 1)
        Constants.database = buscaChinosSqlHelper!!.writableDatabase
        controller = Controller()

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
            setupMap()
        } catch (e: Exception) {
            DebugUtilities.writeLog("", e)
        }

        deleteButton!!.setOnClickListener { deleteMarker() }
    }

    /**
     * Con edge-to-edge el contenido se dibuja debajo de las barras del sistema.
     * Aquí se le da padding al panel de botones (para no quedar tapado por la
     * barra de gestos/navegación) y se avisa al mapa de esos insets para que
     * reposicione sus propios controles (ubicación, brújula, logo).
     */
    private fun setupEdgeToEdge() {
        val bar = buttonBar ?: return
        val basePaddingLeft = bar.paddingLeft
        val basePaddingRight = bar.paddingRight
        val basePaddingBottom = bar.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(bar) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            systemBarInsets = systemBars
            view.updatePadding(
                left = basePaddingLeft + systemBars.left,
                right = basePaddingRight + systemBars.right,
                bottom = basePaddingBottom + systemBars.bottom
            )
            applyMapPadding()
            insets
        }
    }

    /** Deja los controles nativos del mapa por encima del panel de botones y las barras del sistema. */
    private fun applyMapPadding() {
        val bar = buttonBar ?: return
        // El panel de botones acaba de recibir un nuevo padding: se espera al
        // siguiente layout para leer su altura definitiva.
        bar.post {
            val map = googleMap ?: return@post
            val insets = systemBarInsets ?: return@post
            map.setPadding(insets.left, insets.top, insets.right, bar.height)
        }
    }

    private fun setupMap() {
        val mapView = mMapView ?: return
        mapView.getMapAsync { mMap ->
            googleMap = mMap
            applyMapPadding()

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
            mMap.clear()
            for (chino in controller!!.getChinos()) {
                val coords = LatLng(chino.latitude, chino.longitud)
                mMap.addMarker(addMarketOptions(coords, chino.chino_name, ""))
            }

            // For zooming automatically to the location of the marker
            val cameraPosition = CameraPosition.Builder().target(sydney).zoom(12f).build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            mMap.setOnMarkerClickListener { marker ->
                myMarker = marker
                deleteButton!!.visibility = View.VISIBLE
                false
            }

            mMap.setOnMapClickListener{ map ->
                deleteButton!!.visibility = View.GONE
            }
            // Long press: pide el nombre del chino y lo taguea en ese punto exacto
            mMap.setOnMapLongClickListener { latLng ->
                showTagDialog(latLng)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mMapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mMapView?.onResume()
    }

    override fun onPause() {
        mMapView?.onPause()
        super.onPause()
    }

    override fun onStop() {
        mMapView?.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mMapView?.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMapView?.onSaveInstanceState(outState)
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
                if (granted) {
                    setupMap()
                }
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

    /** Muestra un EditText con botón "Taguear" para nombrar el chino en [target]. */
    private fun showTagDialog(target: LatLng) {
        val input = EditText(this).apply {
            hint = "Nombre del chino"
        }
        val padding = (16 * resources.displayMetrics.density).toInt()
        val container = android.widget.FrameLayout(this).apply {
            setPadding(padding, padding, padding, padding)
            addView(input)
        }
        AlertDialog.Builder(this)
            .setTitle("Taguear chino")
            .setView(container)
            .setPositiveButton("Taguear") { _, _ ->
                tagChino(target, input.text.toString())
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /** Taguea un chino en [target] con nombre [name]: añade el marcador y lo inserta en la BBDD. */
    private fun tagChino(target: LatLng, name: String) {
        val map = googleMap ?: return
        val db = Constants.database ?: return
        map.addMarker(addMarketOptions(target, name, ""))
        val cant = controller!!.getCantidadCategorias() + 1
        val initialValues = ContentValues().apply {
            put("codChino", cant)
            put("chino_name", name)
            put("longitud", target.longitude)
            put("latitud", target.latitude)
        }
        db.insert("chino", "codChino=?", initialValues)
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
