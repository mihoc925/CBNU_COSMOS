package com.project.specializedproject;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;

public class GuideList extends AppCompatActivity {
    private String TAG = "GuideList";

    ImageButton guide_list_x;
    RecyclerView rList;
    GuideListAdapter guideAdapter;
    ArrayList<ModelUser> mUser = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_list);
        setView();
        searchGuide();

        rList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        guideAdapter = new GuideListAdapter(mUser, getApplicationContext());
        rList.setAdapter(guideAdapter);
    }

    private void searchGuide() {
        Query searchData = FirebaseDatabase.getInstance().getReference("UserData").orderByChild("permission").equalTo("Guide");
        searchData.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelUser user = snapshot.getValue(ModelUser.class);
                    mUser.add(user);
                }
                mUser.sort(Comparator.comparing(ModelUser::getLevel, (o1, o2) -> o2.compareTo(o1)));
                guideAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setView(){
        guide_list_x = findViewById(R.id.guide_list_x);
        guide_list_x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rList = findViewById(R.id.guide_list_recycler);
    }
}