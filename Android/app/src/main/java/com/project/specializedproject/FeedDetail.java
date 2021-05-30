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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pedro.library.AutoPermissions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class FeedDetail extends AppCompatActivity implements FeedDetailAdapter.clickEventListener{
    // OnMapReadyCallback, GoogleMap.OnMapClickListener,
    final private String TAG = "FeedDetail";
    RecyclerView rList;
    FeedDetailAdapter listAdapter;
    ArrayList<ModelFeed> mFeed = new ArrayList<>();
    String[][] fArray = new String[100][5];
    int fArrayNum = 0;

    SupportMapFragment mapFragment;
    GoogleMap mMap;
    private Marker currentMarker = null;

    ImageView detail_photo;
    TextView detail_title, detail_note;
    Button detail_complete;
    String title, note, photo, location;
    boolean backBtnCallback = false;

    ImageButton detail_zoomIn, detail_zoomOut;
    Button detail_courseLocation, detail_myLocation;
    boolean gpsLocation = true;

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

        //getMapAsync must be called on the Main thread.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.detail_map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                setDefaultLocation();
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

    // default location
    private void setDefaultLocation() {
        LatLng DEFAULT_LOCATION = new LatLng(36.629, 127.456);
        String markerTitle = "위치정보를 불러올 수 없습니다.";
        String markerSnippet = "위치 권한과 GPS 활성 여부를 확인해 주세요.";

        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);
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

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));

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

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(curPoint, 15);
        mMap.moveCamera(cameraUpdate);
    }

    private void setView(){
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
        }else if( view.getId() == R.id.detail_courseLocation ){
            setDefaultLocation();
            detail_courseLocation.setBackgroundColor(Color.parseColor("#F2BA77"));
            detail_myLocation.setBackgroundColor(Color.parseColor("#00ffffff"));
        }else if( view.getId() == R.id.detail_myLocation ){
            startLocationService();
            detail_myLocation.setBackgroundColor(Color.parseColor("#F2BA77"));
            detail_courseLocation.setBackgroundColor(Color.parseColor("#00ffffff"));
        }else if( view.getId() == R.id.detail_zoomIn ){

        }else if( view.getId() == R.id.detail_zoomOut ){

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

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        LatLng location0 = new LatLng(36.7423, 127.4546);
//
//        MarkerOptions makerOptions = new MarkerOptions();
//        makerOptions.position(location0).title("시작 지점");
//        mMap.addMarker(makerOptions);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location0, 15));
//        mMap.setOnMapClickListener(this::onMapClick);
//
//    }
//
//    @Override
//    public void onMapClick(LatLng latLng) {
//        Log.e(TAG, "ㅎㅇ");
//
//    }
}