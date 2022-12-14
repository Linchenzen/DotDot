package com.example.dotdot.StoreCouponManager;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.dotdot.Coupon;
import com.example.dotdot.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//OK
public class StoreCreatCoupon extends Activity {
    private TextView et_startTime;
    private TextView et_endTime;
    private Date startTime = new Date();
    private Date endTime = new Date();
    private TimePickerView pvTime;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference storeRef = db.collection("store");
    String beginning, ending;
    int tit = 1;//title
    int dot = 1;//need
    int con = 1;//content
    int Stime = 1;//starttime
    int Etime = 1;//endtime

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_store_creat_coupon);

        Button creatBtn = (Button) findViewById(R.id.creatBtn);
        Button backBtn = (Button) findViewById(R.id.backBtn);
        creatBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText couponTitle = (EditText) findViewById(R.id.couponTitle);
                AutoCompleteTextView couponContent = (AutoCompleteTextView) findViewById(R.id.couponContent);
                EditText dotNeed = (EditText) findViewById(R.id.dotNeed);

                if (couponTitle.length() == 0) {
                    tit = 0;
                    Toast t = Toast.makeText(StoreCreatCoupon.this, "???????????????????????????", Toast.LENGTH_LONG);
                    TextView tv = (TextView) t.getView().findViewById(android.R.id.message);
                    tv.setTextSize(30);
                    t.show();
                } else if (couponContent.length() == 0) {
                    con = 0;
                    Toast t = Toast.makeText(StoreCreatCoupon.this, "??????????????????????????? !", Toast.LENGTH_LONG);
                    TextView tv = (TextView) t.getView().findViewById(android.R.id.message);
                    tv.setTextSize(30);
                    t.show();
                } else if (dotNeed.length() == 0) {
                    dot = 0;
                    Toast t = Toast.makeText(StoreCreatCoupon.this, "???????????????????????? !", Toast.LENGTH_LONG);
                    TextView tv = (TextView) t.getView().findViewById(android.R.id.message);
                    tv.setTextSize(30);
                    t.show();
                } else if (beginning.length() == 0) {
                    Stime = 0;
                    Toast t = Toast.makeText(StoreCreatCoupon.this, "????????????????????? !", Toast.LENGTH_LONG);
                    TextView tv = (TextView) t.getView().findViewById(android.R.id.message);
                    tv.setTextSize(30);
                    t.show();
                } else if (ending.length() == 0) {
                    Etime = 0;
                    Toast t = Toast.makeText(StoreCreatCoupon.this, "????????????????????? !", Toast.LENGTH_LONG);
                    TextView tv = (TextView) t.getView().findViewById(android.R.id.message);
                    tv.setTextSize(30);
                    t.show();
                }

                if (tit == 1 && dot == 1 && con == 1 && Stime == 1 && Etime == 1) {
                    creatCoupon();
                    Toast t = Toast.makeText(StoreCreatCoupon.this, "???????????? !", Toast.LENGTH_LONG);
                    TextView tv = (TextView) t.getView().findViewById(android.R.id.message);
                    tv.setTextSize(30);
                    t.show();
                    onBackPressed();
                }
                tit = 1;
                dot = 1;
                con = 1;
                Stime = 1;
                Etime = 1;//????????????
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //??????????????????---------------------------------------------------------------------

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .9));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;

        params.x = 0;
        params.y = -10;

        getWindow().setAttributes(params);

        //????????????--------------------------------------------------------------------------
        et_startTime = findViewById(R.id.et_startTime);
        et_endTime = findViewById(R.id.et_endTime);
        et_startTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.et_startTime:
                        if (pvTime != null) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(startTime);
                            pvTime.setDate(calendar);
                            pvTime.show(view);
                        }
                        break;
                    case R.id.et_endTime:
                        if (pvTime != null) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(endTime);
                            pvTime.setDate(calendar);
                            pvTime.show(view);
                        }
                        break;
                }
            }
        });
        et_endTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.et_startTime:
                        if (pvTime != null) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(startTime);
                            pvTime.setDate(calendar);
                            pvTime.show(view);
                        }
                        break;
                    case R.id.et_endTime:
                        if (pvTime != null) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(endTime);
                            pvTime.setDate(calendar);
                            pvTime.show(view);
                        }
                        break;
                }
            }
        });
        initTimePicker();
    }

    private void initTimePicker() {
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                //????????????????????????EditText
                if (v.getId() == R.id.et_startTime) {
                    startTime = date;
                    TextView editText = (TextView) v;
                    editText.setText(getTime(date));
                    beginning = getTime(date);
                } else {
                    endTime = date;
                    TextView editText = (TextView) v;
                    editText.setText(getTime(date));
                    ending = getTime(date);
                }
            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                    }
                })
                .setCancelText("??????")//??????????????????
                .setSubmitText("??????")//??????????????????
                .setTitleSize(20)//??????????????????
                .setTitleText("????????????")//????????????
                .setOutSideCancelable(false)//?????????????????????????????????????????????????????????????????????
                .isCyclic(true)//??????????????????
                .setTitleColor(Color.BLACK)//??????????????????
                .setSubmitColor(Color.BLUE)//????????????????????????
                .setCancelColor(Color.RED)//????????????????????????
                .setTitleBgColor(Color.WHITE)//??????????????????
                .setLabel("???", "???", "???", "???", "???", "???")
                .isDialog(true)//?????????????????????????????????
                .setType(new boolean[]{true, true, true, true, true, false})
                .build();
        Dialog mDialog = pvTime.getDialog();
        if (mDialog != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);
            params.leftMargin = 0;
            params.rightMargin = 0;
            pvTime.getDialogContainerLayout().setLayoutParams(params);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//??????????????????
                dialogWindow.setGravity(Gravity.BOTTOM);//??????Bottom,????????????
            }
        }
    }


    static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String getTime(Date date) {//???????????????????????????????????????
        return format.format(date);
    }

    public Date getDateFromString(String datetoSaved) {
        try {
            Date date = format.parse(datetoSaved);
            return date;
        } catch (ParseException e) {
            return null;
        }
    }

    public void creatCoupon() {
        EditText couponTitle = (EditText) findViewById(R.id.couponTitle);
        AutoCompleteTextView couponContent = (AutoCompleteTextView) findViewById(R.id.couponContent);
        EditText dotNeed = (EditText) findViewById(R.id.dotNeed);
        String title = couponTitle.getText().toString();
        String content = couponContent.getText().toString();
        Integer need = Integer.parseInt(dotNeed.getText().toString());
        Coupon coupon = new Coupon(getDateFromString(beginning), getDateFromString(ending), title, content, need);
        //??????ID
        String storeID = getSharedPreferences("save_storeId", MODE_PRIVATE)
                .getString("user_id", "???????????????");

        storeRef.document(storeID).collection("coupon")
                .add(coupon);

    }

    //-------------------------------------------------------------------------------------
}

