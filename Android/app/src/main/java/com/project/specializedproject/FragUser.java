package com.project.specializedproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class FragUser extends Fragment {
    private String TAG = "FragUser";
    Context mContext;

    FirebaseAuth fAuth;
    private ArrayList<ModelFeed> mFeed = new ArrayList<>();
    FragUserAdapter userAdapter;
    RecyclerView rList;
    int fArrayNum = 0;
    int proceedMission[] = new int[2]; // 비동기 처리
    ArrayList<String> clearMission = new ArrayList<>();


    Dialog_Guide_Signup guideSignup;

    CircleImageView user_profileImg;
    TextView user_nick, user_permission, user_level, user_point;
    Button user_permissionBtn, user_logoutBtn;
    String guide_per;

    // 프로필 이미지
    Uri uri;
    int proceedMaxNum[] = new int[2]; // 비동기 처리
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance(); // 초기화 필수
    String photo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_user, container, false);
        mContext = view.getContext();
        fAuth = FirebaseAuth.getInstance();
        setView(view);
        searchMyProfile();
        searchMission();

        rList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        userAdapter = new FragUserAdapter(mFeed, getContext());
        rList.setAdapter(userAdapter);
        return view;
    }

    private void searchMyProfile() {
        DatabaseReference searchData = FirebaseDatabase.getInstance().getReference("UserData").child(fAuth.getCurrentUser().getUid());
        searchData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    ModelUser user = dataSnapshot.getValue(ModelUser.class);

//                    Picasso.get().load(user.getProfileImg()).into(user_profileImg);
                    Glide.with(mContext).load(user.getProfileImg()).into(user_profileImg);
                    user_nick.setText(user.getNick());
                    user_level.setText(user.getLevel());
                    user_point.setText(user.getPoint());

                    if(user.getPermission().equals("Guide")){
                        user_permission.setText("가이드");
                        user_permissionBtn.setText("내 게시글");
                        guide_per = "Guide";
                    }else{
                        user_permission.setText("여행자");
                        user_permissionBtn.setText("가이드 신청");
                        guide_per = "Traveler";
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void searchMission() {
        proceedMission[0] = 0;
        proceedMission[1] = 0;
        Query searchData = FirebaseDatabase.getInstance().getReference("Mission")
                .orderByChild(fAuth.getCurrentUser().getUid());
        searchData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mFeed.clear();
                fArrayNum = 0;

                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        proceedMission[0]++;
                        String fid = String.valueOf(snapshot.getKey());
                        searchClearMission(fid);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void searchClearMission(String fid) {
        DatabaseReference searchData = FirebaseDatabase.getInstance().getReference("Mission").child(fid)
                .child(fAuth.getCurrentUser().getUid()).child("clear");
        searchData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                proceedMission[1]++;
                if(dataSnapshot.getValue() != null) {
                    if (Integer.parseInt(String.valueOf(dataSnapshot.getValue())) == 1) {
                        clearMission.add(fid);
                    }
                }
                if(proceedMission[0] == proceedMission[1]) {
                    for (int i = 0; i < clearMission.size(); i++)
                        addItem(i);
                    userAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void addItem(int i) {
        ModelFeed mf = new ModelFeed();
        mf.setFid(clearMission.get(i));
        mFeed.add(mf);
    }

    private void setView(View view){
        rList = view.findViewById(R.id.user_travel);
        user_profileImg = view.findViewById(R.id.user_profileImg);
        user_nick = view.findViewById(R.id.user_nick);
        user_permission = view.findViewById(R.id.user_permission);
        user_level = view.findViewById(R.id.user_level);
        user_point = view.findViewById(R.id.user_point);

        user_permissionBtn = view.findViewById(R.id.user_permissionBtn);
        user_logoutBtn = view.findViewById(R.id.user_logoutBtn);
        user_profileImg.setOnClickListener(this::onClick);
        user_permissionBtn.setOnClickListener(this::onClick);
        user_logoutBtn.setOnClickListener(this::onClick);
    }

    public void onClick(View view){
        if(view.getId() == R.id.user_permissionBtn){

            DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
            int width = dm.widthPixels;
            int height = dm.heightPixels;

            if(guide_per.equals("Guide")){
                Toast.makeText(getContext(), "미구현입니다.",Toast.LENGTH_SHORT).show();
            }else{
                guideSignup = new Dialog_Guide_Signup(mContext);
                WindowManager.LayoutParams wm = guideSignup.getWindow().getAttributes();
                wm.copyFrom(guideSignup.getWindow().getAttributes());
                wm.width = width * 3/4;
                wm.height = height / 4;
                guideSignup.show();
            }
        }else if(view.getId() == R.id.user_logoutBtn){
            new AlertDialog.Builder(mContext).setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?").setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(mContext, AccountLogin.class));
                    removeFragment(FragUser.this); // 종료
                    Toast.makeText(mContext,"로그아웃 되었습니다.",Toast.LENGTH_LONG).show();
                }})
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Toast.makeText(mContext,"로그아웃을 취소하였습니다.",Toast.LENGTH_LONG).show();
                        }
                    }).show();
        }else if(view.getId() == R.id.user_profileImg){
            gallery();
        }
    }

    private void removeFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
            final FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.remove(fragment);
            mFragmentTransaction.commit();
            fragment.onDestroy();
            fragment.onDetach();
            fragment = null;
        }
    }

    private void gallery(){
        Intent intent= new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,10);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 10:
                if(resultCode==RESULT_OK){
                    uri = data.getData(); //선택한 이미지의 경로 얻어오기
                    Glide.with(this).load(uri).into(user_profileImg);
                    upload_Image(); // 바로 실행
                }
                break;
        }
    }

    private void upload_Image() {
        proceedMaxNum[0] = 0; // Task 검증 초기화
        proceedMaxNum[1] = 0;

        if(uri != null){ // NullPointException
            proceedMaxNum[0]++;
            upload_proceed();
        }else{
            Toast.makeText(getContext(), "이미지가 선택되지 않았습니다.",Toast.LENGTH_SHORT).show();
        }
    }

    private void upload_proceed(){
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss");
        String filename = mAuth.getCurrentUser().getUid()
                + "_" + sdf.format(new Date()) + ".png";

        StorageReference photoRef= firebaseStorage.getReference("Profile/"+filename);
        StorageTask uploadTask = photoRef.putFile(uri);
        uploadTask.continueWithTask(new Continuation() { // using Task.. 느림
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return photoRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUrl = task.getResult();
                    photo = downloadUrl.toString();

                    proceedMaxNum[1]++;
                    if(proceedMaxNum[0] == proceedMaxNum[1])
                        submit_Data();
                }
            }
        });
    }

    private void submit_Data(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserData").child(mAuth.getCurrentUser().getUid());
        ref.child("profileImg").setValue(photo);
    }
}
