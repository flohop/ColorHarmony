package com.example.colorharmony;

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
        return mhex1;
    }

    public String getHex2() {
        return mhex2;
    }

    public String getHex3() {
        return mhex3;
    }

    public String getHex4() {
        return mhex4;
    }

    public int getFavorite() {
        return mfavorite;
    }
}


