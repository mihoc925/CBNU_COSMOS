package com.project.specializedproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pedro.library.AutoPermissions;

import java.io.IOException;
import java.util.List;

public class FeedLocation extends AppCompatActivity {
    final private String TAG = "FeedLocation";
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    private Marker currentMarker = null;
    Button writeLocation_Btn;
    ImageButton writeLocation_myLocation;

    Double select_latitude, select_longitude;
    int content;
    String state;
    boolean gpsLocation = true;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_write_location);
        Intent intent = getIntent();
        content = intent.getExtras().getInt("content");
        state = intent.getExtras().getString("state");
        writeLocation_Btn = findViewById(R.id.writeLocation_Btn);
        writeLocation_myLocation = findViewById(R.id.writeLocation_myLocation);
        writeLocation_Btn.setOnClickListener(this::onClick);
        writeLocation_myLocation.setOnClickListener(this::onClick);
        fAuth = FirebaseAuth.getInstance();

        //getMapAsync must be called on the Main thread.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.writeLocation_Map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                setDefaultLocation();
                startLocationService();

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
                    @Override
                    public void onMapClick(LatLng latLng) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        select_latitude = latLng.latitude; // 위도
                        select_longitude = latLng.longitude; // 경도
                        markerOptions.position(new LatLng(select_latitude, select_longitude));

                        markerOptions.title("위치 설정");
                        markerOptions.snippet("이 위치가 맞나요?");
                        markerOptions.draggable(true);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                        googleMap.clear();
                        googleMap.addMarker(markerOptions);

                        if(state.equals("write"))
                            setFeedLocation(); // 위치 FeedWrite 페이지 저장
                        if(state.equals("list")) {
                            setListLocation(); // 검색창 저장
                        }
                    }
                });
            }
        });
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        AutoPermissions.Companion.loadAllPermissions(this, 100);
    }
    
    private void setListLocation(){
        final Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address> list = null;
        try {
            //미리 구해놓은 위도값 mLatitude;
            //미리 구해놓은 경도값 mLongitude;

            list = geocoder.getFromLocation(
                    select_latitude, // 위도
                    select_longitude, // 경도
                    10); // 얻어올 값의 개수
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("UserData").child(fAuth.getCurrentUser().getUid()).child("uLocation");
            if (list.size()==0) {
                ref.setValue("주소 정보가 없습니다");
            } else {
                ref.setValue(list.get(0).getAddressLine(0));
            }
        }
    }

    private void setFeedLocation(){
        if(content == 0)
            ((FeedWrite) FeedWrite.feedW_Context).feedW_location0.setText(select_latitude +"\n"+ select_longitude);
        else if (content == 1)
            ((FeedWrite) FeedWrite.feedW_Context).feedW_location1.setText(select_latitude +"\n"+ select_longitude);
        else if (content == 2)
            ((FeedWrite) FeedWrite.feedW_Context).feedW_location2.setText(select_latitude +"\n"+ select_longitude);
        else if (content == 3)
            ((FeedWrite) FeedWrite.feedW_Context).feedW_location3.setText(select_latitude +"\n"+ select_longitude);
        else if (content == 4)
            ((FeedWrite) FeedWrite.feedW_Context).feedW_location4.setText(select_latitude +"\n"+ select_longitude);
        else if (content == 5)
            ((FeedWrite) FeedWrite.feedW_Context).feedW_location5.setText(select_latitude +"\n"+ select_longitude);
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

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 17);
        mMap.moveCamera(cameraUpdate);
    }

    // my location
    private void startLocationService() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            int chk1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
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
//                String msg = "최근 위치 ->  Latitue : " + latitude + "\nLongitude : " + longitude;
                showCurrentLocation(latitude, longitude);
//                Log.e(TAG, msg);
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

    class GPSListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if(gpsLocation == false) {  // 임시 실행 안함
                Double latitude = location.getLatitude();
                Double longitude = location.getLongitude();

//                String message = "내 위치 -> Latitude : " + latitude + "\nLongitude:" + longitude;
//                Log.e("Map", message);

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

//        Toast.makeText(getApplicationContext(), "실행", Toast.LENGTH_LONG).show();
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

        // 현재 위치 자동 저장
        select_latitude = latitude; // 위도
        select_longitude = longitude; // 경도
        if(state.equals("write"))
            setFeedLocation(); // 위치 FeedWrite 페이지 저장
        if(state.equals("list"))
            setListLocation();
    }

    public void onClick(View view){
        if(view.getId() == R.id.writeLocation_myLocation){
            startLocationService();
        }else if(view.getId() == R.id.writeLocation_Btn){
            finish();
        }
    }
}