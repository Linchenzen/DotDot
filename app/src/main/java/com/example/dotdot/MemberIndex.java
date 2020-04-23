package com.example.dotdot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.dotdot.QRcode.QrcodeCreate;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MemberIndex extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private static final int RESQUEST_PERMISSION_LOCATION = 1;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference note = db.collection("Member")
            .document("iICTR1JL4eAG4B3QBi1S").collection("loyalty_card");
    //記得改成session


    Button home;
    Button btn_dot;
    Button btn_notificaiton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberindex);

        //------------------------Map---------------------------------------------------------------
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //-----------------------左下功能列----------------------------------------------------------
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //------------------------action-----------------------------------------------------------
        home = (Button) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PopActivity.class);
                startActivity(i);
            }
        });


        //------------------------QRcode-----------------------------------------------------------
        btn_dot = (Button) findViewById(R.id.btn_dot);
        btn_dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(getApplicationContext(), QrcodeCreate.class);
                startActivity(j);
            }
        });

        //------------------------Notification-----------------------------------------------------------
        btn_notificaiton = findViewById(R.id.notification);
        btn_notificaiton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent n = new Intent(getApplicationContext(), notification.class);
                startActivity(n);
            }
        });


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        addmarker();
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style));
        mMap.setInfoWindowAdapter(new MapInforWindowAdapter(this));
        mMap.setOnInfoWindowClickListener(this);
/** this code is used to get the permission/ check the permission allow or not*/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.
                    ACCESS_FINE_LOCATION}, RESQUEST_PERMISSION_LOCATION);
        } else {
            getMyLocation();
            Toast.makeText(this, "登入成功", Toast.LENGTH_LONG).show();
        }

        mMap.setMyLocationEnabled(true);

    }

    public void addmarker() {

        note.document("BxskPfoZCfztCUSuDUOu").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Loyalty_card poi = documentSnapshot.toObject(Loyalty_card.class);
                    MarkerOptions options = new MarkerOptions();
                    String title = poi.getStore();
                    String point = poi.getPoints_owned();
                    options.title(title);
                    options.snippet(point);
                    options.position(storerecord.chicken);
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.shop1));
                    mMap.addMarker(options);

                }

            }
        });

        note.document("luyQa2eRYr3k4q22hq7T").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Loyalty_card poi = documentSnapshot.toObject(Loyalty_card.class);
                    MarkerOptions options1 = new MarkerOptions();
                    String title = poi.getStore();
                    String point = poi.getPoints_owned();
                    options1.title(title);
                    options1.snippet(point);
                    options1.position(storerecord.loc1);
                    options1.icon(BitmapDescriptorFactory.fromResource(R.drawable.shop1));
                    mMap.addMarker(options1);

                }

            }
        });

    }


    @SuppressLint("MissingPermission")
    private void getMyLocation() {
        mfusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng mylocation = new LatLng(location.getLatitude(), location.getLongitude());
                    //記得改成mylocation
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 17));

                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RESQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getMyLocation();
            } else {
                Toast.makeText(this, "this is permission is mandatory", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RESQUEST_PERMISSION_LOCATION);
            }
        }
    }


    @Override
    public void onInfoWindowClick(Marker marker) {

        Intent intent = new Intent(MemberIndex.this, Collectioncard.class);

    }


}
