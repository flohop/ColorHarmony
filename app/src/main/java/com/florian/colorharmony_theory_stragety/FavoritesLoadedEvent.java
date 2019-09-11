package com.florian.colorharmony_theory_stragety;

import android.database.Cursor;

public class FavoritesLoadedEvent {

    Cursor colorCursor;

    public FavoritesLoadedEvent(Cursor myColorCursor){
        this.colorCursor = myColorCursor;
    }

    public Cursor getCursor() {
        return (this.colorCursor);
    }

}
