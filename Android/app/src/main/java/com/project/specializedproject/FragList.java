package com.project.specializedproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class FragList extends Fragment {
    private String TAG = "FragList";
    Context mContext;
    RecyclerView rList;
    FragListAdapter listAdapter;

    ArrayList<ModelList> mData = new ArrayList<>();
    String[][] mArray = new String[1000][10];
    int mArrayNum = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_list, container, false);
        mContext = view.getContext();
        rList = view.findViewById(R.id.recyclerList);

        setFirebase();

        rList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        listAdapter = new FragListAdapter(mData, getContext());
        rList.setAdapter(listAdapter);
        return view;
    }

    private void setFirebase() {
        Query searchData = FirebaseDatabase.getInstance().getReference("UserData").orderByChild("permission").equalTo("Guide");
        searchData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mData.clear();
                mArrayNum = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelList user = snapshot.getValue(ModelList.class);
                    mData.add(user);
                }
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
