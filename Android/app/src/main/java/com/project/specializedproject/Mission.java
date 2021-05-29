package com.project.specializedproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Mission extends AppCompatActivity {
    private static final String TAG = "Mission";
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    TextView mission_content, mission_title;
    ImageView mission_image, mission_state;
    RatingBar mission_ratingBar;
    TextView mission_point;
    ImageView mission_check1, mission_check2, mission_check3, mission_check4, mission_check5;
    TextView mission_content1, mission_content2, mission_content3, mission_content4, mission_content5;
    Button mission_clear;

    String fid;
    int content, score;
    int[] recognitionCheck = new int[3];
    String feedW_photo, feedW_title;
    int uploadCallback = 0;

    ArrayList<Integer> content1 = new ArrayList<>();
    ArrayList<Integer> content2 = new ArrayList<>();
    ArrayList<Integer> content3 = new ArrayList<>();
    ArrayList<Integer> content4 = new ArrayList<>();
    ArrayList<Integer> content5 = new ArrayList<>();
    int[][] recognition = new int[5][3];
    boolean updateState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);
        setView();
        
        for(int i=0; i<recognitionCheck.length; i++) // 초기화
            recognitionCheck[i] = 0;

        Intent intent = getIntent();
        fid = intent.getExtras().getString("fid");
        content = intent.getExtras().getInt("content");
        score = intent.getExtras().getInt("score");
        recognitionCheck = intent.getExtras().getIntArray("recognitionCheck");

        searchData();
        uploadData();
        setBinding();
    }

    private void uploadData(){
        for(int i=0; i<recognitionCheck.length; i++) { // score >= 0, len = 3
            String uid = fAuth.getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("Mission").child(fid).child(uid).child("content"+content).child(""+i);

            if (recognitionCheck[i] == 1)
                ref.setValue(1);
            else
                ref.setValue(0);

            uploadCallback++;
            if(uploadCallback >= recognitionCheck.length - 1)
                loadMissionData();
        }
    }

    private void loadMissionData(){
        String uid = fAuth.getCurrentUser().getUid();
        DatabaseReference searchData = FirebaseDatabase.getInstance().getReference("Mission").child(fid).child(uid);
        searchData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){

                    ModelMission mission = dataSnapshot.getValue(ModelMission.class);
                    content1 = mission.getContent1();
                    content2 = mission.getContent2();
                    content3 = mission.getContent3();
                    content4 = mission.getContent4();
                    content5 = mission.getContent5();

                    loadMissionData_RecogValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void loadMissionData_RecogValue(){
        int j;

        if(content1 != null)
            for (j = 0; j < content1.size(); j++)
                recognition[0][j] = content1.get(j);
        if(content2 != null)
            for (j = 0; j < content2.size(); j++)
                recognition[1][j] = content2.get(j);
        if(content3 != null)
            for (j = 0; j < content3.size(); j++)
                recognition[2][j] = content3.get(j);
        if(content4 != null)
            for (j = 0; j < content4.size(); j++)
                recognition[3][j] = content4.get(j);
        if(content5 != null)
            for (j = 0; j < content5.size(); j++)
                recognition[4][j] = content5.get(j);

        setDataBinding();
    }

    private void setDataBinding(){
        int j;

        if(content1 != null)
            for (j = 0; j < content1.size(); j++) {
                if(recognition[0][j] == 1) {
                    mission_check1.setImageResource(R.drawable.check_box_black);
                    break;
                }
            }
        if(content2 != null)
            for (j = 0; j < content2.size(); j++) {
                if(recognition[1][j] == 1) {
                    mission_check2.setImageResource(R.drawable.check_box_black);
                    break;
                }
            }
        if(content3 != null)
            for (j = 0; j < content3.size(); j++) {
                if(recognition[2][j] == 1) {
                    mission_check3.setImageResource(R.drawable.check_box_black);
                    break;
                }
            }
        if(content4 != null)
            for (j = 0; j < content4.size(); j++) {
                if(recognition[3][j] == 1) {
                    mission_check4.setImageResource(R.drawable.check_box_black);
                }
            }
        if(content5 != null)
            for (j = 0; j < content5.size(); j++) {
                if(recognition[4][j] == 1) {
                    mission_check5.setImageResource(R.drawable.check_box_black);
                    break;
                }
            }

        for(j=0; j<3; j++){
            if(recognition[content-1][j] == 1) {
                switch (content){
                    case 1:
                        mission_check1.setImageResource(R.drawable.check_box_red);
                        mission_content1.setTextColor(Color.parseColor("#9F1E1E"));
                        break;
                    case 2:
                        mission_check2.setImageResource(R.drawable.check_box_red);
                        mission_content2.setTextColor(Color.parseColor("#9F1E1E"));
                        break;
                    case 3:
                        mission_check3.setImageResource(R.drawable.check_box_red);
                        mission_content3.setTextColor(Color.parseColor("#9F1E1E"));
                        break;
                    case 4:
                        mission_check4.setImageResource(R.drawable.check_box_red);
                        mission_content4.setTextColor(Color.parseColor("#9F1E1E"));
                        break;
                    case 5:
                        mission_check5.setImageResource(R.drawable.check_box_red);
                        mission_content5.setTextColor(Color.parseColor("#9F1E1E"));
                        break;
                }
            }
        }

    }

    private void searchData() { // 데이터 불러오기
        setString(content); // 뷰 바인딩 매칭
        Query searchData = FirebaseDatabase.getInstance().getReference("Feed").orderByChild("fid").equalTo(fid);
        searchData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Map map = (Map) snapshot.getValue();
                        Iterator<String> Iterator = map.keySet().iterator();
                        while (Iterator.hasNext()) {
                            Iterator.next();
                            Glide.with(getApplicationContext()).load((String) map.get(feedW_photo)).into(mission_image);
                            mission_title.setText((String) map.get(feedW_title));

                            if((String) map.get("feedW_title1") != null){
                                mission_check1.setImageResource(R.drawable.check_box_default);
                                mission_content1.setText((String) map.get("feedW_title1"));
                            }
                            if((String) map.get("feedW_title2") != null){
                                mission_check2.setImageResource(R.drawable.check_box_default);
                                mission_content2.setText((String) map.get("feedW_title2"));
                            }
                            if((String) map.get("feedW_title3") != null){
                                mission_check3.setImageResource(R.drawable.check_box_default);
                                mission_content3.setText((String) map.get("feedW_title3"));
                            }
                            if((String) map.get("feedW_title4") != null){
                                mission_check4.setImageResource(R.drawable.check_box_default);
                                mission_content4.setText((String) map.get("feedW_title4"));
                            }
                            if((String) map.get("feedW_title5") != null){
                                mission_check5.setImageResource(R.drawable.check_box_default);
                                mission_content5.setText((String) map.get("feedW_title5"));
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setString(int i){ // 뷰 바인딩 매칭
        feedW_photo = "feedW_photo" + i;
        feedW_title = "feedW_title" + i;
    }

    private void setBinding(){
        mission_content.setText("도전과제 "+ content +".");
        mission_state.setVisibility(View.VISIBLE);
        if(score <= 0){
            mission_state.setImageResource(R.drawable.mission_fail);
            mission_ratingBar.setRating(0);
            mission_point.setText("0 원");
            mission_point.setTextColor(Color.parseColor("#707070"));
        }else {
            mission_state.setImageResource(R.drawable.mission_success);
            if(score == 100)
                mission_ratingBar.setRating(5);
            else if(score >= 50)
                mission_ratingBar.setRating(3);
            else
                mission_ratingBar.setRating(1);
            mission_point.setText(score * 10 + " 원");
            mission_point.setTextColor(Color.parseColor("#EF5DA8"));
            uploadPointLevel();
        }
    }

    int loadPoint, loadLevel;

    // todo 컨텐츠 별 남은 점수만 주게끔
    private void uploadPointLevel(){
        String uid = fAuth.getCurrentUser().getUid();
        Query updateUser = FirebaseDatabase.getInstance().getReference("UserData").orderByChild("uid").equalTo(uid);
        updateUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Map map = (Map) snapshot.getValue();
                        Iterator<String> Iterator = map.keySet().iterator();
                        while (Iterator.hasNext()) {
                            Iterator.next();

                            loadPoint = Integer.parseInt((String) map.get("point"));
                            loadLevel = Integer.parseInt((String) map.get("level"));

                            loadPoint += (score * 10);

                            if(loadLevel < 5) {
                                if (loadLevel == 0)
                                    loadLevel = 1;
                                if (loadLevel == 1 && loadPoint >= 1000)
                                    loadLevel = 2;
                                if (loadLevel == 2 && loadPoint >= 3000)
                                    loadLevel = 3;
                                if (loadLevel == 3 && loadPoint >= 5000)
                                    loadLevel = 4;
                                if (loadLevel == 4 && loadPoint >= 7500)
                                    loadLevel = 5;
                            }else if(loadLevel < 9){
                                if(loadLevel == 5 && loadPoint >= 10000)
                                    loadLevel = 6;
                                if(loadLevel == 6 && loadPoint >= 30000)
                                    loadLevel = 7;
                                if(loadLevel == 7 && loadPoint >= 50000)
                                    loadLevel = 8;
                                if(loadLevel == 8 && loadPoint >= 75000)
                                    loadLevel = 9;
                            }else{
                                if(loadPoint / 10000 >= loadLevel)
                                    loadLevel ++;
                            }

                            if(updateState == false){
                                DatabaseReference updateValue = FirebaseDatabase.getInstance().getReference("UserData").child(uid);
                                updateValue.child("point").setValue("" + loadPoint);
                                updateValue.child("level").setValue("" + loadLevel);
                            }
                            updateState = true;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void setView(){
        mission_image = findViewById(R.id.mission_image);
        mission_content = findViewById(R.id.mission_content);
        mission_title = findViewById(R.id.mission_title);
        mission_state = findViewById(R.id.mission_state);
        mission_ratingBar = findViewById(R.id.mission_ratingBar);
        mission_point = findViewById(R.id.mission_point);

        mission_check1 = findViewById(R.id.mission_check1);
        mission_check2 = findViewById(R.id.mission_check2);
        mission_check3 = findViewById(R.id.mission_check3);
        mission_check4 = findViewById(R.id.mission_check4);
        mission_check5 = findViewById(R.id.mission_check5);
        mission_content1 = findViewById(R.id.mission_content1);
        mission_content2 = findViewById(R.id.mission_content2);
        mission_content3 = findViewById(R.id.mission_content3);
        mission_content4 = findViewById(R.id.mission_content4);
        mission_content5 = findViewById(R.id.mission_content5);

        mission_clear = findViewById(R.id.mission_clear);
        mission_clear.setOnClickListener(this::onClick);
    }

    public void onClick(View view){
        if( view.getId() == R.id.mission_clear ){
            Intent intent = new Intent(this, FeedDetail.class);
            intent.putExtra("fid", fid);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() { // 뒤로가기 없음
//        super.onBackPressed();
    }
}