package com.project.specializedproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragHomeAdapter extends RecyclerView.Adapter<FragHomeAdapter.ViewHolder> {
    private String TAG = "FragHomeAdapter";
    private List<ModelUser> mUser;
    private Context mContext;

    public FragHomeAdapter(ArrayList<ModelUser> user, Context context) {
        this.mUser = user;
        this.mContext = context;
    }

    @NonNull
    @Override
    public FragHomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_home_item, parent, false);

        FragHomeAdapter.ViewHolder vh = new FragHomeAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull FragHomeAdapter.ViewHolder holder, int position) {
//        Log.e(TAG, ">>>"+mUser.get(position).getProfileImg());
//        Glide.with(mContext).load(mUser.get(position).getProfileImg()).into(holder.home_item_profileImg);
//        holder.home_item_nick.setText(mUser.get(position).getNick());
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView home_item_profileImg;
        TextView home_item_nick, home_item_level, home_item_postCount, home_item_followCount;
        ImageButton home_item_followBtn;

        ViewHolder(View itemView) {
            super(itemView);

            home_item_profileImg = itemView.findViewById(R.id.home_item_profileImg);
            home_item_nick = itemView.findViewById(R.id.home_item_nick);

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