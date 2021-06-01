package com.project.specializedproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pedro.library.AutoPermissions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class FeedDetail extends AppCompatActivity implements FeedDetailAdapter.clickEventListener{
    final private String TAG = "FeedDetail";
    public static Context detail_Context;
    RecyclerView rList;
    FeedDetailAdapter listAdapter;
    ArrayList<ModelFeed> mFeed = new ArrayList<>();
    String[][] fArray = new String[5][6];
    int fArrayNum = 0;
    String fid;
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    private Marker currentMarker = null;

    NestedScrollView detail_scroll;
    ImageView detail_photo;
    TextView detail_title, detail_note;
    Button detail_complete;
    String title, note, photo, location, fd_item; // fd_item = 미션 가능 여부
    boolean backBtnCallback = false;

    ImageButton detail_zoomIn, detail_zoomOut;
    Button detail_courseLocation, detail_myLocation;
    boolean gpsLocation = true;

    String tmpLoc0, tmpLoc1, tmpLoc2, tmpLoc3, tmpLoc4, tmpLoc5;
    String[] location0 = new String[2];
    String[] location1 = new String[2];
    String[] location2 = new String[2];
    String[] location3 = new String[2];
    String[] location4 = new String[2];
    String[] location5 = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_detail);
        detail_Context = this;
        Intent intent = getIntent();
        fid = intent.getExtras().getString("fid");
        setView();

        searchFeed();

        rList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        listAdapter = new FeedDetailAdapter(mFeed, getApplicationContext());
        listAdapter.setClickEventListener(this); // Click Callback
        rList.setAdapter(listAdapter);

        //getMapAsync must be called on the Main thread.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.detail_map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                searchMap();
                detail_courseLocation.setBackgroundColor(Color.parseColor("#F2BA77"));
            }
        });
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        AutoPermissions.Companion.loadAllPermissions(this, 100);
    }

    private void searchMap(){
        Query searchData = FirebaseDatabase.getInstance().getReference("Feed").orderByChild("fid").equalTo(fid);
        searchData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Map map = (Map) snapshot.getValue();
                        Iterator<String> Iterator = map.keySet().iterator();
                        while (Iterator.hasNext()) {
                            String next = Iterator.next();

                            tmpLoc0 = (String) map.get("feedW_location0");
                            tmpLoc1 = (String) map.get("feedW_location1");
                            tmpLoc2 = (String) map.get("feedW_location2");
                            tmpLoc3 = (String) map.get("feedW_location3");
                            tmpLoc4 = (String) map.get("feedW_location4");
                            tmpLoc5 = (String) map.get("feedW_location5");
                        }
                    }
                    if(tmpLoc0 != null && !tmpLoc0.equals(""))
                        location0 = tmpLoc0.split("\n");
                    if(tmpLoc1 != null && !tmpLoc1.equals(""))
                        location1 = tmpLoc1.split("\n");
                    if(tmpLoc2 != null && !tmpLoc2.equals(""))
                        location2 = tmpLoc2.split("\n");
                    if(tmpLoc3 != null && !tmpLoc3.equals(""))
                        location3 = tmpLoc3.split("\n");
                    if(tmpLoc4 != null && !tmpLoc4.equals(""))
                        location4 = tmpLoc4.split("\n");
                    if(tmpLoc5 != null && !tmpLoc5.equals(""))
                        location5 = tmpLoc5.split("\n");

                    setDefaultLocation();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    // default location
    private void setDefaultLocation() {
        LatLng DEFAULT_LOCATION;
        String markerTitle;
        String markerSnippet;
        if(tmpLoc0 == null || tmpLoc0.equals("")) {
            DEFAULT_LOCATION = new LatLng(36.629, 127.456);
            markerTitle = "위치정보를 불러올 수 없습니다.";
            markerSnippet = "위치 권한과 GPS 활성 여부를 확인해 주세요.";
        }else {
            DEFAULT_LOCATION = new LatLng(Double.parseDouble(location0[0]), Double.parseDouble(location0[1]));
            markerTitle = "주요 위치";
            markerSnippet = "주변 핀의 도전과제를 찾아보세요!";
        }

        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.pin0);
        markerOptions.icon(icon);
        markerOptions.draggable(false);
        currentMarker = mMap.addMarker(markerOptions);

        missionMarker();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 17);
        mMap.moveCamera(cameraUpdate);
    }

    private void missionMarker(){   // todo 핀 이미지 변경@@
        LatLng Mission_LOCATION;
        MarkerOptions markerOptions = new MarkerOptions();
        PolylineOptions polyOptions = new PolylineOptions().clickable(true);
        if(tmpLoc0 != null && !tmpLoc0.equals(""))
            polyOptions.add(new LatLng(Double.parseDouble(location0[0]), Double.parseDouble(location0[1])));

        if(tmpLoc1 != null && !tmpLoc1.equals("")){
            Mission_LOCATION = new LatLng(Double.parseDouble(location1[0]), Double.parseDouble(location1[1]));
            polyOptions.add(new LatLng(Double.parseDouble(location1[0]), Double.parseDouble(location1[1])));
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.pin1);
            markerOptions.icon(icon);
            markerOptions.position(Mission_LOCATION);
            markerOptions.title("도전과제1");
            currentMarker = mMap.addMarker(markerOptions);
        }
        if(tmpLoc2 != null && !tmpLoc2.equals("")){
            Mission_LOCATION = new LatLng(Double.parseDouble(location2[0]), Double.parseDouble(location2[1]));
            polyOptions.add(new LatLng(Double.parseDouble(location2[0]), Double.parseDouble(location2[1])));
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.pin2);
            markerOptions.icon(icon);
            markerOptions.position(Mission_LOCATION);
            markerOptions.title("도전과제2");
            currentMarker = mMap.addMarker(markerOptions);
        }
        if(tmpLoc3 != null && !tmpLoc3.equals("")){
            Mission_LOCATION = new LatLng(Double.parseDouble(location3[0]), Double.parseDouble(location3[1]));
            polyOptions.add(new LatLng(Double.parseDouble(location3[0]), Double.parseDouble(location3[1])));
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.pin3);
            markerOptions.icon(icon);
            markerOptions.position(Mission_LOCATION);
            markerOptions.title("도전과제3");
            currentMarker = mMap.addMarker(markerOptions);
        }
        if(tmpLoc4 != null && !tmpLoc4.equals("")){
            Mission_LOCATION = new LatLng(Double.parseDouble(location4[0]), Double.parseDouble(location4[1]));
            polyOptions.add(new LatLng(Double.parseDouble(location4[0]), Double.parseDouble(location4[1])));
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.pin4);
            markerOptions.icon(icon);
            markerOptions.position(Mission_LOCATION);
            markerOptions.title("도전과제4");
            currentMarker = mMap.addMarker(markerOptions);
        }
        if(tmpLoc5 != null && !tmpLoc5.equals("")){
            Mission_LOCATION = new LatLng(Double.parseDouble(location5[0]), Double.parseDouble(location5[1]));
            polyOptions.add(new LatLng(Double.parseDouble(location5[0]), Double.parseDouble(location5[1])));
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.pin5);
            markerOptions.icon(icon);
            markerOptions.position(Mission_LOCATION);
            markerOptions.title("도전과제5");
            currentMarker = mMap.addMarker(markerOptions);
        }
        mMap.addPolyline(polyOptions.color(Color.parseColor("#FF9345")));
    }

    // my location
    private void startLocationService() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            int chk1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int chk2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            Location location = null;
            if (chk1 == PackageManager.PERMISSION_GRANTED && chk2 == PackageManager.PERMISSION_GRANTED) {
                location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else {
                return;
            }

            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String msg = "최근 위치 ->  Latitue : " + latitude + "\nLongitude : " + longitude;
                showCurrentLocation(latitude, longitude);
                Log.e(TAG, msg);
            }

            GPSListener gpsListener = new GPSListener();
            long minTime = 10000;
            float minDistance = 0;

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);


        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }


    class GPSListener implements LocationListener { // 실행 안함
        @Override
        public void onLocationChanged(Location location) {
            if(gpsLocation == false) {
                Double latitude = location.getLatitude();
                Double longitude = location.getLongitude();

                String message = "내 위치 -> Latitude : " + latitude + "\nLongitude:" + longitude;
                Log.e("Map", message);

                showCurrentLocation(latitude, longitude);
            }
            gpsLocation = true;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }

    private void showCurrentLocation(Double latitude, Double longitude) {
        LatLng curPoint = new LatLng(latitude, longitude);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 17));

        Toast.makeText(getApplicationContext(), "실행", Toast.LENGTH_LONG).show();
        String markerTitle = "내위치";
        String markerSnippet = "위치정보가 확인되었습니다.";

        if (currentMarker != null) currentMarker.remove();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(curPoint);
        markerOptions.title(markerTitle);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.mylocaition);
        markerOptions.icon(icon);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(curPoint, 17);
        mMap.moveCamera(cameraUpdate);
    }

    private void setView(){
        detail_scroll = findViewById(R.id.detail_scroll);
        rList = findViewById(R.id.recyclerDetail);
        detail_photo = findViewById(R.id.detail_photo);
        detail_title = findViewById(R.id.detail_title);
        detail_note = findViewById(R.id.detail_note);
        detail_complete = findViewById(R.id.detail_complete);
        detail_complete.setOnClickListener(this::onClick);

        detail_zoomIn = findViewById(R.id.detail_zoomIn);
        detail_zoomOut = findViewById(R.id.detail_zoomOut);
        detail_courseLocation = findViewById(R.id.detail_courseLocation);
        detail_myLocation = findViewById(R.id.detail_myLocation);
        detail_zoomIn.setOnClickListener(this::onClick);
        detail_zoomOut.setOnClickListener(this::onClick);
        detail_courseLocation.setOnClickListener(this::onClick);
        detail_myLocation.setOnClickListener(this::onClick);
    }

    private void searchFeed() {
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

                            if(fArrayNum >= 5){
                                break;
                            }else {
                                // Main info
                                Glide.with(getApplicationContext()).load((String) map.get("feedW_photo0")).into(detail_photo);
                                detail_title.setText((String) map.get("feedW_title0"));
                                detail_note.setText((String) map.get("feedW_note0"));

                                if ((String) map.get(title) != null) { // index = title
                                    fArray[fArrayNum][0] = (String) map.get(title);
                                    fArray[fArrayNum][1] = (String) map.get(note);
                                    fArray[fArrayNum][2] = (String) map.get(photo);
                                    fArray[fArrayNum][3] = (String) map.get(location);
                                    fArray[fArrayNum][4] = (String) map.get("fid");
                                    fArray[fArrayNum][5] = (String) map.get(fd_item); // 미션 가능 여부

                                    fArrayNum++;
                                } else {
                                    break;
                                }
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
        mf.setFd_item(fArray[i][5]);
        mFeed.add(mf);
    }

    public void onClick(View view){
        if( view.getId() == R.id.detail_complete ){
            FirebaseAuth fAuth = FirebaseAuth.getInstance();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("Mission").child(fid).child(fAuth.getCurrentUser().getUid()).child("clear");
            ref.setValue(1);
            finish();
            Toast.makeText(this, "도전과제를 모두 완료했습니다!",Toast.LENGTH_SHORT).show();
        }else if( view.getId() == R.id.detail_courseLocation ){
            setDefaultLocation();
            detail_courseLocation.setBackgroundColor(Color.parseColor("#F2BA77"));
            detail_myLocation.setBackgroundColor(Color.parseColor("#00ffffff"));
        }else if( view.getId() == R.id.detail_myLocation ){
            startLocationService();
            detail_myLocation.setBackgroundColor(Color.parseColor("#F2BA77"));
            detail_courseLocation.setBackgroundColor(Color.parseColor("#00ffffff"));
        }else if( view.getId() == R.id.detail_zoomIn ){
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
        }else if( view.getId() == R.id.detail_zoomOut ){
            mMap.animateCamera(CameraUpdateFactory.zoomOut());
        }
    }

    private void setString(int i){
        switch (i){
            case 0:
                title = "feedW_title1";
                note = "feedW_note1";
                photo = "feedW_photo1";
                location = "feedW_location1";
                fd_item = "fd_item11";
                break;
            case 1:
                title = "feedW_title2";
                note = "feedW_note2";
                photo = "feedW_photo2";
                location = "feedW_location2";
                fd_item = "fd_item21";
                break;
            case 2:
                title = "feedW_title3";
                note = "feedW_note3";
                photo = "feedW_photo3";
                location = "feedW_location3";
                fd_item = "fd_item31";
                break;
            case 3:
                title = "feedW_title4";
                note = "feedW_note4";
                photo = "feedW_photo4";
                location = "feedW_location4";
                fd_item = "fd_item41";
                break;
            case 4:
                title = "feedW_title5";
                note = "feedW_note5";
                photo = "feedW_photo5";
                location = "feedW_location5";
                fd_item = "fd_item51";
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
}