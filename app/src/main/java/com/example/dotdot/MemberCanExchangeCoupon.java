package com.example.dotdot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static android.content.Context.MODE_PRIVATE;

public class MemberCanExchangeCoupon extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference couponRef = db.collection("store");
    private CollectionReference memRef = db.collection("Member");
    private MemCouponAdapter adapter;
    private View view;
    int memberPointOwned = 80;//記得改

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_member_can_exchange_coupon, container, false);

        //跳轉頁到MemberOverlookCoupon
        Button overlookBtn = (Button)view.findViewById(R.id.overlookBtn);
        overlookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                MemberOverlookCoupon llf = new MemberOverlookCoupon();
                ft.replace(R.id.fragment_container, llf);
                ft.commit();
            }
        });
        //跳轉頁到MemberOwnedCoupon
        Button ownedCoupon = (Button)view.findViewById(R.id.ownedCoupon);
        overlookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                MemberOverlookCoupon llf = new MemberOverlookCoupon();
                ft.replace(R.id.fragment_container, llf);
                ft.commit();
            }
        });
        //store的亂碼Id
        String storeId = getActivity().getSharedPreferences("save_storeId", MODE_PRIVATE)
                .getString("store_id", "沒選擇店家");
        TextView test = (TextView)view.findViewById(R.id.test);
        //記得要改成活的
        memRef.document("iICTR1JL4eAG4B3QBi1S").collection("loyalty_card")
                .whereEqualTo("store", storeId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Loyalty_card loyalty_card = documentSnapshot.toObject(Loyalty_card.class);
                            memberPointOwned = Integer.parseInt(loyalty_card.getPoints_owned());
                            test.setText(Integer.toString(memberPointOwned));
                        }
                    }
                });
        setUpRecyclerView();
        return view;
    }

    private void setUpRecyclerView() {
        //store的亂碼Id
        String storeId = this.getActivity().getSharedPreferences("save_storeId", MODE_PRIVATE)
                .getString("store_id", "沒選擇店家");

        Query query = couponRef.document(storeId).collection("coupon")
                .whereLessThanOrEqualTo("dotNeed",memberPointOwned);

        FirestoreRecyclerOptions<Coupon> options = new FirestoreRecyclerOptions.Builder<Coupon>()
                .setQuery(query, Coupon.class)
                .build();

        adapter = new MemCouponAdapter(options);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setAdapter(adapter);

        //coupon的title名稱
        SharedPreferences couponpref = this.getActivity().getSharedPreferences("save_coupon", MODE_PRIVATE);

        //coupon的Id名稱
        SharedPreferences couponIdpref = this.getActivity().getSharedPreferences("save_couponId", MODE_PRIVATE);

        adapter.setOnItemClickListener(new MemCouponAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Coupon coupon = documentSnapshot.toObject(Coupon.class);
                couponpref.edit()
                        .putString("coupon_title", coupon.getCouponTitle())
                        .commit();
                couponIdpref.edit()
                        .putString("coupon_id",documentSnapshot.getId())
                        .commit();
                Intent intent = new Intent(getActivity(), MemCouponContent.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}