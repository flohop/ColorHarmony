package com.florian.colorharmony_theory_stragety;

public class FavoriteColor {

    public String mtitle;
    public String mdescription;
    public String mharmonyType;

    public String mhex1;
    public String mhex2;
    public String mhex3;
    public String mhex4;
    public int id;

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

    public FavoriteColor(String title, String harmonyType, String description, int favorite,
                         String hex1, String hex2, String hex3
                         ){
        this.mtitle = title;
        this.mharmonyType = harmonyType;
        this.mdescription = description;
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

    //constructors with id
    public FavoriteColor(int id, String title, String harmonyType, String description, int favorite,
                         String hex1, String hex2){
        this.mtitle = title;
        this.mharmonyType = harmonyType;
        this.mdescription = description;
        this.mfavorite = favorite;

        this.mhex1 = hex1;
        this.mhex2 = hex2;
        this.id = id;
    }

    public FavoriteColor(int id, String title, String harmonyType, String description, int favorite,
                         String hex1, String hex2, String hex3){
        this.mtitle = title;
        this.mharmonyType = harmonyType;
        this.mdescription = description;
        this.mfavorite = favorite;

        this.mhex1 = hex1;
        this.mhex2 = hex2;
        this.mhex3 = hex3;
        this.id = id;
    }

    public FavoriteColor(int id, String title, String harmonyType, String description, int favorite,
                         String hex1, String hex2, String hex3, String hex4){
        this.mtitle = title;
        this.mharmonyType = harmonyType;
        this.mdescription = description;
        this.mfavorite = favorite;

        this.mhex1 = hex1;
        this.mhex2 = hex2;
        this.mhex3 = hex3;
        this.mhex4 = hex4;
        this.id = id;
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

    public int getId() { return id;}

    public int getHarmonyTypeResource() {

        int returnImage = R.drawable.ic_error_black_24dp;

        switch (mharmonyType) {
            case ("Complementary"):
                returnImage = R.drawable.ic_complementary_harmony;
                break;
            case ("Monochromatic"):
                returnImage = R.drawable.ic_monochromatic_harmony;
                break;
            case ("Analogous"):
                returnImage = R.drawable.ic_analogous_harmony;
                break;
            case ("Split Complementary"):
                returnImage = R.drawable.ic_split_complementary_harmony;
                break;
            case ("Triadic"):
                returnImage = R.drawable.ic_triadic_harmony;
                break;
            case ("Tetradic"):
                returnImage = R.drawable.ic_tetradic_harmony;
                break;

        }
        return returnImage;
    }
    public String getHex1() {
        return this.mhex1;
    }

    public String getHex2() {
        return this.mhex2;
    }

    public String getHex3() {
        return this.mhex3;
    }

    public String getHex4() {
        return this.mhex4;
    }

    public int getFavorite() {
        return mfavorite;
    }

    public static FavoriteColor createFavoriteColorInstance(String name, String mdescription, String type, String hHex1,
                                                            String hHex2, String hHex3, String hHex4){
        FavoriteColor myFavColor;
        switch (type){
            case "Complementary":
                myFavColor = new FavoriteColor(name, type, 0, hHex1, hHex2);
                break;

            case "Analogous":
            case "Split Complementary":
            case "Triadic":
                myFavColor = new FavoriteColor(name, type, 0, hHex1, hHex2, hHex3);
                break;
            case "Monochromatic":
            case "Tetradic":
                myFavColor = new FavoriteColor(name, type, 0, hHex1, hHex2, hHex3, hHex4);
                break;
             default:
                myFavColor = null;
        }

        return myFavColor;

    }

    public void setMtitle(String mtitle) {
        this.mtitle = mtitle;
    }

    public void setMdescription(String mdescription) {
        this.mdescription = mdescription;
    }

    public void setMharmonyType(String mharmonyType) {
        this.mharmonyType = mharmonyType;
    }

    public void setMhex1(String mhex1) {
        this.mhex1 = mhex1;
    }

    public void setMhex2(String mhex2) {
        this.mhex2 = mhex2;
    }

    public void setMhex3(String mhex3) {
        this.mhex3 = mhex3;
    }

    public void setMhex4(String mhex4) {
        this.mhex4 = mhex4;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMfavorite(int mfavorite) {
        this.mfavorite = mfavorite;
    }
}


