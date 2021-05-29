package com.project.specializedproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

public class FragHome extends Fragment {
    private String TAG = "FragHome";
    Context mContext;

    LinearLayout home_layout;
    ImageView home_defaultImg;
    Button home_test;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_home, container, false);
        mContext = view.getContext();
        setView(view);

        home_defaultImg.setVisibility(View.VISIBLE);
        home_layout.setVisibility(View.VISIBLE);


        return view;
    }

    private void setView(View view){
        home_layout = view.findViewById(R.id.home_layout);
        home_defaultImg = view.findViewById(R.id.home_defaultImg);
        home_test = view.findViewById(R.id.home_test);
        home_test.setOnClickListener(this::onCliCk);
    }

    public void onCliCk(View view){
        if(view.getId() == R.id.home_test){

            Intent intent = new Intent(getContext(), ClassifierActivity.class);
            intent.putExtra("state",  "write");
            intent.putExtra("content",  0);
            startActivity(intent);
        }
    }

}
