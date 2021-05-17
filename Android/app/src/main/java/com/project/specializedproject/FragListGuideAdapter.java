package com.project.specializedproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragListGuideAdapter extends RecyclerView.Adapter<FragListGuideAdapter.ViewHolder> {
    private String TAG = "FragListGuideAdapter";
    private List<ModelUser> mUser;
    private Context mContext;

    public FragListGuideAdapter(ArrayList<ModelUser> user, Context context) {
        this.mUser = user;
        this.mContext = context;
    }

    @NonNull
    @Override
    public FragListGuideAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_frag_list_guide_list, parent, false);

        FragListGuideAdapter.ViewHolder vh = new FragListGuideAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull FragListGuideAdapter.ViewHolder holder, int position) {
        Glide.with(mContext).load(mUser.get(position).getProfileImg()).into(holder.list_guide_img);
        holder.list_guide_id.setText(mUser.get(position).getNick());
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView list_guide_img;
        TextView list_guide_id;

        ViewHolder(View itemView) {
            super(itemView);
            list_guide_img = itemView.findViewById(R.id.list_guide_img);
            list_guide_id = itemView.findViewById(R.id.list_guide_id);

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
