package com.project.specializedproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;


public class MainActivity extends AppCompatActivity {

    private final static int ID_list = 1;
    private final static int ID_home = 2;
    private final static int ID_user = 3;

    boolean writeStoragePermission;
    boolean readStoragePermission;
    boolean cameraPermission;

    MeowBottomNavigation Bottomnavigation;
    Fragment selcect_fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPermission(); // 퍼미션 설정
        setBottomNavigation();
    }

    private void setPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            writeStoragePermission = true;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                readStoragePermission = true;
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    cameraPermission = true;
                }
            }
        }
        if (!writeStoragePermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
            if (!readStoragePermission) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 20);
                if (!cameraPermission) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 20);
                }
            }
        }
    }

    private void setBottomNavigation() {
        Bottomnavigation = (MeowBottomNavigation) findViewById(R.id.bottom_nav);        // 바텀바

        Bottomnavigation.add(new MeowBottomNavigation.Model(1, R.drawable.bottom1));
        Bottomnavigation.add(new MeowBottomNavigation.Model(2, R.drawable.bottom2));
        Bottomnavigation.add(new MeowBottomNavigation.Model(3, R.drawable.bottom3));

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragList()).commit();

        //바텀바 중복클릭 금지
        Bottomnavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                Bottomnavigation.setEnabled(false);
            }
        });
        Bottomnavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {
                Bottomnavigation.setEnabled(false);
            }
        });

        Bottomnavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                switch (item.getId()) {
                    case ID_list:
                        selcect_fragment = new FragList();
                        break;
                    case ID_home:
                        selcect_fragment = new FragHome();
                        break;
                    case ID_user:
                        selcect_fragment = new FragUser();
                        break;
                    default:
                        return;
                }
                if (selcect_fragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selcect_fragment).commit();
                }
            }
        });

//        Bottomnavigation.show(1,true); // 기본 홈으로 설정
    }
}