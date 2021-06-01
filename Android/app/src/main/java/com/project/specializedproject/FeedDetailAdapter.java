package com.project.specializedproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeedDetailAdapter extends RecyclerView.Adapter<FeedDetailAdapter.ViewHolder> {
    private String TAG = "FeedDetailAdapter";
    private List<ModelFeed> mFeed;
    FirebaseAuth fAuth;
    private Context mContext;
    int[] contentNum = new int[2];

    public FeedDetailAdapter(ArrayList<ModelFeed> feed, Context context) {
        this.mFeed = feed;
        this.mContext = context;
    }

    // Adapter to Activity : backBtn Ben
    clickEventListener btnCallback;
    public void setClickEventListener(clickEventListener btnCallback) {
        this.btnCallback = btnCallback;
    }

    public interface clickEventListener {
        public void backBtnCallback(boolean backState);
    }

    @NonNull
    @Override
    public FeedDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_detail_item, parent, false);

        FeedDetailAdapter.ViewHolder vh = new FeedDetailAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedDetailAdapter.ViewHolder holder, int position) {
        Glide.with(mContext).load(mFeed.get(position).getFeedW_photo()).into(holder.dti_photo);
        holder.dti_title.setText(mFeed.get(position).getFeedW_title());
        holder.dti_note.setText(mFeed.get(position).getFeedW_note());
        holder.dti_mission.setText("도전과제 " + (position+1) + ".");
        contentNum[0]++; // contentNum
        fAuth = FirebaseAuth.getInstance();

        searchMission(holder, position);

        String fd_item = mFeed.get(position).getFd_item();
        if(fd_item == null || fd_item.equals("")){
            holder.dti_location.setVisibility(View.GONE);
            holder.dti_locationImg.setVisibility(View.GONE);
            holder.dti_camera.setVisibility(View.GONE);
            holder.dti_err.setVisibility(View.GONE);
            contentNum[1]++; // nullContentNum
        }else {
            holder.dti_locationImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    locationClick();
                }
            });
            holder.dti_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    locationClick();
                }
            });

            holder.dti_camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.dti_camera.getText().equals("도전과제")) {
                        startMission(position);
                    }else if(holder.dti_camera.getText().equals("다시수행")){
                        new AlertDialog.Builder(FeedDetail.detail_Context).setTitle("다시 수행").setMessage("이미 점수를 받은 도전과제입니다.\n다시 수행할 경우 초기화됩니다.")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        startMission(position);
                                    }})
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                }).show();
                    }else if(holder.dti_camera.getText().equals("완료")){
                        Toast.makeText(mContext, "이미 완료된 수행과제 입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.dti_err.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "err"+position);
                }
            });
        }

        // end Count
        if(contentNum[0] == mFeed.size()){
            if(contentNum[0] == contentNum[1]) // nothing mission
                ((FeedDetail) FeedDetail.detail_Context).detail_complete.setVisibility(View.GONE);
            else
                ((FeedDetail) FeedDetail.detail_Context).detail_complete.setVisibility(View.VISIBLE);
        }
    }

    ArrayList<Integer> missionData = new ArrayList<>();
    int missionValue = 0;
    int clearMission = 0;

    private void searchMission(FeedDetailAdapter.ViewHolder holder, int position) {
        DatabaseReference searchData = FirebaseDatabase.getInstance().getReference("Mission")
                .child(mFeed.get(position).getFid()).child(fAuth.getCurrentUser().getUid())
                .child("content"+(position+1));
        searchData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    missionValue = 0;
                    missionData = (ArrayList<Integer>)dataSnapshot.getValue();


                    for(int i=0; i<missionData.size(); i++) {
                        if(Integer.parseInt(String.valueOf(missionData.get(i))) == 0)
                            missionValue++;
                        if(missionValue == 0){ // Complete
                            holder.dti_camera.setText("완료");
                            holder.dti_camera.setBackgroundResource(R.drawable.round_edge10_btn_1);
                        }else if(missionValue < 3){ // Incomplete
                            holder.dti_camera.setText("다시수행");
                            holder.dti_camera.setBackgroundResource(R.drawable.round_edge10_btn_2);
                        }else if(missionValue >= 3){ // Failure
                            holder.dti_camera.setText("도전과제");
                            holder.dti_camera.setBackgroundResource(R.drawable.round_edge10_btn);
                        }else {
                            return;
                        }
                    }
                    if(missionValue < 3) {
                        clearMission++;
                        if(position+1 == clearMission){
                            ((FeedDetail) FeedDetail.detail_Context).detail_complete.setVisibility(View.VISIBLE);
                        }else {
                            ((FeedDetail) FeedDetail.detail_Context).detail_complete.setVisibility(View.GONE);
                        }
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void startMission(int position){
        btnCallback.backBtnCallback(true);
        Intent intent = new Intent(mContext, ClassifierActivity.class);
        intent.putExtra("state", "mission");
        intent.putExtra("content", position + 1);
        intent.putExtra("fid", "" + mFeed.get(position).getFid());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private void locationClick(){ // top Scroll
        ((FeedDetail) FeedDetail.detail_Context).detail_scroll.post(new Runnable() {
            @Override
            public void run() {
                ((FeedDetail) FeedDetail.detail_Context).detail_scroll.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFeed.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView dti_photo, dti_err, dti_locationImg;
        TextView dti_mission, dti_title, dti_note, dti_location;
        Button dti_camera;

        ViewHolder(View itemView) {
            super(itemView);
            dti_photo = itemView.findViewById(R.id.dti_photo);
            dti_err = itemView.findViewById(R.id.dti_err);
            dti_locationImg = itemView.findViewById(R.id.dti_locationImg);
            dti_mission = itemView.findViewById(R.id.dti_mission);
            dti_title = itemView.findViewById(R.id.dti_title);
            dti_note = itemView.findViewById(R.id.dti_note);
            dti_location = itemView.findViewById(R.id.dti_location);
            dti_camera = itemView.findViewById(R.id.dti_camera);
        }
    }
}
