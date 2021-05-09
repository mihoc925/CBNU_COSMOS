package com.project.specializedproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

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

    int stateNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_write);
        setView();

    }


    private void onStarClicked() {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        DatabaseReference auto_increment = FirebaseDatabase.getInstance()
                .getReference("UserData").child(fAuth.getCurrentUser().getUid()).child("fid");
        auto_increment.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                ModelUser model = mutableData.getValue(ModelUser.class);

                if (model == null) {
                    mutableData.setValue(1);
                    Log.d(TAG, "model null:"+mutableData.getValue());
                }else{
                    mutableData.setValue((Long) mutableData.getValue() + 1);
                    Log.d(TAG, "model else:"+mutableData.getValue());
                }

//                if (model.stars.containsKey(getUid())) {
//                    // Unstar the post and remove self from stars
//                    model.starCount = model.starCount - 1;
//                    model.stars.remove(getUid());
//                } else {
//                    // Star the post and add self to stars
//                    model.starCount = model.starCount + 1;
//                    model.stars.put(getUid(), true);
//                }

                // Set value and report transaction success
//                mutableData.setValue(model);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }



    public void onClick(View view){
        if(view.getId() == R.id.feedW_plusBtn){
            nogada("plus");
        }else if(view.getId() == R.id.feedW_minusBtn){
            nogada("minus");
        }
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
    }
}