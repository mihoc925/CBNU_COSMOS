package com.project.specializedproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragListAdapter extends RecyclerView.Adapter<FragListAdapter.ViewHolder> {
    private String TAG = "FragListAdapter";
    private List<ModelFeed> mFeed;
    private Context mContext;

    public FragListAdapter(ArrayList<ModelFeed> feed, Context context) {
        this.mFeed = feed;
        this.mContext = context;
    }

    @NonNull
    @Override
    public FragListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_list_item, parent, false);

        FragListAdapter.ViewHolder vh = new FragListAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull FragListAdapter.ViewHolder holder, int position) {
        // 유저 프로필
        DatabaseReference searchData = FirebaseDatabase.getInstance().getReference("UserData").child(mFeed.get(position).getUid());
        searchData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUser user = dataSnapshot.getValue(ModelUser.class);
                Glide.with(mContext).load(user.getProfileImg()).into(holder.list_profileImg);
                holder.list_nick.setText(user.getNick());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        Glide.with(mContext).load(mFeed.get(position).getPhoto()).into(holder.list_photo);
        holder.list_title.setText(mFeed.get(position).getTitle());
        holder.list_note.setText(mFeed.get(position).getNote());
        holder.list_reply.setText(mFeed.get(position).getReply());

        holder.list_contentCount.setText(mFeed.get(position).getContentCount());
        holder.list_contentFollow.setText(mFeed.get(position).getContentFollow());
        holder.list_contentCompletion.setText(mFeed.get(position).getContentCompletion());
        holder.list_contentDistance.setText(mFeed.get(position).getContentDistance());
        holder.list_contentTime.setText(mFeed.get(position).getContentTime());
    }

    @Override
    public int getItemCount() {
        return mFeed.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView list_profileImg;
        ImageView list_photo;
        TextView list_nick, list_title, list_note, list_reply,
                list_contentCount, list_contentFollow, list_contentCompletion,
                list_contentDistance, list_contentTime;

        ViewHolder(View itemView) {
            super(itemView);
            list_profileImg = itemView.findViewById(R.id.list_profileImg);
            list_photo = itemView.findViewById(R.id.list_photo);
            list_nick = itemView.findViewById(R.id.list_nick);
            list_title = itemView.findViewById(R.id.list_title);
            list_note = itemView.findViewById(R.id.list_note);
            list_reply = itemView.findViewById(R.id.list_reply);

            list_contentCount = itemView.findViewById(R.id.list_contentCount);
            list_contentFollow = itemView.findViewById(R.id.list_contentFollow);
            list_contentCompletion = itemView.findViewById(R.id.list_contentCompletion);
            list_contentDistance = itemView.findViewById(R.id.list_contentDistance);
            list_contentTime = itemView.findViewById(R.id.list_contentTime);

            // 리싸이클러뷰 아이템 클릭 이벤트
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {

//                        notifyItemChanged(pos); // 해당 포지션 뷰 업데이트
                    }
                }
            });

        }
    }
}
