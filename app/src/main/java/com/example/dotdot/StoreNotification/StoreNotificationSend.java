package com.example.dotdot.StoreNotification;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dotdot.Note_store_noit;
import com.example.dotdot.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import static android.content.Context.MODE_PRIVATE;

public class StoreNotificationSend extends Fragment {




    private View PrivateView;
    private SwipeRecyclerView notiList;
    private StoreNotiSend_Adapter adapter;
    private Button btn;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notiRef = db.collection("store");


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        PrivateView = inflater.inflate(R.layout.store_notification_send , container , false);

        notiList = (SwipeRecyclerView) PrivateView.findViewById(R.id.StoreNotiRecyclerView);
        setUpRecyclerView();
        btn = (Button)PrivateView.findViewById(R.id.newbtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ccc(v);
            }
        });
        return PrivateView;
    }
    public void ccc(View view){
        Intent h = new Intent(getActivity(), StoreNotiSending.class);
        startActivity(h);
    }

    public void setUpRecyclerView(){
        String storeID =getActivity().getSharedPreferences("save_storeId", MODE_PRIVATE)
                .getString("user_id", "???????????????");

        Query query = notiRef.document(storeID).collection("Notice").orderBy("time", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Note_store_noit> options = new FirestoreRecyclerOptions.Builder<Note_store_noit>()
                .setQuery(query, Note_store_noit.class)
                .build();

        notiList.setOnItemClickListener(mItemClickListener);
        notiList.setSwipeMenuCreator(mSwipeMenuCreator);
        notiList.setOnItemMenuClickListener(mItemMenuClickListener);

        adapter = new StoreNotiSend_Adapter(options);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        notiList.setLayoutManager(linearLayoutManager);
/*
        notiList.addItemDecoration(
                new DefaultItemDecoration(ContextCompat.getColor(getContext(), R.color.divider_color)));
*/
        notiList.setHasFixedSize(true);
        notiList.setAdapter(adapter);
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

    /**
     * RecyclerView???Item???????????????
     */
    private OnItemClickListener mItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View itemView, int position) {
            Toast.makeText(getContext(), "???" + position + "???", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * RecyclerView???Item??????Menu???????????????
     */
    private OnItemMenuClickListener mItemMenuClickListener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int position) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // ???????????????????????????
            int menuPosition = menuBridge.getPosition(); // ?????????RecyclerView???Item??????Position???

            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                Toast.makeText(getContext(), "list???" + position + "; ???????????????" + menuPosition, Toast.LENGTH_SHORT).show();
                adapter.deleteItem(position);
            }
        }
    };

    /**
     * ?????????????????????Item?????????????????????????????????
     */
    private SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int position) {
            int width = getResources().getDimensionPixelSize(R.dimen.dp_70);

            // 1. MATCH_PARENT ???????????????????????????Item?????????;
            // 2. ???????????????????????????80;
            // 3. WRAP_CONTENT???????????????????????????;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

//            SwipeMenuItem addItem = new SwipeMenuItem(getContext()).setBackground(R.drawable.selector_red)
//                    .setImage(R.drawable.ic_action_add)
//                    .setWidth(width)
//                    .setHeight(height);
//            swipeLeftMenu.addMenuItem(addItem); // ????????????????????????

            SwipeMenuItem closeItem = new SwipeMenuItem(getContext()).setBackground(R.drawable.selector_red)
                    .setImage(R.drawable.ic_close_55dp)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(closeItem); // ????????????????????????
        }
    };
}
