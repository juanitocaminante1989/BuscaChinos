package com.example.juan.buscachinos;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class MainActivity extends ActionBarActivity {

    private MapView mMapView;
    private GoogleMap googleMap;
    private Context context;
    private EditText tageName;
    private Button tagButton;
    private GPSTracker gpsTracker;
    private LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this.getApplicationContext();
        mMapView = (MapView) findViewById(R.id.mapViewProducts);
        mMapView.onCreate(savedInstanceState);
        tagButton= (Button) findViewById(R.id.tag_button);
        tageName = (EditText) findViewById(R.id.tag_text);
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        gpsTracker = new GPSTracker(context, locationManager);
        mMapView.onResume();

        try {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_FINE_LOCATION, 1);
                } else {
                    requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, 1);
                }
                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap mMap) {
                        googleMap = mMap;

                        // For showing a move to my location button
                        googleMap.setMyLocationEnabled(true);

                        // For dropping a marker at a point on the Map


                        LatLng sydney = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());


//                        LatLng coords = new LatLng(associatedShopsSparseArray.get(i).getLatitude(), associatedShopsSparseArray.get(i).getLongitude());
//                        googleMap.addMarker(addMarketOptions(sydney, "Ubicacion actual", ""));

                        // For zooming automatically to the location of the marker
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    }
                });
            }
        }catch (Exception e){
            DebugUtilities.writeLog("",e);
        }

        tagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTag();
            }
        });
    }

    public MarkerOptions addMarketOptions(LatLng place, String title, String snippet) {

        MarkerOptions marker = new MarkerOptions();
        marker.position(place).title(title).snippet(snippet);

        return marker;

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if(searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void setTag(){

        LatLng sydney = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        googleMap.addMarker(addMarketOptions(sydney, tageName.getText().toString(), ""));

    }
}
