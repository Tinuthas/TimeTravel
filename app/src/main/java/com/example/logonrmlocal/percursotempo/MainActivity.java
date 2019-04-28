package com.example.logonrmlocal.percursotempo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_GPS = 1;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private double lat;
    private double lon;
    private double distanceTo;

    private Chronometer timeChronometer;
    private EditText searchEditText;
    private EditText travelEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeChronometer = findViewById(R.id.timeChrometer);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        travelEditText = findViewById(R.id.travelEditText);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                distanceTo += location.getAccuracy();
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
        };

        Button permissionButton = findViewById(R.id.permissionButton);
        permissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chechingPermissionGPS()) return;
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_GPS);
            }
        });

        Button turnOnGPSButton = findViewById(R.id.turnOnGpsButton);
        turnOnGPSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            2000, 5, locationListener);
                    return;
                }else Toast.makeText(MainActivity.this, getString(R.string.permission_not_granted),
                        Toast.LENGTH_SHORT).show();

            }
        });
        Button offGPSButton = findViewById(R.id.offGpsButton);
        offGPSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && (Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED)) != null)
                    locationManager.removeUpdates(locationListener);
                else Toast.makeText(MainActivity.this, (getString(R.string.gps_not_enable)),
                        Toast.LENGTH_SHORT).show();

            }
        });

        Button initTravelButton = findViewById(R.id.initTravelButton);
        initTravelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!chechingPermissionGPS() || Settings.Secure.getString(getContentResolver(),
                                Settings.Secure.LOCATION_PROVIDERS_ALLOWED) == null) {
                    Toast.makeText(MainActivity.this, (getString(R.string.gps_not_enable)),
                            Toast.LENGTH_SHORT).show();
                }else {
                    timeChronometer.setBase(SystemClock.elapsedRealtime());
                    distanceTo = 0;
                    travelEditText.setText(String.valueOf(distanceTo));
                    timeChronometer.start();
                }




            }
        });

        Button finishTravelButton = findViewById(R.id.finishTravelPercursoButton);
        finishTravelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if(timeChronometer.isCountDown()) return;
                }else if(timeChronometer.getBase() ==  SystemClock.elapsedRealtime()) return;
                timeChronometer.stop();
                travelEditText.setText(String.format("%s", distanceTo));
                Toast.makeText(MainActivity.this, (getString(R.string.distance)),
                        Toast.LENGTH_LONG).show();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!chechingPermissionGPS()) return;
                searchEditText = findViewById(R.id.searchEditText);
                String loca = searchEditText.getText().toString();
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(String.format("geo:%f,%f?q=" + loca,
                                lat, lon))).setPackage("com.google.android.apps.maps"));
            }
        });
    }

    private boolean chechingPermissionGPS() {
        return ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

}
