package com.example.medwa.androidfinalproject;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
// Main Login Activity
public class MainActivity extends AppCompatActivity {

    // TextInputEditText Declarations
    TextInputEditText email;
    TextInputEditText password;
    // FireBase Declarations
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    // Permission Location Boolean Declaration
    private boolean mLocationPermissionGranted = false;
    // Fused Location Declaration
    private FusedLocationProviderClient mFusedLocationProvider;
    // Route Info Declaration
    private RouteInformation routeInfo;
    // Progress Bar Declaration
    private ProgressBar progressBar;
    // Permission Overriding
    final static int MY_PERMISSIONS_REQUEST_CAMERA = 200;
    final static int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 200;
    final static int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 200;
    // AdView for AdMob
    private AdView mAdView;


    // OnCreate Called
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Update with new RouteInformation
        routeInfo = new RouteInformation();

        // Get new fused location from device
        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);

        // Assign TextInputEditTexts with corresponding XML ID's
        email = (TextInputEditText) findViewById(R.id.loginTxt);
        password = (TextInputEditText) findViewById(R.id.passwordTxt2);

        // FireBase linkage
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference();

        // Assign ProgressBar to corresponding XML ID
        progressBar = findViewById(R.id.PB_LOG);

        // AdMob Initialization
        MobileAds.initialize(MainActivity.this, "ca-app-pub-3940256099942544~334751171");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    // OnResume Called
    @Override
    protected void onResume() {
        super.onResume();

        // Checks to see if permissions are already granted and gets current information
        if (checkMapServices()) {
            if (!mLocationPermissionGranted) {
                getLocationPermission();

                if(mLocationPermissionGranted){
                    getLocation();

                }
            }
        }
    }

    // Gets Location of device but checks to see if it has permissions to do so first
    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // As long as the permissions are granted the location will update to the current context
        mFusedLocationProvider.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    Location location = task.getResult();

                    try {
                        routeInfo.setLatitude(location.getLatitude());
                        routeInfo.setLongitude(location.getLongitude());
                    }
                    catch(NullPointerException e){
                        buildAlertMessageNoGps();
                    }
                }
            }
        });
    }

    // Create Account Button Click calls from XML
    public void registerClick(View view) {
        Intent intent = new Intent(this, RegisterScreen.class);
        startActivity(intent);
    }
    // Login Button Click calls from XML
    public void loginClick(View view) {
        String e = email.getText().toString();
        String p = password.getText().toString();

        // Checks to make sure both EditTexts are entered with information
        if (TextUtils.isEmpty(e) || TextUtils.isEmpty(p)) {

            Toast.makeText(this, "Email or Password field is empty!", Toast.LENGTH_SHORT).show();
        } else {

            // Progress bar becomes visible that way the User knows if they are logging in or not.
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(e, p).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                        // When the task is successful the User's information will then be updated
                        // with the current latitude and longitude
                        FirebaseUser user = mAuth.getCurrentUser();

                        myRef.child(user.getUid()).child("lat").setValue(routeInfo.getLatitude());
                        myRef.child(user.getUid()).child("long").setValue(routeInfo.getLongitude());

                        // New Intent will send the User to the MapView Activity
                        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                        startActivity(intent);
                        // Progress Bar is set to gone that way if the User logs out they will not
                        // see and infinitely scrolling progress bar
                        progressBar.setVisibility(View.GONE);
                        //finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }


    // Checking of permissions before login can happen
    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                if(isCameraEnabled()) {
                    if(isSaveableEnabled()) {
                        if(isReadableEnabled()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // Checks to see if user Enabled GPS
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS services are required for this app to function properly. Turn it on?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(enableGpsIntent, 9002);
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    // Checks to see if User Enabled Camera
    public boolean isCameraEnabled() {

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
        return true;
    }

    // Checks to see if User allowed writable storage
    public boolean isSaveableEnabled() {

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
        return true;
    }

    // Checks to see if user allowed readable storage
    public boolean isReadableEnabled() {

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
        return true;
    }
    // Checks to see if user allowed GPS location
    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }

        return true;
    }
    // Gets the Location Permission
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 9003);
        }
    }

    // Checks to make sure Google Play services are available
    public boolean isServicesOK() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, 9001);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    // Checks to make sure permissions are correct before continuing
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case 9003: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    // Check to make sure permissions are correct before continuing
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 9002: {
                if (!mLocationPermissionGranted) {
                    getLocationPermission();
                }
            }
        }

    }
}