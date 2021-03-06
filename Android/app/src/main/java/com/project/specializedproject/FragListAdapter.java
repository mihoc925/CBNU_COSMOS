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
                try {
                    Glide.with(mContext).load(user.getProfileImg()).into(holder.list_profileImg);
                }catch (Exception e){}
                holder.list_nick.setText(user.getNick());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });


        holder.list_title.setText(mFeed.get(position).getFeedW_title0());
        holder.list_note.setText(mFeed.get(position).getFeedW_note0());
        holder.list_date.setText(viewDate(mFeed.get(position).getC_date(), mFeed.get(position).getU_date()));
        Glide.with(mContext).load(mFeed.get(position).getFeedW_photo0()).into(holder.list_photo);
//        holder.list_reply.setText(mFeed.get(position).getReply());

        holder.list_contentCount.setText(mFeed.get(position).getFeedW_count());
//        holder.list_contentFollow.setText(mFeed.get(position).getContentFollow());
//        holder.list_contentCompletion.setText(mFeed.get(position).getContentCompletion());
        holder.list_contentDistance.setText(contentDistance_Cal(mFeed.get(position).getFeedW_distance()));
        holder.list_contentTime.setText(contentTime_Cal(mFeed.get(position).getFeedW_time()));

        holder.list_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FeedDetail.class);
                intent.putExtra("fid",  mFeed.get(position).getFid());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    private String viewDate(String cdate, String udate){
        int i = 0;
        String outputDate = "";

        String[] tmpCdate = cdate.split("_"); // 포멧 변경
        String[] tmpUdate = udate.split("_");
        String tempCdate = tmpCdate[0];
        String tempUdate = tmpUdate[0];
        String[] strCdate = tempCdate.split("/");
        String[] strUdate = tempUdate.split("/");

        String valCdate = ""; // 날짜 비교
        String valUdate = "";
        for(i=0; i<strCdate.length; i++)
            valCdate += strCdate[i];
        for(i=0; i<strUdate.length; i++)
            valUdate += strUdate[i];

        if(Integer.parseInt(valCdate) >= Integer.parseInt(valUdate))
            outputDate = strCdate[0] + "." + strCdate[1] + "." + strCdate[2];
        else
            outputDate = strUdate[0] + "." + strUdate[1] + "." + strUdate[2];

        return outputDate;
    }

    private String contentDistance_Cal(String x){
        if(x != null && !x.equals("")) {
            double calX = Double.parseDouble(x);
            x = Math.round((calX / 1000)*100)/100.0 + "km";
        }
        return x;
    }

    private String contentTime_Cal(String x){
        if(x != null && !x.equals("")) {
            int calX = Integer.parseInt(x);
            int hour = (calX / 60);
            int min = (calX % 60);
            if(min < 10)
                x = hour + "시간 0" + min + "분";
            else
                x = hour + "시간 " + min + "분";
        }
        return x;
    }

    @Override
    public int getItemCount() {
        return mFeed.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView list_profileImg;
        ImageView list_photo;
        TextView list_nick, list_title, list_note, list_reply, list_date,
                list_contentCount, list_contentFollow, list_contentCompletion,
                list_contentDistance, list_contentTime;

        LinearLayout list_linear;

        ViewHolder(View itemView) {
            super(itemView);
            list_profileImg = itemView.findViewById(R.id.list_profileImg);
            list_photo = itemView.findViewById(R.id.list_photo);
            list_nick = itemView.findViewById(R.id.list_nick);
            list_title = itemView.findViewById(R.id.list_title);
            list_note = itemView.findViewById(R.id.list_note);
            list_reply = itemView.findViewById(R.id.list_reply);
            list_date = itemView.findViewById(R.id.list_date);

            list_contentCount = itemView.findViewById(R.id.list_contentCount);
            list_contentFollow = itemView.findViewById(R.id.list_contentFollow);
            list_contentCompletion = itemView.findViewById(R.id.list_contentCompletion);
            list_contentDistance = itemView.findViewById(R.id.list_contentDistance);
            list_contentTime = itemView.findViewById(R.id.list_contentTime);

            list_linear = itemView.findViewById(R.id.list_linear);
        }
    }
}
