package com.example.colorharmony;

import android.graphics.drawable.Drawable;

public class TacticItem {

    private String mTacticName;
    private int mTacticImage;
    private String mTacticInfoText;
    private int mAlertImage;

    public TacticItem(String tacticName, int tacticImage, String infoText) {

        mTacticName = tacticName;
        mTacticImage = tacticImage;
        mTacticInfoText = infoText;
    }

    public String getTacticName(){
        return mTacticName;
    }

    public int getTacticImage() {
        return mTacticImage;
    }

    public int getAlertImageCode() {
        return mAlertImage;
    }

    public void setAlertImageCode(int resId){
        mAlertImage = resId;
    }

    public String getInfoText() {
        return mTacticInfoText;
    }
}
