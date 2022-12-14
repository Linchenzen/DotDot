package com.example.dotdot.MemberCouponManager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.dotdot.Coupon;
import com.example.dotdot.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//OK
public class ConfirmExchange extends Activity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference storeRef = db.collection("store");
    private CollectionReference memRef = db.collection("Member");
    int storeDotNeed;
    int countCoupon;
    int countDot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_exchange);
        TextView memberPoint = findViewById(R.id.memberPoint);
        TextView couponPoint = (TextView) findViewById(R.id.couponPoint);
        TextView wordDot = (TextView) findViewById(R.id.wordDot);
        TextView wordDot2 = (TextView) findViewById(R.id.wordDot2);
        TextView word1 = (TextView) findViewById(R.id.word1);
        TextView word2 = (TextView) findViewById(R.id.word2);
        TextView wordWarning = (TextView) findViewById(R.id.wordWarning);
        TextView text = (TextView) findViewById(R.id.text);
        Button doubleCheckBtn = (Button) findViewById(R.id.doubleCheckBtn);
        Button cancelBtn = findViewById(R.id.CancelBtn2);
        Button doubleCeckBtn2 = findViewById(R.id.doubleCheckBtn2);

        doubleCeckBtn2.setVisibility(View.INVISIBLE);

        //coupon???title??????
        String whichCoupon = getSharedPreferences("save_coupon", MODE_PRIVATE)
                .getString("coupon_title", "?????????Coupon");

        //member?????????Id
        String memberId = getSharedPreferences("save_memberId", MODE_PRIVATE)
                .getString("user_id", "???????????????");

        //coupon???id
        String CouponId = getSharedPreferences("save_couponId", MODE_PRIVATE)
                .getString("coupon_id", "?????????Coupon");

        //store?????????Id
        String storeId = getSharedPreferences("save_storeId", MODE_PRIVATE)
                .getString("store_id", "???????????????");

        //loyalty_card?????????Id
        String loyalty_card_id = getSharedPreferences("save_loyalty_card_id", MODE_PRIVATE)
                .getString("loyalty_card_id", "???????????????");

        //memberPointOwned
        int memberPointOwned = getSharedPreferences("save_memberPointOwned", MODE_PRIVATE)
                .getInt("memberPointOwned", 123);

        memberPoint.setText(Integer.toString(memberPointOwned));

        storeRef.document(storeId).collection("coupon")
                .whereEqualTo("couponTitle", whichCoupon)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Coupon coupon = documentSnapshot.toObject(Coupon.class);
                            couponPoint.setText(Integer.toString(coupon.getDotNeed()));
                            storeDotNeed = coupon.getDotNeed();
                        }
                    }
                });

        doubleCheckBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                memberPoint.setText("");
                couponPoint.setText("");
                wordDot.setText("");
                wordDot2.setText("");
                /*wordWarning.setText("");*/
                word1.setText("");
                word2.setText("");
                //????????????
                if (memberPointOwned < storeDotNeed) {
                    doubleCheckBtn.setVisibility(View.INVISIBLE);
                    cancelBtn.setVisibility(View.INVISIBLE);
                    doubleCeckBtn2.setVisibility(View.VISIBLE);
                    text.setText("?????????????????????><");
                } else {
                    doubleCheckBtn.setVisibility(View.INVISIBLE);
                    cancelBtn.setVisibility(View.INVISIBLE);
                    doubleCeckBtn2.setVisibility(View.VISIBLE);

                    int total = memberPointOwned - storeDotNeed;
                    text.setText("????????????\n?????? " + total + " ???");

                    //??????memberPointQwned???????????????
                    SharedPreferences memberPointOwnedpref = getSharedPreferences("save_memberPointOwned", MODE_PRIVATE);
                    memberPointOwnedpref.edit()
                            .putInt("memberPointOwned", total)
                            .apply();

                    //????????????????????????Coupon???
                    Map<Object, Object> ownedCoupon = new HashMap<>();
                    ownedCoupon.put("couponTitle", whichCoupon);
                    ownedCoupon.put("couponId", CouponId);
                    ownedCoupon.put("dotNeed", storeDotNeed);
                    ownedCoupon.put("time", new Date());
                    memRef.document(memberId)
                            .collection("loyalty_card").document(loyalty_card_id)
                            .collection("Owned_Coupon").add(ownedCoupon);

                    //???????????????????????????????????????????????????????????????????????????
                    db.collection("Member").document(memberId)
                            .collection("loyalty_card").document(loyalty_card_id)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        countCoupon = Integer.parseInt(documentSnapshot.getString("couponCount"));
                                        countDot = Integer.parseInt(documentSnapshot.getString("dotUse"));

                                        countCoupon = countCoupon + 1;
                                        countDot += storeDotNeed;
                                        Map<Object, Object> upData = new HashMap<>();
                                        upData.put("points_owned", Integer.toString(total));
                                        upData.put("couponCount", Integer.toString(countCoupon));
                                        upData.put("dotUse", Integer.toString(countDot));
                                        db.collection("Member").document(memberId)
                                                .collection("loyalty_card").document(loyalty_card_id)
                                                .set(upData, SetOptions.merge());
                                    }
                                }
                            });

                    //???????????????member???Loyalty???Record
                    String i = Integer.toString(storeDotNeed);
                    String use = "-" + i;
                    Map<Object, Object> dotUseRecord = new HashMap<>();
                    dotUseRecord.put("store_couponId", CouponId);
                    dotUseRecord.put("couponTitle", whichCoupon);
                    dotUseRecord.put("storeId", storeId);
                    dotUseRecord.put("point_use", use);
                    dotUseRecord.put("time", new Date());
                    memRef.document(memberId).collection("loyalty_card")
                            .document(loyalty_card_id).collection("Record")
                            .add(dotUseRecord);

                    memRef.document(memberId).collection("loyalty_card")
                            .document(loyalty_card_id).collection("DotUse")
                            .add(dotUseRecord);


                    db.collection("Record").document(memberId).collection("record").add(dotUseRecord);
                    //???????????????????????????
                    Map<Object, Object> couponBeenExchange = new HashMap<>();
                    couponBeenExchange.put("store_couponId", CouponId);
                    couponBeenExchange.put("time", new Date());
                    storeRef.document(storeId).collection("couponBeenExchange")
                            .add(couponBeenExchange);
                }
                doubleCeckBtn2.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //??????????????????---------------------------------------------------------------------
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .7), (int) (height * .4));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;

        params.x = 0;
        params.y = -10;

        getWindow().setAttributes(params);
    }
}
