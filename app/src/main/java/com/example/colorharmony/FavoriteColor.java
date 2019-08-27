package com.example.colorharmony;

public class FavoriteColor {

    public String mtitle;
    public String mdescription;
    public String mharmonyType;

    public String mhex1;
    public String mhex2;
    public String mhex3;
    public String mhex4;

    public int mfavorite; //0 = not favorite, 1= favorite


    //constructors without description
    public FavoriteColor(String title, String harmonyType, int favorite,
                         String hex1, String hex2, String hex3, String hex4){
        this.mtitle = title;
        this.mharmonyType = harmonyType;
        this.mfavorite = favorite;

        this.mhex1 = hex1;
        this.mhex2 = hex2;
        this.mhex3 = hex3;
        this.mhex4 = hex4;
    }

    public FavoriteColor(String title, String harmonyType, int favorite,
                         String hex1, String hex2, String hex3){
        this.mtitle = title;
        this.mharmonyType = harmonyType;
        this.mfavorite = favorite;

        this.mhex1 = hex1;
        this.mhex2 = hex2;
        this.mhex3 = hex3;

    }

    public FavoriteColor(String title, String harmonyType, int favorite,
                         String hex1, String hex2){
        this.mtitle = title;
        this.mharmonyType = harmonyType;
        this.mfavorite = favorite;

        this.mhex1 = hex1;
        this.mhex2 = hex2;
    }


    // constructors with description
    public FavoriteColor(String title, String harmonyType, String description, int favorite,
                         String hex1, String hex2, String hex3, String hex4){
        this.mtitle = title;
        this.mharmonyType = harmonyType;
        this.mdescription = description;
        this.mfavorite = favorite;

        this.mhex1 = hex1;
        this.mhex2 = hex2;
        this.mhex3 = hex3;
        this.mhex4 = hex4;
    }

    public FavoriteColor(String title, String harmonyType, String descrition, int favorite,
                         String hex1, String hex2, String hex3
                         ){
        this.mtitle = title;
        this.mharmonyType = harmonyType;
        this.mdescription = descrition;
        this.mfavorite = favorite;

        this.mhex1 = hex1;
        this.mhex2 = hex2;
        this.mhex3 = hex3;

    }

    public FavoriteColor(String title, String harmonyType, String description, int favorite,
                         String hex1, String hex2){
        this.mtitle = title;
        this.mharmonyType = harmonyType;
        this.mdescription = description;
        this.mfavorite = favorite;

        this.mhex1 = hex1;
        this.mhex2 = hex2;
    }

    public FavoriteColor(String title, String type, String hex1, String hex2, String hex3, String hex4){
        this.mtitle = title;
        this.mharmonyType = type;
        this.mhex1 = hex1;
        this.mhex2 = hex2;
        this.mhex3 = hex3;
        this.mhex4 = hex4;
    }

    public String getTitle() {
        return mtitle;
    }

    public String getDescription() {
        return mdescription;
    }

    public String getHarmonyType() {
        return mharmonyType;
    }

    public String getHex1() {
        return mhex1;
    }

    public String getHex2() {
        return mhex2;
    }

    public String getMhex3() {
        return mhex3;
    }

    public String getHex4() {
        return mhex4;
    }

    public int getFavorite() {
        return mfavorite;
    }
}


