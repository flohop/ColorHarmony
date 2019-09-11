package com.florian.colorharmony_theory_stragety;

import android.database.Cursor;

public class FavoritesUpdatedEvent {

    public int position;
    public Cursor myCursor;
    public FavoritesUpdatedEvent(int position, Cursor c){
        this.position = position;
        this.myCursor = c;
    }

    public int getPosition(){
        return this.position;
    }

    public Cursor getCursor() {
        return this.myCursor;
    }


}
