package com.example.colorharmony;

import java.util.ArrayList;

public class ReturnedEvent {
    public ArrayList<FavoriteColor> newColors;

    public ReturnedEvent(ArrayList<FavoriteColor> newC){
        this.newColors = newC;
    }

    public ArrayList<FavoriteColor> getNewColors(){
        return this.newColors;
    }
}
