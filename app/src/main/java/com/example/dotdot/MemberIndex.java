package com.example.dotdot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;


import com.example.dotdot.BotNav_mem.botnav2;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.dotdot.App.CHANNEL_1_ID;
import static android.widget.Toast.makeText;

public class MemberIndex extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private static final int RESQUEST_PERMISSION_LOCATION = 1;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference storeref = db.collection("store");
    private CollectionReference loy = db.collection("Member")
            .document("iICTR1JL4eAG4B3QBi1S").collection("loyalty_card");
    //????????????session
    private CollectionReference memref = db.collection("Member");

    private NotificationManagerCompat notificationManager;
    private String dot;
    private String sto;
    private String str;
    private String nowdt;

    Button home;
    Button btn_dot;
    Button btn_notificaiton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberindex);

        notificationManager = NotificationManagerCompat.from(this);
        //------------------------Map---------------------------------------------------------------
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //-----------------------???????????????----------------------------------------------------------
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

        String memberId = getSharedPreferences("save_memberId", MODE_PRIVATE)
                .getString("user_id", "???????????????");

        if (memberId.equals("???????????????")) {
            Intent r = new Intent(getApplicationContext(), Nonelogin.class);
            startActivity(r);

        } else {
            addmarker();
            makeText(this, "????????????", Toast.LENGTH_LONG).show();

        }
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

        }

        mMap.setMyLocationEnabled(true);

    }

    public void addmarker() {

        //member?????????Id
        String memberId = getSharedPreferences("save_memberId", MODE_PRIVATE)
                .getString("user_id", "???????????????");
        memref.document(memberId).collection("loyalty_card")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    Loyalty_card poi = queryDocumentSnapshot.toObject(Loyalty_card.class);
                    String point = poi.getPoints_owned();

                    MarkerOptions options = new MarkerOptions();
                    options.title("???????????????");
                    options.snippet(point);
                    options.position(storerecord.chicken);
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.shop1));
                    mMap.addMarker(options);

                    MarkerOptions options1 = new MarkerOptions();
                    options1.title("???????????????");
                    options1.snippet(point);
                    options1.position(storerecord.loc1);
                    options1.icon(BitmapDescriptorFactory.fromResource(R.drawable.shop2));
                    mMap.addMarker(options1);

                    MarkerOptions options2 = new MarkerOptions();
                    options2.title("?????????");
                    options2.snippet(point);
                    options2.position(storerecord.loc2);
                    options2.icon(BitmapDescriptorFactory.fromResource(R.drawable.shop3));
                    mMap.addMarker(options2);


                }

            }
        });


    }
////////

    @SuppressLint("MissingPermission")
    private void getMyLocation() {
        mfusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng mylocation = new LatLng(location.getLatitude(), location.getLongitude());
                    //????????????mylocation
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(storerecord.chicken, 19));

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

        Intent intent = new Intent(MemberIndex.this, botnav2.class);
        startActivity(intent);

    }

    public void noti() {
        loy.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String id = documentSnapshot.getId();
                            loy.document(id).collection("Record")
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                MemberPointRec m = documentSnapshot.toObject(MemberPointRec.class);
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                //Date c = m.getTime();
                                                //Date newDate = new Date(c.getTime() + 28800000);//?????????????????? ???+8??????
                                                String dead = sdf.format(m.getTime());

                                                if (nowdt == null) { //???????????????????????????????????????????????????
                                                    dot = m.getPoint_get();
                                                    if (dot != null) {  //?????????"????????????"?????????
                                                        sto = m.getStoreId();

                                                        storeref.document(sto) //storeID????????????
                                                                .get()
                                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                        Store store = documentSnapshot.toObject(Store.class);
                                                                        str = store.getName();
                                                                        sendOnChannel(dot, str);
                                                                    }
                                                                });
                                                        nowdt = dead;
                                                        break;
                                                    }
                                                } else { //???????????????????????????????????????????????????
                                                    if (compareDate(nowdt, dead) == true) {  //???????????????????????????????????????
                                                        dot = m.getPoint_get();
                                                        if (dot != null) {  //??????????????????????????????
                                                            sto = m.getStoreId();

                                                            storeref.document(sto) //storeID????????????
                                                                    .get()
                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                            Store store = documentSnapshot.toObject(Store.class);
                                                                            str = store.getName();
                                                                            sendOnChannel(dot, str);
                                                                        }
                                                                    });
                                                            nowdt = dead;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });
                            break;
                        }
                    }
                });
    }

    public void sendOnChannel(String dot, String str) {//????????????????????????
        SharedPreferences session = getSharedPreferences("get_dot_time", MODE_PRIVATE);
        final SharedPreferences.Editor editor = session.edit();
        String time = session.getString("newTime", "???????????????");

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_ok)
                .setContentTitle("????????????")
                .setContentText("??????" + str + "?????????" + dot + "???")
                .setWhen(System.currentTimeMillis())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
    }

    public boolean compareDate(String nowDate, String compareDate) { //??????????????????
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            Date now = df.parse(nowDate);
            Date compare = df.parse(compareDate);
            if (now.before(compare)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
