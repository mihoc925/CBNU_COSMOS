package com.project.specializedproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static androidx.core.content.ContextCompat.startActivity;

public class Dialog_Guide_Signup extends Dialog {

    Button dialog_guide_signup, dialog_guide_cancel;

    public Dialog_Guide_Signup(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_dialog_guide_signup);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog_guide_signup = findViewById(R.id.dialog_guide_signup);
        dialog_guide_cancel = findViewById(R.id.dialog_guide_cancel);
        dialog_guide_signup.setOnClickListener(this::onClick);
        dialog_guide_cancel.setOnClickListener(this::onClick);
    }

    public void onClick(View view){
        if(view.getId() == R.id.dialog_guide_signup){
            FirebaseAuth fAuth = FirebaseAuth.getInstance();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("UserData").child(fAuth.getCurrentUser().getUid());
            ref.child("permission").setValue("Guide");
            ref.child("profileImg").setValue("https://firebasestorage.googleapis.com/v0/b/specializedproject-a3dd0.appspot.com/o/defaultProfileGuide.png?alt=media&token=b18ac610-3009-4655-b6ef-901c88289400");
            dismiss();

            Intent i = new Intent(getContext(), MainActivity.class);
            this.getContext().startActivity(i);
            Toast.makeText(getContext(), "등급이 가이드로 변경되었습니다!",Toast.LENGTH_SHORT).show();

        }else if(view.getId() == R.id.dialog_guide_cancel){
            dismiss();
        }
    }
}