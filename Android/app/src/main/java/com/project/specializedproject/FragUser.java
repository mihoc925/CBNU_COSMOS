package com.project.specializedproject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragUser extends Fragment {
    private String TAG = "FragUser";
    Context mContext;

    CircleImageView user_profileImg;
    TextView user_nick, user_permission, user_level, user_point;
    Button user_permissionBtn, user_logoutBtn;
    String guide_per;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_user, container, false);
        mContext = view.getContext();
        setView(view);
        searchMyProfile();

        return view;
    }

    private void searchMyProfile() {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        DatabaseReference searchData = FirebaseDatabase.getInstance().getReference("UserData").child(fAuth.getCurrentUser().getUid());
        searchData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    ModelUser user = dataSnapshot.getValue(ModelUser.class);

//                    Picasso.get().load(user.getProfileImg()).into(user_profileImg);
                    Glide.with(mContext).load(user.getProfileImg()).into(user_profileImg);
                    user_nick.setText(user.getNick());
                    user_level.setText(user.getLevel());
                    user_point.setText(user.getPoint());

                    if(user.getPermission().equals("Guide")){
                        user_permission.setText("가이드");
                        user_permissionBtn.setText("가이드 목록");
                        guide_per = "Guide";
                    }else{
                        user_permission.setText("여행자");
                        user_permissionBtn.setText("가이드 신청");
                        guide_per = "Traveler";
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setView(View view){
        user_profileImg = view.findViewById(R.id.user_profileImg);
        user_nick = view.findViewById(R.id.user_nick);
        user_permission = view.findViewById(R.id.user_permission);
        user_level = view.findViewById(R.id.user_level);
        user_point = view.findViewById(R.id.user_point);

        user_permissionBtn = view.findViewById(R.id.user_permissionBtn);
        user_logoutBtn = view.findViewById(R.id.user_logoutBtn);
        user_permissionBtn.setOnClickListener(this::onClick);
        user_logoutBtn.setOnClickListener(this::onClick);
    }

    public void onClick(View view){
        if(view.getId() == R.id.user_permissionBtn){
            if(guide_per.equals("Guide")){
                Log.e(TAG, "가이드");
            }else{
                Log.e(TAG, "여행자");
            }
        }else if(view.getId() == R.id.user_logoutBtn){
            Log.e(TAG, "user_logoutBtn");
        }
    }
}
