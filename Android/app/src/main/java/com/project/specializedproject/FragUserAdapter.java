package com.project.specializedproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FragUserAdapter extends RecyclerView.Adapter<FragUserAdapter.ViewHolder> {
    private String TAG = "FragUserAdapter";
    private List<ModelFeed> mFeed;
    private Context mContext;

    public FragUserAdapter(ArrayList<ModelFeed> feed, Context context) {
        this.mFeed = feed;
        this.mContext = context;
    }

    @NonNull
    @Override
    public FragUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_second_feed_item, parent, false);

        FragUserAdapter.ViewHolder vh = new FragUserAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull FragUserAdapter.ViewHolder holder, int position) {
        Query searchData = FirebaseDatabase.getInstance().getReference("Feed").orderByChild("fid").equalTo(mFeed.get(position).getFid());
        searchData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                        Map map = (Map) snapshot.getValue();
                        Iterator<String> Iterator = map.keySet().iterator();
                        while (Iterator.hasNext()) {
                            String next = Iterator.next();

                            Glide.with(mContext).load((String) map.get("feedW_photo0")).into(holder.sfi_image);
                            holder.sfi_title.setText((String) map.get("feedW_title0"));
                            holder.sfi_note.setText((String) map.get("feedW_note0"));
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        holder.sfi_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FeedDetail.class);
                intent.putExtra("fid", mFeed.get(position).getFid());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFeed.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout sfi_layout;
        ImageView sfi_image;
        TextView sfi_title, sfi_note;

        ViewHolder(View itemView) {
            super(itemView);
            sfi_layout = itemView.findViewById(R.id.sfi_layout);
            sfi_image = itemView.findViewById(R.id.sfi_image);
            sfi_title = itemView.findViewById(R.id.sfi_title);
            sfi_note = itemView.findViewById(R.id.sfi_note);
        }
    }
}
