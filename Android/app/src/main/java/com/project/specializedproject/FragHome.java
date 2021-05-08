package com.project.specializedproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragHome extends Fragment {
    private String TAG = "FragHome";
    Context mContext;
    RecyclerView rList;
    FragHomeAdapter homeAdapter;

    ArrayList<ModelUser> mUser = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_home, container, false);
        mContext = view.getContext();
        rList = view.findViewById(R.id.recyclerHome);

        setFirebase();

        rList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        homeAdapter = new FragHomeAdapter(mUser, getContext());
        rList.setAdapter(homeAdapter);
        return view;
    }

    private void setFirebase() {
        Query searchData = FirebaseDatabase.getInstance().getReference("UserData").orderByChild("permission").equalTo("Guide");
        searchData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelUser user = snapshot.getValue(ModelUser.class);
                    mUser.add(user);
                }
                homeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
