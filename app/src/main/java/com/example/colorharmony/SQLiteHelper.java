package com.example.colorharmony;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Process;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "color_table.db";
    private static final int SCHEMA_VERSION = 1;
    static final String ID = "_id";
    static final String TITLE = "name";
    static final String DESCRIPTION ="description";
    static final String HARMONY_TYPE = "type"; // Complementary, Monochromatic, Analogous,
                                                // Split Complementary, Triadic, Tetradic
    static final String COLOR_VALUE_1 = "hex1";
    static final String COLOR_VALUE_2 = "hex2";
    static final String COLOR_VALUE_3 = "hex3";
    static final String COLOR_VALUE_4 = "hex4";
    static final String FAVORITE = "Favorite"; // 0 = not favorite, 1=favorite
    static final String TABLE = "saved_colors";

    private ArrayList<FavoriteColor> favoriteColorList;
    private static SQLiteHelper singleton = null;

    private Cursor c;

    synchronized static SQLiteHelper getInstance(Context ctxt){
        if(singleton == null){
            singleton = new SQLiteHelper(ctxt.getApplicationContext());
        }
        return (singleton);
    }
    private SQLiteHelper(Context ctxt) {
        super(ctxt, DATABASE_NAME, null, SCHEMA_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE saved_colors(_id INTEGER PRIMARY KEY AUTOINCREMENT ,name TEXT, description TEXT, type TEXT NOT NULL, hex1 TEXT NOT NULL, hex2 TEXT NOT NULL, hex3 TEXT, hex4 TEXT," +
                "favorite Integer);");
        ContentValues cv = new ContentValues();

        cv.put(TITLE, "Classic Tetradic");
        cv.put(DESCRIPTION, "I use this for designing clothes");
        cv.put(HARMONY_TYPE, "Tetradic");
        cv.put(COLOR_VALUE_1, "32ffc2");
        cv.put(COLOR_VALUE_2, "ff5832");
        cv.put(COLOR_VALUE_3, "ffbe32");
        cv.put(COLOR_VALUE_4, "7a32ff");
        cv.put(FAVORITE, 1);
        db.insert(TABLE, TITLE, cv);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new RuntimeException("How did i get here?");

    }

    void loadFavorites() {
        LoadThread myThread = new LoadThread();
        myThread.start();
    }
    void updateFavorites(String name, String description, String type, String h1, String h2) {
        new UpdateThread(name, description, type, h1, h2).start();
    }

    void updateFavorites(String name, String description, String type, String h1, String h2, String h3) {
        new UpdateThread(name, description, type, h1, h2, h3).start();
    }
    void updateFavorites(String name, String description, String type, String h1, String h2, String h3, String h4) {
        new UpdateThread(name, description, type, h1, h2, h3, h4).start();
    }

    void removeFavorite(int id) {
        new RemoveThread(id).start();
    }

    void updateFavoritesValues(String name, String description, int id, int position){
        new UpdateFavoritesThread(name, description, id, position).start();
    }

    Cursor getEmptyCursor(){
        return (new LoadThread().getEmptyCursor());
    }

    private class LoadThread extends Thread {
        LoadThread(){
            super();
        }
        @Override
        public void run(){
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);


            SQLiteDatabase database = getReadableDatabase();
            if(!database.isOpen()){
            }
            c = database.rawQuery("SELECT* FROM saved_colors", null);
            if(c.getCount() > 0) {
                EventBus.getDefault().post(new FavoritesLoadedEvent(c));
            }
            else{
            }
        }
        public Cursor getEmptyCursor() {
            SQLiteDatabase database = getReadableDatabase();
            c = database.rawQuery("SELECT* FROM saved_Colors", null);
            return c;
        }

    }

    private class UpdateThread extends Thread {
        private String fav_name;
        private String fav_description;
        private String fav_type;
        private String hex1;
        private String hex2;
        private String hex3;
        private String hex4;

        private int favorite;

        public UpdateThread(String name, String description, String type, String h1, String h2){
            //Constructor for complimentary
            super();
            this.fav_name = name;
            this.fav_description = description;
            this.fav_type = type;
            this.hex1 = h1;
            this.hex2 = h2;
            this.hex3 = "not_given";
            this.hex4 = "not_given";
            this.favorite = 0;
        }

        public UpdateThread(String name, String description, String type, String h1, String h2, String h3){
            //Constructor for Triadic analogous and split complimentary
            super();
            this.fav_name = name;
            this.fav_description = description;
            this.fav_type = type;
            this.hex1 = h1;
            this.hex2 = h2;
            this.hex3 = h3;
            this.hex4 = "not_given";
            this.favorite = 0;
        }

        public UpdateThread(String name, String description, String type, String h1, String h2, String h3, String h4){
            //Constructor for Monochromatic and Tetradic
            super();
            this.fav_name = name;
            this.fav_description = description;
            this.fav_type = type;
            this.hex1 = h1;
            this.hex2 = h2;
            this.hex3 = h3;
            this.hex4 = h4;
            this.favorite = 0;
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            String[] args = {this.fav_name, this.fav_description, this.fav_type,
                    this.hex1, this.hex2, this.hex3, this.hex4, String.valueOf(this.favorite)};
            getWritableDatabase().execSQL("INSERT INTO saved_colors (name, description, type," +
                    " hex1, hex2, hex3, hex4, favorite) VALUES(?, ?, ?, ?, ?, ?, ?, ?)", args);

            Cursor updatedCursor = getReadableDatabase().rawQuery("SELECT* FROM saved_colors", null);

            updatedCursor.moveToLast();
            ArrayList<FavoriteColor> newFavColors = new ArrayList<>();
            while(updatedCursor.moveToPrevious()) {
                String title = updatedCursor.getString(updatedCursor.getColumnIndex(SQLiteHelper.TITLE));
                String description = updatedCursor.getString(updatedCursor.getColumnIndex(SQLiteHelper.DESCRIPTION));
                String type = updatedCursor.getString(updatedCursor.getColumnIndex(SQLiteHelper.HARMONY_TYPE));
                String hex1 = updatedCursor.getString(updatedCursor.getColumnIndex(SQLiteHelper.COLOR_VALUE_1));
                String hex2 = updatedCursor.getString(updatedCursor.getColumnIndex(SQLiteHelper.COLOR_VALUE_2));
                String hex3 = updatedCursor.getColumnName(updatedCursor.getColumnIndex(SQLiteHelper.COLOR_VALUE_3));
                String hex4 = updatedCursor.getColumnName(updatedCursor.getColumnIndex(SQLiteHelper.COLOR_VALUE_4));

                FavoriteColor tempColor = FavoriteColor.createFavoriteColorInstance(title, description, type, hex1, hex2, hex3, hex4);

                newFavColors.add(tempColor);
                EventBus.getDefault().post(new UpdatedEvent(updatedCursor));
            }
        }

    }

    public class UpdateFavoritesThread extends Thread{
        private String name;
        private String description;
        private int id;
        private int position;

        UpdateFavoritesThread(String n, String descr, int colorId, int position){
            super();
            this.name = n;
            this.description = descr;
            this.id = colorId;
            this.position = position;
        }

        @Override
        public void run(){
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            ContentValues cv = new ContentValues();
            cv.put(SQLiteHelper.TITLE, this.name);
            cv.put(SQLiteHelper.DESCRIPTION, this.description);
            getWritableDatabase().update(TABLE, cv,"_id=" + this.id, null);
            Cursor updatedCursor = getReadableDatabase().rawQuery("SELECT* FROM saved_colors", null);
            EventBus.getDefault().post(new FavoritesUpdatedEvent(this.position, updatedCursor));
        }
    }

    public class RemoveThread extends Thread{
        int id;

        public RemoveThread(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            //finished
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            getWritableDatabase().delete("saved_colors", "_id" + "=" + this.id, null);
            Cursor updatedCursor = getReadableDatabase().rawQuery("SELECT* FROM saved_colors", null);
           /* updatedCursor.moveToFirst();
            ArrayList<FavoriteColor> newFavColors = new ArrayList<>();
            while(updatedCursor.moveToNext()) {
                String title = updatedCursor.getString(updatedCursor.getColumnIndex(SQLiteHelper.TITLE));
                String description = updatedCursor.getString(updatedCursor.getColumnIndex(SQLiteHelper.DESCRIPTION));
                String type = updatedCursor.getString(updatedCursor.getColumnIndex(SQLiteHelper.HARMONY_TYPE));
                String hex1 = updatedCursor.getString(updatedCursor.getColumnIndex(SQLiteHelper.COLOR_VALUE_1));
                String hex2 = updatedCursor.getString(updatedCursor.getColumnIndex(SQLiteHelper.COLOR_VALUE_2));
                String hex3 = updatedCursor.getColumnName(updatedCursor.getColumnIndex(SQLiteHelper.COLOR_VALUE_3));
                String hex4 = updatedCursor.getColumnName(updatedCursor.getColumnIndex(SQLiteHelper.COLOR_VALUE_4));

                FavoriteColor tempColor = FavoriteColor.createFavoriteColorInstance(title, description, type, hex1, hex2, hex3, hex4);

                newFavColors.add(tempColor);
            }*/

            EventBus.getDefault().post(new UpdatedEvent(updatedCursor));

        }
    }




}
