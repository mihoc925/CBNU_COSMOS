package com.project.specializedproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FeedWrite extends AppCompatActivity {
    private String TAG = "FeedWrite";

    LinearLayout feedW_layout0, feedW_layout1, feedW_layout2, feedW_layout3;
    TextView feedW_title0, feedW_title1, feedW_title2, feedW_title3;
    TextView feedW_note0, feedW_note1, feedW_note2, feedW_note3;
    ImageButton feedW_photo0, feedW_photo1, feedW_photo2, feedW_photo3;
    Button feedW_location0, feedW_location1, feedW_location2, feedW_location3;

    // 반복x
    TextView feedW_distance, feedW_time;
    ImageButton feedW_plusBtn, feedW_minusBtn;
    Button feedW_submit;

    int stateNum = 0;
    int fid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_write);
        setView();

    }

    public void onClick(View view){
        if(view.getId() == R.id.feedW_plusBtn){
            nogada("plus");
        }else if(view.getId() == R.id.feedW_minusBtn){
            nogada("minus");
        }else if(view.getId() == R.id.feedW_submit){
            createFid();
        }else if(view.getId() == R.id. feedW_photo0){
            photoNum = 0;
            gallery();
        }
    }

    private void createFid(){
        Query searchData = FirebaseDatabase.getInstance().getReference("Feed").orderByChild("fid").limitToLast(1);
        searchData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        fid = Integer.parseInt(snapshot.getKey()) + 1;
                    }
                }
                upload_Image();
//                submit();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void gallery(){
        Intent intent= new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 10:
                if(resultCode==RESULT_OK){
                    //선택한 이미지의 경로 얻어오기
                    imgUri= data.getData();
                    if(photoNum == 0)
                        Glide.with(this).load(imgUri).into(feedW_photo0);
                }
                break;
        }
    }

    //Todo :: 이름 바꾸기 & 자리 옮기기
    Uri imgUri;
    int photoNum;
    StorageTask uploadTask;
    String photo0;

    private void upload_Image() {
        // 파일 이름, 경로
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddhhmmss");
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance(); // 초기화 필수
        String filename = mAuth.getCurrentUser().getUid()
                + "_" + sdf.format(new Date()) + ".png";
        StorageReference imgRef= firebaseStorage.getReference("Feed/"+filename);

        // using Task.. 느림
        uploadTask = imgRef.putFile(imgUri);
        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imgRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {

                    Uri downloadUrl = task.getResult();
                    photo0 = downloadUrl.toString();

                    submit_Data();
                }
            }
        });
    }

    private void submit_Data(){
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        int layoutNum = 0;

        if(feedW_layout1.getVisibility() == View.VISIBLE)
            layoutNum = 1;
        if(feedW_layout2.getVisibility() == View.VISIBLE)
            layoutNum = 2;
        if (feedW_layout3.getVisibility() == View.VISIBLE)
            layoutNum = 3;

        // TODO Validation 추가
        // 이미지 업로드 위치 잘 보고 결정

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Feed").child(String.valueOf(fid));
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("fid", fid);
        hashMap.put("uid", fAuth.getCurrentUser().getUid());
        hashMap.put("feedW_title0", feedW_title0.getText().toString());
        hashMap.put("feedW_note0", feedW_note0.getText().toString());
        hashMap.put("feedW_photo0", photo0);
        hashMap.put("feedW_location0", feedW_location0.getText().toString());
        hashMap.put("feedW_distance", feedW_distance.getText().toString());
        hashMap.put("feedW_time", feedW_time.getText().toString());

        if(layoutNum == 1){
            hashMap.put("feedW_title1", feedW_title1.getText().toString());
            hashMap.put("feedW_note1", feedW_note1.getText().toString());
            hashMap.put("feedW_photo1", "Test photo");
            hashMap.put("feedW_location1", feedW_location1.getText().toString());
            if(layoutNum == 2){
                hashMap.put("feedW_title2", feedW_title2.getText().toString());
                hashMap.put("feedW_note2", feedW_note2.getText().toString());
                hashMap.put("feedW_photo2", "Test photo");
                hashMap.put("feedW_location2", feedW_location2.getText().toString());
                if(layoutNum == 3){
                    hashMap.put("feedW_title3", feedW_title3.getText().toString());
                    hashMap.put("feedW_note3", feedW_note3.getText().toString());
                    hashMap.put("feedW_photo3", "Test photo");
                    hashMap.put("feedW_location3", feedW_location3.getText().toString());
                }
            }
        }
        ref.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // todo 다이얼로그
                    Log.e(TAG, "true!");
                    finish();
                }
                if (task.isCanceled()) {
                    // todo 다이얼로그
                    Log.e(TAG, "false!");
                }
            }
        });
    }

    private void nogada(String dif){
        switch (stateNum){
            case 0:
                if(dif.equals("plus")){
                    feedW_layout1.setVisibility(View.VISIBLE);
                    stateNum++;
                }else{
                    Toast.makeText(getApplicationContext(), "최소 1개 이상의 코스 등록이 가능합니다.",Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                if(dif.equals("plus")){
                    feedW_layout2.setVisibility(View.VISIBLE);
                    stateNum++;
                }else{
                    feedW_layout1.setVisibility(View.GONE);
                    stateNum--;
                }
                break;
            case 2:
                if(dif.equals("plus")){
                    feedW_layout3.setVisibility(View.VISIBLE);
                    stateNum++;
                }else{
                    feedW_layout2.setVisibility(View.GONE);
                    stateNum--;
                }
                break;
            case 3:
                if(dif.equals("plus")){
                    Toast.makeText(getApplicationContext(), "최대 "+stateNum+"개의 코스 등록이 가능합니다.",Toast.LENGTH_SHORT).show();
                }else{
                    feedW_layout3.setVisibility(View.GONE);
                    stateNum--;
                }
                break;
            default:
                Toast.makeText(getApplicationContext(), "최소 1개, 최대 5개 코스 등록이 가능합니다.",Toast.LENGTH_SHORT).show();
        }
    }

    private void setView(){
        feedW_layout0 = findViewById(R.id.feedW_layout0);
        feedW_title0 = findViewById(R.id.feedW_title0);
        feedW_note0 = findViewById(R.id.feedW_note0);
        feedW_photo0 = findViewById(R.id.feedW_photo0);
        feedW_location0 = findViewById(R.id.feedW_location0);
        feedW_photo0.setOnClickListener(this::onClick);
        feedW_location0.setOnClickListener(this::onClick);

        feedW_layout1 = findViewById(R.id.feedW_layout1);
        feedW_title1 = findViewById(R.id.feedW_title1);
        feedW_note1 = findViewById(R.id.feedW_note1);
        feedW_photo1 = findViewById(R.id.feedW_photo1);
        feedW_location1 = findViewById(R.id.feedW_location1);
        feedW_photo1.setOnClickListener(this::onClick);
        feedW_location1.setOnClickListener(this::onClick);

        feedW_layout2 = findViewById(R.id.feedW_layout2);
        feedW_title2 = findViewById(R.id.feedW_title2);
        feedW_note2 = findViewById(R.id.feedW_note2);
        feedW_photo2 = findViewById(R.id.feedW_photo2);
        feedW_location2 = findViewById(R.id.feedW_location2);
        feedW_photo2.setOnClickListener(this::onClick);
        feedW_location2.setOnClickListener(this::onClick);

        feedW_layout3 = findViewById(R.id.feedW_layout3);
        feedW_title3 = findViewById(R.id.feedW_title3);
        feedW_note3 = findViewById(R.id.feedW_note3);
        feedW_photo3 = findViewById(R.id.feedW_photo3);
        feedW_location3 = findViewById(R.id.feedW_location3);
        feedW_photo3.setOnClickListener(this::onClick);
        feedW_location3.setOnClickListener(this::onClick);


        feedW_distance = findViewById(R.id.feedW_distance);
        feedW_time = findViewById(R.id.feedW_time);

        feedW_plusBtn = findViewById(R.id.feedW_plusBtn);
        feedW_minusBtn = findViewById(R.id.feedW_minusBtn);
        feedW_plusBtn.setOnClickListener(this::onClick);
        feedW_minusBtn.setOnClickListener(this::onClick);

        feedW_submit = findViewById(R.id.feedW_submit);
        feedW_submit.setOnClickListener(this::onClick);
    }
}