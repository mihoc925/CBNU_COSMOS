package com.project.specializedproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GuideListAdapter extends RecyclerView.Adapter<GuideListAdapter.ViewHolder> {
    private String TAG = "GuideListAdapter";
    private List<ModelUser> mUser;
    private Context mContext;

    public GuideListAdapter(ArrayList<ModelUser> user, Context context) {
        this.mUser = user;
        this.mContext = context;
    }

    @NonNull
    @Override
    public GuideListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_guide_list_item, parent, false);

        GuideListAdapter.ViewHolder vh = new GuideListAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull GuideListAdapter.ViewHolder holder, int position) {
        Glide.with(mContext).load(mUser.get(position).getProfileImg()).into(holder.guide_item_profileImg);
        holder.guide_item_nick.setText(mUser.get(position).getNick());
        holder.guide_item_level.setText("Lv." + mUser.get(position).getLevel());
//        holder.guide_item_postCount.setText(feedCount[position]);
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView guide_item_profileImg;
        TextView guide_item_nick, guide_item_level, guide_item_postCount;

        ViewHolder(View itemView) {
            super(itemView);

            guide_item_profileImg = itemView.findViewById(R.id.guide_item_profileImg);
            guide_item_nick = itemView.findViewById(R.id.guide_item_nick);
            guide_item_level = itemView.findViewById(R.id.guide_item_level);
            guide_item_postCount = itemView.findViewById(R.id.guide_item_postCount);

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