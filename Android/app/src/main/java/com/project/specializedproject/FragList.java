package com.project.specializedproject;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import java.util.Comparator;

public class FragList extends Fragment {
    private String TAG = "FragList";
    Context mContext;
    RecyclerView rList, gList;
    FragListAdapter listAdapter;
    FragListGuideAdapter listGuideAdapter;

    ArrayList<ModelFeed> mFeed = new ArrayList<>();
    ArrayList<ModelUser> mUser = new ArrayList<>();
    String userPermission;

    LinearLayout list_locationLayout;
    ImageButton list_filter, list_write;
    Button list_guide_listBtn;
    Dialog_Filter dialogFilter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_list, container, false);
        mContext = view.getContext();
        rList = view.findViewById(R.id.recyclerList);
        gList = view.findViewById(R.id.recycler_GuideList);
        setView(view);

        searchMyProfile();
        searchUser();
        Comparator comparator = Comparator.comparing(ModelFeed::getFid, (o1, o2) -> o2.compareTo(o1));
        searchFeed(comparator);

        rList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        gList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        listAdapter = new FragListAdapter(mFeed, getContext());
        listGuideAdapter = new FragListGuideAdapter(mUser, getContext());
        rList.setAdapter(listAdapter);
        gList.setAdapter(listGuideAdapter);
        return view;
    }

    private void searchFeed(Comparator comparator) {
        DatabaseReference searchData = FirebaseDatabase.getInstance().getReference("Feed");
        searchData.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mFeed.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelFeed feed = snapshot.getValue(ModelFeed.class);
                    mFeed.add(feed);
                }

                mFeed.sort(comparator);
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void searchUser() {
        Query searchData = FirebaseDatabase.getInstance().getReference("UserData").orderByChild("permission").equalTo("Guide");
        searchData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelUser user = snapshot.getValue(ModelUser.class);
                    mUser.add(user);
                }
                listGuideAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void searchMyProfile(){
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
        list_guide_listBtn = view.findViewById(R.id.list_guide_listBtn);

        list_locationLayout.setOnClickListener(this::onClick);
        list_filter.setOnClickListener(this::onClick);
        list_write.setOnClickListener(this::onClick);
        list_guide_listBtn.setOnClickListener(this::onClick);
    }

    public void onClick(View view){
        if(view.getId() == R.id.list_locationLayout){
            // TODO 커스텀 다이어로그
            Log.e(TAG, "list_locationLayout");

        }else if(view.getId() == R.id.list_filter){
            DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
            int width = dm.widthPixels;
            int height = dm.heightPixels;

            dialogFilter = new Dialog_Filter(getContext(), new Dialog_Filter.FilterListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void clickFunction(String a, boolean b) {
                    if(!a.equals("선택하세요")) {
                        Comparator comparator = Comparator.comparing(ModelFeed::getFid);

                        switch (a) {
                            case "최신순":
                                if(b == false)
                                    comparator = Comparator.comparing(ModelFeed::getC_date, (o1, o2) -> o2.compareTo(o1));
                                else
                                    comparator = Comparator.comparing(ModelFeed::getC_date);
                                break;
                            case "거리순":
                                if(b == false)
                                    comparator = Comparator.comparing(ModelFeed::getFeedW_distance, (o1, o2) -> o2.compareTo(o1));
                                else
                                    comparator = Comparator.comparing(ModelFeed::getFeedW_distance);
                                break;
                            case "시간순":
                                if(b == false)
                                    comparator = Comparator.comparing(ModelFeed::getFeedW_time, (o1, o2) -> o2.compareTo(o1));
                                else
                                    comparator = Comparator.comparing(ModelFeed::getFeedW_time);
                                break;
                            case "가이드순":
                                if(b == false)
                                    comparator = Comparator.comparing(ModelFeed::getUid, (o1, o2) -> o2.compareTo(o1));
                                else
                                    comparator = Comparator.comparing(ModelFeed::getUid);
                                break;
                        }
                        searchFeed(comparator);
                    }
                }
            });
            WindowManager.LayoutParams wm = dialogFilter.getWindow().getAttributes();
            wm.copyFrom(dialogFilter.getWindow().getAttributes());
            wm.width = width * 3/4;
            wm.height = height / 6;
            dialogFilter.show();

        }else if(view.getId() == R.id.list_write){
            if(!userPermission.equals("Traveler")){
                startActivity(new Intent(mContext, FeedWrite.class));
            }else {
                Toast.makeText(mContext, "가이드가 아닙니다.",Toast.LENGTH_SHORT).show();
            }
        }else if(view.getId() == R.id.list_guide_listBtn){
            Toast.makeText(mContext, "미구현입니다.",Toast.LENGTH_SHORT).show();
        }
    }
}
