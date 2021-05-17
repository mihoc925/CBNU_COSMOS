package com.project.specializedproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class FeedWrite extends AppCompatActivity {
    private String TAG = "FeedWrite";
    ProgressDialog pd;

    LinearLayout feedW_layout0, feedW_layout1, feedW_layout2, feedW_layout3;
    TextView feedW_title0, feedW_title1, feedW_title2, feedW_title3;
    TextView feedW_note0, feedW_note1, feedW_note2, feedW_note3;
    ImageButton feedW_photo0, feedW_photo1, feedW_photo2, feedW_photo3;
    Button feedW_location0, feedW_location1, feedW_location2, feedW_location3;

    // 반복x
    TextView feedW_distance, feedW_time;
    ImageButton feedW_plusBtn, feedW_minusBtn;
    Button feedW_submit;

    int stateNum = 0; // 레이아웃 추가, 삭제
    int fid = 0; // auto_increment

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance(); // 초기화 필수
    int layoutNum = 0; // 활성화 레이아웃
    Uri photoUri[] = new Uri[5];
    String photo[] = new String[5];
    int photoNum; // 버튼) 해당 이미지 번호
    int proceedMaxNum[] = new int[2]; // Task 검증 :: 0= for, 1= Task

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
            // TODO (진행 순서) 1.validation >> 2.search_fid >> 3.upload_Image >> 4.upload_Proceed >> 5.submit
            form_validation();
        }else if(view.getId() == R.id. feedW_photo0){
            photoNum = 0;
            gallery();
        }else if(view.getId() == R.id. feedW_photo1){
            photoNum = 1;
            gallery();
        }else if(view.getId() == R.id. feedW_photo2){
            photoNum = 2;
            gallery();
        }else if(view.getId() == R.id. feedW_photo3){
            photoNum = 3;
            gallery();
        }
    }

    private void form_validation(){
        if(feedW_layout1.getVisibility() == View.VISIBLE)
            layoutNum = 1;
        if(feedW_layout2.getVisibility() == View.VISIBLE)
            layoutNum = 2;
        if (feedW_layout3.getVisibility() == View.VISIBLE)
            layoutNum = 3;

        boolean isValidation = true;

        if (TextUtils.isEmpty(feedW_title0.getText().toString())) {
            feedW_title0.setError("제목을 입력해 주세요.");
            isValidation = false;
        }
        if (TextUtils.isEmpty(feedW_note0.getText().toString())) {
            feedW_note0.setError("내용을 입력해 주세요.");
            isValidation = false;
        }
        if (TextUtils.isEmpty(feedW_distance.getText().toString())) {
            feedW_distance.setError("예상 거리를 입력해 주세요.");
            isValidation = false;
        }else if((!Pattern.matches("^[0-9]*$", feedW_distance.getText().toString()))){
            feedW_distance.setError("숫자만 입력해 주세요.\n예) 입력: 1500 = 출력: 1.5km");
            isValidation = false;
        }
        if (TextUtils.isEmpty(feedW_time.getText().toString())) {
            feedW_time.setError("예상 시간을 입력해 주세요.");
            isValidation = false;
        }else if((!Pattern.matches("^[0-9]*$", feedW_time.getText().toString()))){
            feedW_time.setError("숫자만 입력해 주세요.\n예) 입력: 120 = 출력: 2시간");
            isValidation = false;
        }

        if(isValidation == true){
            createFid();
        }
    }

    private void createFid(){
        pd = new ProgressDialog(FeedWrite.this);
        pd.setMessage("잠시만 기다려주세요");
        pd.show();

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
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

//        // 파일 이름
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddhhmmss");
//        String filename = mAuth.getCurrentUser().getUid() +"_" + fid + photoNum   // 프로필 업로드에 사용
//                + "_" + sdf.format(new Date()) + ".png";
    private void upload_Image() {
        proceedMaxNum[0] = 0; // Task 검증 초기화
        proceedMaxNum[1] = 0;

        for(int i=0; i<=layoutNum; i++){
            if(photoUri[i] != null){ // NullPointException
                proceedMaxNum[0]++;
                upload_proceed(i);

            }else{ // defaultFeed Image
                photo[i] = "https://firebasestorage.googleapis.com/v0/b/specializedproject-a3dd0.appspot.com/o/defaultFeed.png?alt=media&token=5a4254d6-949f-469e-9fa0-e89c38bf9042";
            }
        }
        if(proceedMaxNum[0] == 0)
            submit_Data();
    }

    private void upload_proceed(int i){
        String filename = fid + "_"  + i + ".png";
        StorageReference photoRef= firebaseStorage.getReference("Feed/"+filename);

        StorageTask uploadTask = photoRef.putFile(photoUri[i]);
        uploadTask.continueWithTask(new Continuation() { // using Task.. 느림
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return photoRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUrl = task.getResult();
                    photo[i] = downloadUrl.toString();
                    Log.e(TAG, "photo[ "+ i +" ] = "+photo[i]);

                    proceedMaxNum[1]++;

                    Log.e(TAG, "proceedMaxNum = "+proceedMaxNum[0]+" / "+proceedMaxNum[1]);
                    if(proceedMaxNum[0] == proceedMaxNum[1])
                        submit_Data();
                }
            }
        });
    }

    private void submit_Data(){
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Feed").child(String.valueOf(fid));
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy/MM/dd_hh:mm:ss");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("fid", fid);
        hashMap.put("uid", fAuth.getCurrentUser().getUid());
        hashMap.put("c_date", sdf.format(new Date()));
        hashMap.put("u_date", sdf.format(new Date()));
        hashMap.put("feedW_title0", feedW_title0.getText().toString());
        hashMap.put("feedW_note0", feedW_note0.getText().toString());
        hashMap.put("feedW_photo0", photo[0]);
        hashMap.put("feedW_location0", feedW_location0.getText().toString());
        hashMap.put("feedW_distance", feedW_distance.getText().toString());
        hashMap.put("feedW_time", feedW_time.getText().toString());

        Log.e(TAG, "------------------------layoutNum = "+layoutNum);
        Log.e(TAG, "photo 0 = "+photo[0]);
        Log.e(TAG, "photo 1 = "+photo[1]);
        Log.e(TAG, "photo 2 = "+photo[2]);
        Log.e(TAG, "photo 3 = "+photo[3]);

        if(layoutNum >= 1){
            hashMap.put("feedW_title1", feedW_title1.getText().toString());
            hashMap.put("feedW_note1", feedW_note1.getText().toString());
            hashMap.put("feedW_photo1", photo[1]);
            hashMap.put("feedW_location1", feedW_location1.getText().toString());
            if(layoutNum >= 2){
                hashMap.put("feedW_title2", feedW_title2.getText().toString());
                hashMap.put("feedW_note2", feedW_note2.getText().toString());
                hashMap.put("feedW_photo2", photo[2]);
                hashMap.put("feedW_location2", feedW_location2.getText().toString());
                if(layoutNum >= 3){
                    hashMap.put("feedW_title3", feedW_title3.getText().toString());
                    hashMap.put("feedW_note3", feedW_note3.getText().toString());
                    hashMap.put("feedW_photo3", photo[3]);
                    hashMap.put("feedW_location3", feedW_location3.getText().toString());
                }
            }
        }
        pd.dismiss();

        ref.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    finish();
                    Toast.makeText(getApplicationContext(), "데이터 코스를 등록했습니다!",Toast.LENGTH_SHORT).show();
                }
                if (task.isCanceled()) {
                    Toast.makeText(getApplicationContext(), "등록에 실패했습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 10:
                if(resultCode==RESULT_OK){
                    photoUri[photoNum] = data.getData(); //선택한 이미지의 경로 얻어오기

                    if(photoNum == 0)
                        Glide.with(this).load(photoUri[photoNum]).into(feedW_photo0);
                    if(photoNum == 1)
                        Glide.with(this).load(photoUri[photoNum]).into(feedW_photo1);
                    if(photoNum == 2)
                        Glide.with(this).load(photoUri[photoNum]).into(feedW_photo2);
                    if(photoNum == 3)
                        Glide.with(this).load(photoUri[photoNum]).into(feedW_photo3);
                }
                break;
        }
    }

    private void gallery(){
        Intent intent= new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,10);
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