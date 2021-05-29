package com.project.specializedproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountLogin extends AppCompatActivity {
    private String TAG = "AccountLogin";
    ProgressDialog pd;

    Button login_Btn, login_regitBtn;
    EditText login_email, login_pass;

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        pd = new ProgressDialog(AccountLogin.this);
        pd.setMessage("잠시만 기다려주세요");
        pd.show();

        FirebaseApp.initializeApp(getApplicationContext()); // Firebase init
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // 자동 로그인
        if (firebaseUser != null) {
            pd.dismiss();
            Intent i = new Intent(AccountLogin.this, MainActivity.class);
            startActivity(i);
            finish();
        }else
            pd.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_login);

        login_email = findViewById(R.id.login_email);
        login_pass = findViewById(R.id.login_pass);
        login_regitBtn = findViewById(R.id.login_regitBtn);
        login_Btn = findViewById(R.id.login_Btn);

        // 동영상 설정
        VideoView vd = findViewById(R.id.login_vd);
        vd.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.travel));
        vd.start();
        vd.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true); // 동영상 무한 반복. 반복을 원치 않을 경우 false
            }
        });

        login_regitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AccountRegit.class));
                finish();
            }
        });

        login_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(login_email.getText().toString())) {
                    login_email.setError("이메일을 입력해주세요");
                    return;
                }
                if (TextUtils.isEmpty(login_pass.getText().toString())) {
                    login_pass.setError("비밀번호를 입력해주세요");
                    return;
                }
                if (login_pass.length() < 6) {
                    login_pass.setError("6자 이상의 비밀번호가 필요합니다.");
                    return;
                } else {
                    checkLogin();
                }
            }
        });
    }
    private void checkLogin(){
        firebaseAuth.signInWithEmailAndPassword(login_email.getText().toString(), login_pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            pd.dismiss();
                            Intent i = new Intent(AccountLogin.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            Toast.makeText(AccountLogin.this, "로그인 되었습니다.",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
                } else {
                    pd.dismiss();
                    Toast.makeText(AccountLogin.this, "아이디와 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}