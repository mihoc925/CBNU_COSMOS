package com.project.specializedproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragList extends Fragment {
    private String TAG = "FragList";
    Context mContext;
    RecyclerView rList;
    FragListAdapter listAdapter;

    ArrayList<ModelFeed> mFeed = new ArrayList<>();
    String userPermission;

    LinearLayout list_locationLayout;
    ImageButton list_filter, list_write;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_list, container, false);
        mContext = view.getContext();
        rList = view.findViewById(R.id.recyclerList);
        setView(view);
        searchMyProfile();

        searchFeed();

        rList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        listAdapter = new FragListAdapter(mFeed, getContext());
        rList.setAdapter(listAdapter);
        return view;
    }

    private void searchFeed() {
        Query searchData = FirebaseDatabase.getInstance().getReference("Feed").orderByChild("fid");
        searchData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mFeed.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelFeed feed = snapshot.getValue(ModelFeed.class);
                    mFeed.add(feed);
                }

                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void searchMyProfile() {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        DatabaseReference searchData = FirebaseDatabase.getInstance().getReference("UserData").child(fAuth.getCurrentUser().getUid());
        searchData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    ModelUser user = dataSnapshot.getValue(ModelUser.class);
                    if(user.getPermission().equals("Guide")){
                        list_write.setVisibility(View.VISIBLE);
                        userPermission = "Guide";
                    }else{
                        list_write.setVisibility(View.GONE);
                        userPermission = "Traveler";
                    }
                }else{
                    list_write.setVisibility(View.GONE);
                    userPermission = "Traveler";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void setView(View view){
        list_locationLayout = view.findViewById(R.id.list_locationLayout);
        list_filter = view.findViewById(R.id.list_filter);
        list_write = view.findViewById(R.id.list_write);

        list_locationLayout.setOnClickListener(this::onClick);
        list_filter.setOnClickListener(this::onClick);
        list_write.setOnClickListener(this::onClick);
    }

    public void onClick(View view){
        if(view.getId() == R.id.list_locationLayout){
            Log.e(TAG, "list_locationLayout");

        }else if(view.getId() == R.id.list_filter){
            Log.e(TAG, "list_filter");

        }else if(view.getId() == R.id.list_write){
            if(!userPermission.equals("Traveler")){
                startActivity(new Intent(mContext, FeedWrite.class));
            }else {
                Toast.makeText(mContext, "가이드가 아닙니다.",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
