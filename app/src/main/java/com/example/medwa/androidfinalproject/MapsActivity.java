package com.example.medwa.androidfinalproject;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.maps.android.clustering.ClusterManager;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

// Map View Activity
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    // File and URI Declarations
    File photoFile = null;
    Uri photoURI;
    // MapView Declaration
    private MapView mMapView;
    // ImageButton Declarations
    private ImageButton mReportStatus; //Used for camera now
    private ImageButton mBTN_Track;
    private ImageButton mSettings;
    // ClusterManager for Status Markers Declarations
    private ClusterManager<StatusMarkers> mClusterManager;
    private MyClusterManager mClusterManagerRenderer;
    // GoogleMap Declaration
    private GoogleMap mGoogleMap;
    // FireBase Declarations
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private FirebaseUser user;
    // FusedLocation Declaration
    private FusedLocationProviderClient mFusedLocationProvider;
    // Permission Overrides
    private final static long UPDATE_INTERVAL = 4000;
    private final static long FASTEST_INTERVAL = 2000;
    // Polyline Declaration used for Google Maps
    Polyline mPolyline;
    // ArrayList of Longitude and Latitudes Declaration
    ArrayList<LatLng> mLocations;
    // Permission Overrides
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    // ImageView Declaration
    ImageView mImageView;
    // FireBase Storage and Reference Declaration
    FirebaseStorage mStorage;
    StorageReference mStorageRef;
    // String Declarations
    String mCurrentPhotoPath;
    StorageReference mStorage2;
    // MapView Bundle Key for API
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    ProgressDialog mProgress;

    // OnCreate called
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Assign ImageView with corresponding XML ID
        mImageView = (ImageView) findViewById(R.id.debugImage);
        // Associate ArrayList with new instance of ArrayList
        mLocations = new ArrayList<>();
        // Get Locations for Device
        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);
        // FireBase Storage Assignments
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        mStorage2 = FirebaseStorage.getInstance().getReference();
        // Progress Dialog assignment
        mProgress = new ProgressDialog(this);
        // FireBase Authentication and Database Assignments
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference();
        user = mAuth.getCurrentUser();
        // ImageButton Assigned to corresponding XML IDs
        mReportStatus = (ImageButton) findViewById(R.id.reportStatusButton); //Used for camera now
        mBTN_Track = (ImageButton) findViewById(R.id.BTN_Track);
        mSettings = (ImageButton) findViewById(R.id.settingsButton);

        //region Important Map Stuff
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        // Assigns MapView to corresponding XML ID
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
        //endregion

        //region Tracking Button
        mBTN_Track.setOnClickListener(v -> startLocationUpdates());

        //region Camera Button
        // Camera onClickListener
        mReportStatus.setOnClickListener(v->{dispatchTakePictureIntent();});
        // Button to go to settings
        mSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MapsActivity.this, Settings.class);
            startActivity(intent);
        });
    }

    // Take Pictures Intent
    private void dispatchTakePictureIntent() {
        Toast.makeText(this, "DISPATCH PICTURES CALLED!", Toast.LENGTH_SHORT).show();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go

            try {
                Toast.makeText(this, "TRYING TO CREATE IMAGE!", Toast.LENGTH_SHORT).show();
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created

            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.medwa.androidfinalproject",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
            Toast.makeText(this, "PHOTO FILE IS NULL!", Toast.LENGTH_SHORT).show();
        }
    }
    // Create Image File from Camera Intent
    private File createImageFile() throws IOException {
        Toast.makeText(this, "CREATE IMAGE CALLED!", Toast.LENGTH_SHORT).show();
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Gets absolute path of current Image
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    // onActivityResult called attempts to upload captured picture FireBase Storage
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(this, "ACTIVITY RESULT CALLED!", Toast.LENGTH_SHORT).show();
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode ==RESULT_OK && data!=null ) {

            final StorageReference ref = mStorageRef.child("Photos").child( UUID.randomUUID().toString());
            Toast.makeText(this, "TRUE ACTIVITY", Toast.LENGTH_SHORT).show();
            UploadTask uploadTask = ref.putFile(photoURI);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) throw task.getException();
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful())  Toast.makeText(MapsActivity.this, "Uploading Finished", Toast.LENGTH_SHORT).show();

                }
            });
        }
        //endregion

        //region Settings Button
        mSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MapsActivity.this, Settings.class);
            startActivity(intent);
        });
        //endregion

        //region Updates Database on Initialization and Value Changes
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("onDataChange", "onDataChange called.");

                if (mGoogleMap != null) {
                    if (mClusterManager == null) {
                        mClusterManager = new ClusterManager<StatusMarkers>(MapsActivity.this, mGoogleMap);
                    }
                    if (mClusterManagerRenderer == null) {
                        mClusterManagerRenderer = new MyClusterManager(MapsActivity.this, mGoogleMap, mClusterManager);
                        mClusterManager.setRenderer(mClusterManagerRenderer);
                    }
                    mClusterManager.clearItems();

                    //Iterates through database and updates the map with markers
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        RouteInformation routeInfo = new RouteInformation();

                        try {
                            routeInfo.setLatitude((Double) ds.child("lat").getValue());
                            routeInfo.setLongitude((Double) ds.child("long").getValue());
                            routeInfo.setBus(ds.child("bus").getValue().toString().trim());
                            routeInfo.setSnippet(ds.child("status").getValue().toString().trim());
                            routeInfo.setAvatar((long) ds.child("avatar").getValue());

                            StatusMarkers newStatusMarker = new StatusMarkers(new LatLng(routeInfo.getLatitude(), routeInfo.getLongitude()), routeInfo.getBus(), routeInfo.getSnippet(), (int) routeInfo.getAvatar());
                            mClusterManager.addItem(newStatusMarker);

                        } catch (Exception e) {
                            Log.e("addValueEventListener", "NullPointerException: " + e.getMessage());
                        }

                        Log.d("mData", ds.toString().trim());
                    }

                }
                //Populates the map
                mClusterManager.cluster();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //endregion

    }


    // Add Status Markers Method
    private void addStatusMarkers () {
        Log.d("mReportStatus Button", "addStatusMarkers was called.");

        RouteInformation routeInfo = new RouteInformation();

        getLocation();

        int image = R.drawable.delayed_bus;
        myRef.child(user.getUid()).child("bus").setValue("N70 Bus");
        myRef.child(user.getUid()).child("status").setValue("Running Late");
        myRef.child(user.getUid()).child("avatar").setValue(image);
    }

    // onSaveInstanceState called
    @Override
    public void onSaveInstanceState (Bundle outState){
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    // onResume called
    @Override
    public void onResume () {
        super.onResume();
        mMapView.onResume();
    }

    // onStart called
    @Override
    public void onStart () {
        super.onStart();
        mMapView.onStart();
    }

    // onStop called
    @Override
    public void onStop () {
        super.onStop();
        mMapView.onStop();
    }

    // onMapReady called
    @Override
    public void onMapReady (GoogleMap map){
        mGoogleMap = map;
        RouteInformation routeInfo = new RouteInformation();
        //map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        double bottom = routeInfo.getLatitude() - .1;
        double left = routeInfo.getLongitude() - .1;
        double top = routeInfo.getLatitude() + .1;
        double right = routeInfo.getLongitude() + .1;

        LatLngBounds boundary = new LatLngBounds(new LatLng(bottom, left), new LatLng(top, right));

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(boundary.getCenter(), 15));

    }

    // onPause called
    @Override
    public void onPause () {
        mMapView.onPause();
        super.onPause();
    }

    // onDestroy called
    @Override
    public void onDestroy () {
        mMapView.onDestroy();
        super.onDestroy();
    }

    // onLowMemory called
    @Override
    public void onLowMemory () {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    // Gets Location as long as Permissions allow
    public void getLocation () {
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

        mFusedLocationProvider.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();

                    try {
                        myRef.child(user.getUid()).child("lat").setValue(location.getLatitude());
                        myRef.child(user.getUid()).child("long").setValue(location.getLongitude());
                    } catch (Exception e) {
                        Log.e("getLocation", e.getMessage());
                    }
                }
            }
        });
    }

    //region Method used for tracking location
    private void startLocationUpdates () {
            mBTN_Track.setBackgroundColor(Color.RED);
            LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
            mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
            mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);

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
            mFusedLocationProvider.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult result) {

                            Location location = result.getLastLocation();

                            if (location != null) {
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                mLocations.add(latLng);

                                myRef.child(user.getUid()).child("lat").setValue(location.getLatitude());
                                myRef.child(user.getUid()).child("long").setValue(location.getLongitude());

                                PolylineOptions options = new PolylineOptions().color(Color.BLUE);
                                for (int i = 0; i < mLocations.size(); i++) {
                                    options.add(mLocations.get(i));
                                }
                                mPolyline = mGoogleMap.addPolyline(options);


                                //mPolyline = mGoogleMap.addPolyline(new PolylineOptions().clickable(true).add(
                                //        new LatLng(location.getLatitude(), location.getLongitude())
                                // ));

                            }
                        }
                    },
                    Looper.myLooper());
    }
    //endregion
}