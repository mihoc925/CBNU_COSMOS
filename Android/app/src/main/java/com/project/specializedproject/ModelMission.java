package com.project.specializedproject;

import java.util.ArrayList;

public class ModelMission {

    private ArrayList<Integer> content1;
    private ArrayList<Integer> content2;
    private ArrayList<Integer> content3;
    private ArrayList<Integer> content4;
    private ArrayList<Integer> content5;

    public ArrayList<Integer> getContent1() {
        return content1;
    }
    public void setContent1(ArrayList<Integer> content1) {
        this.content1 = content1;
    }
    public ArrayList<Integer> getContent2() {
        return content2;
    }
    public void setContent2(ArrayList<Integer> content2) {
        this.content2 = content2;
    }
    public ArrayList<Integer> getContent3() {
        return content3;
    }
    public void setContent3(ArrayList<Integer> content3) {
        this.content3 = content3;
    }
    public ArrayList<Integer> getContent4() {
        return content4;
    }
    public void setContent4(ArrayList<Integer> content4) {
        this.content4 = content4;
    }
    public ArrayList<Integer> getContent5() {
        return content5;
    }
    public void setContent5(ArrayList<Integer> content5) {
        this.content5 = content5;
    }

    public ModelMission(ArrayList<Integer> content1, ArrayList<Integer> content2, ArrayList<Integer> content3, ArrayList<Integer> content4, ArrayList<Integer> content5) {
        this.content1 = content1;
        this.content2 = content2;
        this.content3 = content3;
        this.content4 = content4;
        this.content5 = content5;
    }
    public ModelMission() {}
}
