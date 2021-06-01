package com.project.specializedproject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragHome extends Fragment {
    private String TAG = "FragHome";
    Context mContext;

    FirebaseAuth fAuth;
    private ArrayList<ModelFeed> mFeed = new ArrayList<>();
    FragHomeAdapter homeAdapter;
    RecyclerView rList;

    int fArrayNum = 0;
    int proceedMission[] = new int[2]; // 비동기 처리
    ArrayList<String> clearMission = new ArrayList<>();

    LinearLayout home_layout;
    ImageView home_defaultImg;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_home, container, false);
        mContext = view.getContext();
        fAuth = FirebaseAuth.getInstance();
        setView(view);
        searchMission();

        home_defaultImg.setVisibility(View.GONE);
        home_layout.setVisibility(View.VISIBLE);

        rList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        homeAdapter = new FragHomeAdapter(mFeed, getContext());
        rList.setAdapter(homeAdapter);
        return view;
    }

    private void searchMission() {
        proceedMission[0] = 0;
        proceedMission[1] = 0;
        Query searchData = FirebaseDatabase.getInstance().getReference("Mission")
                .orderByChild(fAuth.getCurrentUser().getUid());
        searchData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mFeed.clear();
                fArrayNum = 0;

                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        proceedMission[0]++;
                        String fid = String.valueOf(snapshot.getKey());
                        Log.e(TAG, "fid="+fid);
                        searchClearMission(fid);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void searchClearMission(String fid) {
        DatabaseReference searchData = FirebaseDatabase.getInstance().getReference("Mission").child(fid)
                .child(fAuth.getCurrentUser().getUid()).child("clear");
        searchData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                proceedMission[1]++;
                if(dataSnapshot.getValue() != null) {
                    if (Integer.parseInt(String.valueOf(dataSnapshot.getValue())) == 0) {
                        clearMission.add(fid);
                    }
                }
                if(proceedMission[0] == proceedMission[1]) {
                    for (int i = 0; i < clearMission.size(); i++)
                        addItem(i);
                    homeAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void addItem(int i) {
        ModelFeed mf = new ModelFeed();
        mf.setFid(clearMission.get(i));
        mFeed.add(mf);
    }

    private void setView(View view){
        home_layout = view.findViewById(R.id.home_layout);
        home_defaultImg = view.findViewById(R.id.home_defaultImg);
        rList = view.findViewById(R.id.home_recycler);

    }

    public void onCliCk(View view){
//        if(view.getId() == R.id.home_test){
//        }
    }

}
