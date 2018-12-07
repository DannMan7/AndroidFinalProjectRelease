package com.example.medwa.androidfinalproject;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.w3c.dom.Text;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mMapView;
    public Button mReportStatus;
    private Button mBTN_Track;
    private ClusterManager<StatusMarkers> mClusterManager;
    private MyClusterManager mClusterManagerRenderer;
    private GoogleMap mGoogleMap;
    //private RouteInformation routeInfo;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private FusedLocationProviderClient mFusedLocationProvider;




    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);

        //routeInfo = new RouteInformation();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference();
        user = mAuth.getCurrentUser();

        mReportStatus = (Button) findViewById(R.id.reportStatusButton);
        mBTN_Track = (Button) findViewById(R.id.BTN_Track);

        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

        mReportStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("mReportStatus Button","Button was clicked.");
                addStatusMarkers();

            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("onDataChange","onDataChange called.");

                if(mGoogleMap != null) {
                    if(mClusterManager == null){
                        mClusterManager = new ClusterManager<StatusMarkers>(MapsActivity.this, mGoogleMap);
                    }
                    if(mClusterManagerRenderer == null){
                        mClusterManagerRenderer = new MyClusterManager(MapsActivity.this, mGoogleMap, mClusterManager);
                        mClusterManager.setRenderer(mClusterManagerRenderer);
                    }
                    mClusterManager.clearItems();

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
                mClusterManager.cluster();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void addStatusMarkers(){
        Log.d("mReportStatus Button","addStatusMarkers was called.");

        RouteInformation routeInfo = new RouteInformation();

        getLocation();

        int image = R.drawable.delayed_bus;
        myRef.child(user.getUid()).child("bus").setValue("N70 Bus");
        myRef.child(user.getUid()).child("status").setValue("Running Late");
        myRef.child(user.getUid()).child("avatar").setValue(image);

        // myRef.child(user.getUid()).child("lat").setValue(routeInfo.getLatitude());
        //myRef.child(user.getUid()).child("long").setValue(routeInfo.getLongitude());


       /* if(mGoogleMap != null){

            if(mClusterManager == null){
                mClusterManager = new ClusterManager<StatusMarkers>(this.getApplicationContext(), mGoogleMap);
            }
            if(mClusterManagerRenderer == null){
                mClusterManagerRenderer = new MyClusterManager(this, mGoogleMap, mClusterManager);
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }

                try{
                    String snippet = "";
                    //TODO: Create another list view for statuses/snippets
                    snippet = "Running Late";

                    int avatar = R.drawable.delayed_bus; // set the default avatar

                    StatusMarkers newStatusMarker = new StatusMarkers(new LatLng(routeInfo.getLatitude(), routeInfo.getLongitude()),routeInfo.getBus(), snippet, avatar );

                    mClusterManager.addItem(newStatusMarker);

                    FirebaseUser user = mAuth.getCurrentUser();

                    myRef.child(user.getUid()).child("avatar").setValue(avatar);
                    myRef.child(user.getUid()).child("lat").setValue(routeInfo.getLatitude());
                    myRef.child(user.getUid()).child("long").setValue(routeInfo.getLongitude());
                    myRef.child(user.getUid()).child("status").setValue(snippet);

                    Log.d("mReportStatus Button", "Marker was created.");

                }catch (NullPointerException e){
                    Log.e("AddMapMarkers: ", "addMapMarkers: NullPointerException: " + e.getMessage() );
                }

            mClusterManager.cluster();

        }*/
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        RouteInformation routeInfo = new RouteInformation();
        //map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        double bottom = routeInfo.getLatitude()-.1;
        double left = routeInfo.getLongitude()-.1;
        double top = routeInfo.getLatitude()+.1;
        double right = routeInfo.getLongitude()+.1;

        LatLngBounds boundary = new LatLngBounds(new LatLng(bottom,left), new LatLng(top,right));

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(boundary.getCenter(),15));

    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();


    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

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

        mFusedLocationProvider.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    Location location = task.getResult();

                    try {
                        myRef.child(user.getUid()).child("lat").setValue(location.getLatitude());
                        myRef.child(user.getUid()).child("long").setValue(location.getLongitude());
                    }
                    catch(Exception e){
                        Log.e("getLocation", e.getMessage());
                    }
                }
            }
        });
    }

}
