package com.project.specializedproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragListAdapter extends RecyclerView.Adapter<FragListAdapter.ViewHolder> {
    private String TAG = "FragListAdapter";
    private List<ModelList> mData;
    private Context mContext;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public FragListAdapter(ArrayList<ModelList> arr, Context context) {
        this.mData = arr;
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
        Glide.with(mContext).load(mData.get(position).getProfileImg()).into(holder.profileImg);
        holder.nick.setText(mData.get(position).getNick());

//        Query searchUser = FirebaseDatabase.getInstance().getReference("UserData").orderByChild("permission").equalTo("Guide");
//        searchUser.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Map map = (Map)snapshot.getValue();
////                    Glide.with(mContext).load((String)map.get("profileImg")).into(holder.profileImg);
////                    mData.get(position).setNick((String)map.get("nick"));
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) { }
//        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImg;
        TextView nick;

        ViewHolder(View itemView) {
            super(itemView);

            profileImg = itemView.findViewById(R.id.profileImg);
            nick = itemView.findViewById(R.id.nick);

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
