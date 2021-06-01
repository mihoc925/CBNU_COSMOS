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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;

public class AccountRegit extends AppCompatActivity {
    private String TAG = "AccountRegit";
    ProgressDialog pd;

    EditText regit_email, regit_nick, regit_phone, regit_pass, regit_repass;
    Button regit_loginBtn, regit_Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_regit);

        pd = new ProgressDialog(AccountRegit.this);
        pd.setMessage("잠시만 기다려주세요");

        // 동영상 설정
        VideoView vd = findViewById(R.id.regit_vd);
        vd.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.travel));
        vd.start();
        vd.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true); // 동영상 무한 반복. 반복을 원치 않을 경우 false
            }
        });

        regit_email = findViewById(R.id.regit_email);
        regit_nick = findViewById(R.id.regit_nick);
        regit_phone = findViewById(R.id.regit_phone);
        regit_pass = findViewById(R.id.regit_pass);
        regit_repass = findViewById(R.id.regit_repass);
        regit_loginBtn = findViewById(R.id.regit_loginBtn);
        regit_Btn = findViewById(R.id.regit_Btn);

        regit_loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AccountLogin.class));
                finish();
            }
        });

        regit_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });
    }

    private void validation(){
        boolean isValidation = true;
        if (TextUtils.isEmpty(regit_email.getText().toString())) {
            regit_email.setError("아이디를 입력해 주세요.");
            isValidation = false;
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(regit_email.getText().toString()).matches()) {
            regit_email.setError("이메일 형식으로 입력해주세요.");
            isValidation = false;
        }
        if (TextUtils.isEmpty(regit_nick.getText().toString())){
            regit_nick.setError("닉네임을 입력해 주세요.");
            isValidation = false;
        }
        if (TextUtils.isEmpty(regit_phone.getText().toString())) {
            regit_phone.setError("휴대폰 번호를 입력해 주세요.");
            isValidation = false;
        }else if (!Pattern.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$", regit_phone.getText().toString())) {
            regit_phone.setError("올바른 휴대폰 번호가 아닙니다.");
            isValidation = false;
        }
        if (TextUtils.isEmpty(regit_pass.getText().toString())) {
            regit_pass.setError("비밀번호를 입력해 주세요.");
            isValidation = false;
        }else if(regit_pass.getText().toString().length() < 6) {
            regit_pass.setError("6자 이상의 비밀번호가 필요합니다.");
            isValidation = false;
        }
        if (TextUtils.isEmpty(regit_repass.getText().toString())) {
            regit_repass.setError("비밀번호 확인을 입력해 주세요.");
            isValidation = false;
        }else if(!regit_repass.getText().toString().equals(regit_pass.getText().toString())) {
            regit_repass.setError("비밀번호를 다시 한번 확인해 주세요.");
            isValidation = false;
        }

        if(isValidation == true) {
            RegisterNow(regit_email.getText().toString(), regit_nick.getText().toString(),
                    regit_phone.getText().toString(), regit_pass.getText().toString());
            pd.show();
        }
    }

    private void RegisterNow(final String emailStr, String nickStr, String phoneStr, String passStr) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fAuth.createUserWithEmailAndPassword(emailStr, passStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserData").child(fAuth.getCurrentUser().getUid());
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("uid", fAuth.getCurrentUser().getUid());
                    map.put("email", emailStr);
                    map.put("nick", nickStr);
                    map.put("phone", phoneStr);
                    map.put("permission", "Traveler");
                    map.put("level", "0");
                    map.put("point", "0");
                    map.put("profileImg", "https://firebasestorage.googleapis.com/v0/b/specializedproject-a3dd0.appspot.com/o/defaultProfile.png?alt=media&token=d5fae5e8-0d93-4dab-9c3c-51866c73dfe4");

                    ref.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                pd.dismiss();
                                Intent i = new Intent(AccountRegit.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                Toast.makeText(AccountRegit.this, "회원가입이 완료되었습니다! :)", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                } else {
                    pd.dismiss();
                    Toast.makeText(AccountRegit.this, "회원가입이 불가합니다." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}