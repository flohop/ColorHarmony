package com.example.colorharmony;

public class FavoritesUpdatedEvent {

    public int position;
    public MyCursorAdapter.ViewHolder viewHolder;
    public FavoritesUpdatedEvent(int position){
        this.position = position;
    }

    public int getPosition(){
        return this.position;
    }


}
