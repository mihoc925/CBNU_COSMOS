package com.project.specializedproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;

public class Dialog_Filter extends Dialog {
    String TAG = "Dialog_Filter";
    ImageButton filter_x;
    Spinner filter_list;
    CheckBox filter_check;
    boolean reverse = false;
    ArrayList spinnerList = new ArrayList<>();

    private FilterListener filterListener;
    public interface FilterListener {
        void clickFunction(String a, boolean b);
    }

    public Dialog_Filter(@NonNull Context context, FilterListener filterListener) {
        super(context);
        this.filterListener = filterListener;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_dialog_filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        filter_list = findViewById(R.id.filter_list);
        filter_x = findViewById(R.id.filter_x);
        filter_check = findViewById(R.id.filter_check);
        filter_x.setOnClickListener(this::onClick);
        filter_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reverse = true;
            }
        });
        setSpinner();
    }

    private void setSpinner(){
        spinnerList.add("선택하세요");
        spinnerList.add("최신순");
        spinnerList.add("거리순");
        spinnerList.add("시간순");
        spinnerList.add("가이드순");

        ArrayAdapter arrAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item, spinnerList);
        filter_list.setAdapter(arrAdapter);
        filter_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterListener.clickFunction((String) spinnerList.get(position), reverse);
                if(!(spinnerList.get(position)).equals("선택하세요")){
                    dismiss();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void onClick(View view){
        if(view.getId() == R.id.filter_x){
            dismiss();
        }
    }
}