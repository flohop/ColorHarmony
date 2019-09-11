package com.florian.colorharmony_theory_stragety;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;


public class FavoritesActivity extends FragmentActivity {
    FavoriteColorsFragment f;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
            f = new FavoriteColorsFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, f).commit();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
