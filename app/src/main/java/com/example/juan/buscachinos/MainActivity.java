package com.example.juan.buscachinos;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView;
    private GoogleMap googleMap;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this.getApplicationContext();
        mMapView = (MapView) findViewById(R.id.mapViewProducts);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();
        try {
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;

                    // For showing a move to my location button
                    googleMap.setMyLocationEnabled(true);

                    // For dropping a marker at a point on the Map
                    GPSTracker gpsTracker = new GPSTracker(context);
                    LatLng sydney = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());


//                        LatLng coords = new LatLng(associatedShopsSparseArray.get(i).getLatitude(), associatedShopsSparseArray.get(i).getLongitude());
//                        googleMap.addMarker(addMarketOptions(coords, associatedShopsSparseArray.get(i).getName(), associatedShopsSparseArray.get(i).getStreet()));

                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            });
        }catch (Exception e){
            DebugUtilities.writeLog("",e);
        }
    }
}
