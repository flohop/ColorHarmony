package com.florian.colorharmony_theory_stragety;


public class TacticItem {

    private String mTacticName;
    private int mTacticImage;
    private String mTacticInfoText;
    private int mAlertImage;
    private int mShowImageResource;

    public TacticItem(String tacticName, int tacticImage, String infoText, int showImageResource) {

        mTacticName = tacticName;
        mTacticImage = tacticImage;
        mTacticInfoText = infoText;
        mShowImageResource = showImageResource;
    }

    public String getTacticName(){
        return mTacticName;
    }

    public int getTacticImage() {
        return mTacticImage;
    }

    public int getShowImageResource(){
        return mShowImageResource;
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
