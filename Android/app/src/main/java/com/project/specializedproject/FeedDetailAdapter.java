package com.project.specializedproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class FeedDetailAdapter extends RecyclerView.Adapter<FeedDetailAdapter.ViewHolder> {
    private String TAG = "FeedDetailAdapter";
    private List<ModelFeed> mFeed;
    private Context mContext;

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

        holder.dti_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCallback.backBtnCallback(true);
                Intent intent = new Intent(mContext, ClassifierActivity.class);
                intent.putExtra("state",  "mission");
                intent.putExtra("content",  position + 1);
                intent.putExtra("fid",  "" + mFeed.get(position).getFid());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
//        holder.dti_location.setText(mFeed.get(position).getFeedW_location1());
    }

    @Override
    public int getItemCount() {
        return mFeed.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView dti_photo, dti_err;
        TextView dti_mission, dti_title, dti_note, dti_location;
        Button dti_camera;

        ViewHolder(View itemView) {
            super(itemView);
            dti_photo = itemView.findViewById(R.id.dti_photo);
            dti_err = itemView.findViewById(R.id.dti_err);
            dti_mission = itemView.findViewById(R.id.dti_mission);
            dti_title = itemView.findViewById(R.id.dti_title);
            dti_note = itemView.findViewById(R.id.dti_note);
            dti_location = itemView.findViewById(R.id.dti_location);
            dti_camera = itemView.findViewById(R.id.dti_camera);

//            dti_err.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.e(TAG, "dti_err");
//                }
//            });
//            dti_location.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.e(TAG, "dti_loc");
//                }
//            });
//            dti_camera.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.e(TAG, "dti_cam");
//                }
//            });
        }
    }
}
