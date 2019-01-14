package com.miniproject16cntn.a1612543.snackfood;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.miniproject16cntn.a1612543.snackfood.DirectionFinder.DirectionFinder;
import com.miniproject16cntn.a1612543.snackfood.DirectionFinder.DirectionFinderListener;
import com.miniproject16cntn.a1612543.snackfood.DirectionFinder.Route;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000;
    private static final float MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final int REQUEST_CODE_LOCATION = 1;
    private GoogleMap mMap;
    private LatLng PositionRestaurant;
    private LatLng PositionPeople;
    private String infoWindow;
    private LocationManager locationManager;
    private Button btnShowRoute;
    private ProgressDialog progressDialog;
    public List<Polyline> polylinePaths;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnShowRoute = findViewById(R.id.btn_show_route);
        btnShowRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowRoute();
            }
        });

        polylinePaths = new ArrayList<>();

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        Intent intent = getIntent();
        String code = intent.getStringExtra("CODE");

        if (code.compareTo(DetailRestaurant.CODE) == 0)
        {
            PositionRestaurant = intent.getExtras().getParcelable(DetailRestaurant.LATLNG);
            infoWindow = intent.getStringExtra(DetailRestaurant.TAG);
        }

        else if (code.compareTo(AddRestaurant.CODE) == 0)
        {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_LOCATION);
            }
            else {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MINIMUM_TIME_BETWEEN_UPDATES,
                        MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                        new MyLocationListener()
                );

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if(location != null)
                {
                    PositionRestaurant = new LatLng(location.getLatitude(), location.getLongitude());
                }
            }
        }
    }

    private void ShowRoute() {
        GetCurrentLocation();
        if (PositionRestaurant != null){
            new DirectionFinder(this, PositionPeople, PositionRestaurant).execute();

        }
    }

    private void GetCurrentLocation() {
        PositionPeople = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MINIMUM_TIME_BETWEEN_UPDATES,
                    MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                    new MyLocationListener()
            );

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(location != null)
            {
                PositionPeople = new LatLng(location.getLatitude(), location.getLongitude());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_LOCATION)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                GetCurrentLocation();
        }
    }

    private void showMarker() {
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(PositionRestaurant)
                .title(infoWindow));

        marker.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PositionRestaurant, 18));
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION},1);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        showMarker();
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);
        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        for (Route route : routes){
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(route.bounds,200));
            Marker markerOrigin = mMap.addMarker(new MarkerOptions()
                    .position(route.startLocation));
            Marker markerDestination = mMap.addMarker(new MarkerOptions()
                    .position(route.endLocation));
            markerDestination.showInfoWindow();
            markerOrigin.showInfoWindow();
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);
            for(int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }


    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            PositionRestaurant = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PositionRestaurant, 18));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}

//Default: 10.8758275,106.7985386
