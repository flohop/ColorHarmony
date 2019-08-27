package com.example.colorharmony;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class FavoritesLoadedEvent {

    Cursor colorCursor;

    public FavoritesLoadedEvent(Cursor myColorCursor){
        this.colorCursor = myColorCursor;
    }

    public Cursor getCursor() {
        return (this.colorCursor);
    }

}
