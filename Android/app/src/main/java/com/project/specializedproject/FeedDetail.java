package com.project.specializedproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class FeedDetail extends AppCompatActivity implements FeedDetailAdapter.clickEventListener, OnMapReadyCallback {
    final private String TAG = "FeedDetail";
    RecyclerView rList;
    FeedDetailAdapter listAdapter;
    ArrayList<ModelFeed> mFeed = new ArrayList<>();
    String[][] fArray = new String[100][5];
    int fArrayNum = 0;

    GoogleMap mMap;

    ImageView detail_photo;
    TextView detail_title, detail_note;
    Button detail_complete;
    String title, note, photo, location;
    boolean backBtnCallback = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_detail);
        setView();

        searchFeed();

        rList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        listAdapter = new FeedDetailAdapter(mFeed, getApplicationContext());
        listAdapter.setClickEventListener(this); // Click Callback
        rList.setAdapter(listAdapter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.detail_map);
        mapFragment.getMapAsync(this); //getMapAsync must be called on the Main thread.
    }

    private void setView(){
        rList = findViewById(R.id.recyclerDetail);
        detail_photo = findViewById(R.id.detail_photo);
        detail_title = findViewById(R.id.detail_title);
        detail_note = findViewById(R.id.detail_note);
        detail_complete = findViewById(R.id.detail_complete);
        detail_complete.setOnClickListener(this::onClick);
    }

    private void searchFeed() {
        Intent intent = getIntent();
        String fid = intent.getExtras().getString("fid");

        Query searchData = FirebaseDatabase.getInstance().getReference("Feed").orderByChild("fid").equalTo(fid);
        searchData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mFeed.clear();
                fArrayNum = 0;

                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Map map = (Map) snapshot.getValue();
                        Iterator<String> Iterator = map.keySet().iterator();
                        while (Iterator.hasNext()) {
                            String next = Iterator.next();
                            setString(fArrayNum);

                            // Main info
                            Glide.with(getApplicationContext()).load((String) map.get("feedW_photo0")).into(detail_photo);
                            detail_title.setText((String) map.get("feedW_title0"));
                            detail_note.setText((String) map.get("feedW_note0"));

                            if((String) map.get(title) != null) { // index = title
                                fArray[fArrayNum][0] = (String) map.get(title);
                                fArray[fArrayNum][1] = (String) map.get(note);
                                fArray[fArrayNum][2] = (String) map.get(photo);
                                fArray[fArrayNum][3] = (String) map.get(location);
                                fArray[fArrayNum][4] = (String) map.get("fid");

                                fArrayNum++;
                            }else{
                                break;
                            }
                        }
                    }

                    for (int i = 0; i < fArrayNum; i++) {
                        addItem(i);
                    }

                    listAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void addItem(int i) {
        ModelFeed mf = new ModelFeed();
        mf.setFeedW_title(fArray[i][0]);
        mf.setFeedW_note(fArray[i][1]);
        mf.setFeedW_photo(fArray[i][2]);
        mf.setFeedW_location(fArray[i][3]);
        mf.setFid(fArray[i][4]);
        mFeed.add(mf);
    }

    public void onClick(View view){
        if( view.getId() == R.id.detail_complete ){
            Toast.makeText(this, "미구현 기능입니다.",Toast.LENGTH_SHORT).show();
        }
    }

    private void setString(int i){
        switch (i){
            case 0:
                title = "feedW_title1";
                note = "feedW_note1";
                photo = "feedW_photo1";
                location = "feedW_location1";
                break;
            case 1:
                title = "feedW_title2";
                note = "feedW_note2";
                photo = "feedW_photo2";
                location = "feedW_location2";
                break;
            case 2:
                title = "feedW_title3";
                note = "feedW_note3";
                photo = "feedW_photo3";
                location = "feedW_location3";
                break;
            case 3:
                title = "feedW_title4";
                note = "feedW_note4";
                photo = "feedW_photo4";
                location = "feedW_location4";
                break;
            case 4:
                title = "feedW_title5";
                note = "feedW_note5";
                photo = "feedW_photo5";
                location = "feedW_location5";
                break;
        }
    }

    @Override
    public void backBtnCallback(boolean backState) {
        backBtnCallback = backState;
    }

    @Override
    public void onBackPressed() { // 뒤로가기 벤 ( 미구현 )
        if( backBtnCallback == true) {
//            super.onBackPressed();
        }else{
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng seoul = new LatLng(37.52487, 126.92723);

        MarkerOptions makerOptions = new MarkerOptions();
        makerOptions.position(seoul).title("원하는 위치(위도, 경도)에 마커를 표시했습니다.");
        mMap.addMarker(makerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
    }
}