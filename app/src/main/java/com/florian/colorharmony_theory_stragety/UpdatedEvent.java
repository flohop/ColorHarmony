package com.florian.colorharmony_theory_stragety;

import android.database.Cursor;

public class UpdatedEvent {
    private Cursor updatedCursor;


    public UpdatedEvent(Cursor updatedCursor){
        this.updatedCursor = updatedCursor;



    }

    public Cursor getUpdatedCursor() {
        return this.updatedCursor;
    }



}
