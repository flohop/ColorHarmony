package com.example.colorharmony;

import android.database.Cursor;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class UpdatedEvent {
    private Cursor updatedCursor;


    public UpdatedEvent(Cursor updatedCursor){
        this.updatedCursor = updatedCursor;



    }

    public Cursor getUpdatedCursor() {
        return this.updatedCursor;
    }



}
