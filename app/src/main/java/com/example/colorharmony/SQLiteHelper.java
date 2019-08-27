package com.example.colorharmony;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Process;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "color_table.db";
    private static final int SCHEMA_VERSION = 1;
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
        db.execSQL("CREATE TABLE saved_colors(_id INTEGER PRIMARY KEY AUTOINCREMENT ,name TEXT NOT NULL, description TEXT, type TEXT NOT NULL, hex1 TEXT NOT NULL, hex2 TEXT NOT NULL, hex3 TEXT, hex4 TEXT," +
                "favorite Integer);");
        Log.d("DATABASE", "Created SQLite Database");
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

        Log.d("DATABASE", "Inserted values in the database");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new RuntimeException("How did i get here?");

    }

    void loadFavorites() {
        LoadThread myThread = new LoadThread();
        myThread.start();
    }

    private class LoadThread extends Thread {


        LoadThread(){
            super();
        }
        @Override
        public void run(){
            Log.d("BACKGROUND", "run() is being executed");
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);


            SQLiteDatabase database = getReadableDatabase();
            if(!database.isOpen()){
                Log.d("BACKGROUND",  "Db is classed");
            }
            c = database.rawQuery("SELECT  rowid _id, name, type, hex1, hex2, hex3, hex4 FROM saved_colors", null);
            Log.d("BACKGROUND", "Got readable database cursor");
            if(c.getCount() > 0) {
                EventBus.getDefault().post(new FavoritesLoadedEvent(c));
            }
            else{
                Log.d("BACKGROUND", "no elements where found");
            }
        }
    }


}