package com.example.dotdot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static android.content.Context.MODE_PRIVATE;


public class MapInforWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference note = db.collection("Member").document("BFyN264km5dWWtTPYivZ")
            .collection("loyalty_card").document("3fVoEdfNqgmBlwjgAFMQ");
    private Context context;
    public MapInforWindowAdapter(Context context) {
        this.context = context;

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)


    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getInfoContents(Marker marker) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_infor_map, null);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView Message1 = view.findViewById(R.id.tvMessage1);
        tvTitle.setText(marker.getTitle());



        return view;
    }
}