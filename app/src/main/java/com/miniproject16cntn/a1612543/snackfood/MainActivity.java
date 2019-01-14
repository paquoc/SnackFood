package com.miniproject16cntn.a1612543.snackfood;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final float DEFAULT_DISTANCE = 800;
    private List<Restaurant> arrRestaurant;
    private RestaurantAdapter restaurantAdapter;
    private ListView listView;
    public static DBRestaurant dbRestaurant;
    private EditText searchBox;
    public static final String ID_RESTAURANT = "ID_RESTAURANT";
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000;
    private static final float MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private TextView txtNearBy;
    private TextView txtFavorite;
    private TextView txtShowAll;
    private Toolbar toolbar;

    private Button btnAddNewItem;

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchBox =  findViewById(R.id.txt_search);


        dbRestaurant = new DBRestaurant(this);
        try {
            dbRestaurant.create();
        } catch (IOException e) {
            e.printStackTrace();
        }

        listView = findViewById(R.id.list_restaurant);

        arrRestaurant = new ArrayList<Restaurant>();

        loadAllRestaurant();
        refreshListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailRestaurant.class);
                int idx = arrRestaurant.get(position).getId();
                intent.putExtra(ID_RESTAURANT, idx);
                startActivity(intent);
            }
        });

        btnAddNewItem = findViewById(R.id.add_new_restaurant);
        btnAddNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddRestaurant.class);
                startActivity(intent);
            }
        });

        txtFavorite = findViewById(R.id.txtFavorite);
        txtFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrRestaurant.clear();
                arrRestaurant.addAll(dbRestaurant.getFavorite());
                refreshListView();
            }
        });

        txtNearBy = findViewById(R.id.txtNearby);
        txtNearBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrRestaurant.clear();
                arrRestaurant.addAll(dbRestaurant.getRestaurantNearby(getCurrentLocation(), DEFAULT_DISTANCE));
                refreshListView();
            }
        });

        txtShowAll = findViewById(R.id.txtShowAll);
        txtShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAllRestaurant();
                refreshListView();
            }
        });

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        if (menuItem.getItemId() == R.id.nav_home)
                            openHomePage();
                        else if (menuItem.getItemId() == R.id.nav_feedback) {
                            Log.d("Debug","Clicked suscessfully");
                            openFeedbackActivity();
                        }
                        else if (menuItem.getItemId() == R.id.nav_information)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Thông tin ứng dụng");
                            builder.setMessage("Ứng dụng SnackFood phiên bản 1.0 ?");
                            builder.setCancelable(true);
                            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }

                        return true;
                    }
                });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        Intent intent = getIntent();
        if (intent != null)
        {
            loadAllRestaurant();
            refreshListView();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openHomePage() {

    }

    private void openFeedbackActivity(){
        Intent intent = new Intent(MainActivity.this, Feedback.class);
        startActivity(intent);
    }

    private void loadAllRestaurant() {
        if (arrRestaurant != null)
            arrRestaurant.clear();
        arrRestaurant.addAll(dbRestaurant.getAllRestaurant());
    }

    private void refreshListView() {
        restaurantAdapter = new RestaurantAdapter(this, arrRestaurant);
        listView.setAdapter(restaurantAdapter);
    }


    public void onSearchButton(View v)
    {
        if (v.getId() == R.id.btn_search) {
            String key = searchBox.getText().toString().trim();
            if (key.isEmpty()){
                arrRestaurant.clear();
                arrRestaurant.addAll(dbRestaurant.getAllRestaurant());
            }

            else {
                arrRestaurant.clear();
                arrRestaurant.addAll(dbRestaurant.getRestaurant(key));
            }
            refreshListView();
        }
    }

    public LatLng getCurrentLocation()
    {
        LatLng latLng = new LatLng(10.8758275, 106.7985386);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else {

            LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MINIMUM_TIME_BETWEEN_UPDATES,
                    MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                    new MainActivity.MyLocationListener()
            );

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(location != null)
            {
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
        }
        return  latLng;
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {

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


// TODO:
/*
* [ ] Sửa lỗi lặp lại listView
* [ ] Thêm chức năng đăng kí, đăng nhập, đăng xuất
* [ ] Thêm chức năng gọi điện, nhắn tin
* [ ] Thêm chức năng chia sẻ quán ăn
* [ ] Viết review đánh giá.
*
 */