package com.example.colorharmony;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;




public class FavoritesActivity extends FragmentActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
            Log.d("My fragments", "the fragment is being added");

            FavoriteColorsFragment f = new FavoriteColorsFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, f).commit();

        }
        }

        public void closeFavorites(){
            finish();
    }

}
