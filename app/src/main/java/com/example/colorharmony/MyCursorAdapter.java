package com.example.colorharmony;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.service.voice.AlwaysOnHotwordDetector;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MyCursorAdapter extends CursorRecyclerViewAdapter<MyCursorAdapter.ViewHolder>{

    private LayoutInflater cursorInflater;
    TextView textViewTitle;
    ImageView imageViewIcon;
    View viewColor1;
    View viewColor2;
    View viewColor3;
    View viewColor4;
    View itemView;
    Cursor cursor;
    Context context;
    ArrayList<Integer> toDeleteItemsPosition;
    int numberOfElements;

    public MyCursorAdapter(Context context, Cursor cursor){
        super(context, cursor);
        this.cursor = cursor;
        this.context = context;
        toDeleteItemsPosition = new ArrayList<>();
        numberOfElements = cursor.getCount();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("onCreateViewHolder", " got called");
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorites_row, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return  vh;
    }

    @Override
    public void onBindViewHolder(MyCursorAdapter.ViewHolder viewHolder, Cursor cursor) {

        Log.d("onBindViewHolder",  "got called");
        //get the values from the cursor
        String title = cursor.getString(cursor.getColumnIndex(SQLiteHelper.TITLE));
        int id = cursor.getInt(0);
        String type = cursor.getString(cursor.getColumnIndex(SQLiteHelper.HARMONY_TYPE));
        String hex1 = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLOR_VALUE_1));
        String hex2 = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLOR_VALUE_2));
        String hex3 = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLOR_VALUE_3));
        String hex4 = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLOR_VALUE_4));

        //assign the values to the elements
        textViewTitle.setText(title);
        viewHolder.itemView.setTag(id);

        //search for the icon
        switch(type){
            case ("Complementary"):
                imageViewIcon.setBackgroundResource(R.drawable.ic_complementary_harmony);
                break;
            case ("Monochromatic"):
                imageViewIcon.setBackgroundResource(R.drawable.ic_monochromatic_harmony);
                break;
            case ("Analogous"):
                imageViewIcon.setBackgroundResource(R.drawable.ic_analogous_harmony);
                break;
            case ("Split Complementary"):
                imageViewIcon.setBackgroundResource(R.drawable.ic_split_complementary_harmony);
                break;
            case ("Triadic"):
                imageViewIcon.setBackgroundResource(R.drawable.ic_triadic_harmony);
                break;
            case ("Tetradic"):
                imageViewIcon.setBackgroundResource(R.drawable.ic_tetradic_harmony);
                break;

        }

        //set the colors(handle no color available later)

        viewColor1.setBackgroundColor(Color.parseColor("#" + hex1));
        viewColor2.setBackgroundColor(Color.parseColor("#" + hex2));
        try{
            viewColor3.setVisibility(View.VISIBLE);
            viewColor3.setBackgroundColor(Color.parseColor("#" + hex3));
        }
        catch(NumberFormatException e){
            viewColor3.setVisibility(View.INVISIBLE);
        }
        try{
            viewColor4.setVisibility(View.VISIBLE);
            viewColor4.setBackgroundColor(Color.parseColor("#" + hex4));
        }
        catch(NumberFormatException e){
            viewColor4.setVisibility(View.INVISIBLE);
        }
    }

    public void removeItem(int position, int id){
        SQLiteHelper.getInstance(context).removeFavorite(id);

        notifyItemRemoved(position);
        notifyItemChanged(position, numberOfElements);

    }
    public void restoreItem(int position, FavoriteColor restoredColor){

        switch(restoredColor.mharmonyType){
            case "Complementary":
                SQLiteHelper.getInstance(context).updateFavorites(restoredColor.getTitle(),
                        restoredColor.getDescription(), restoredColor.getHarmonyType(),
                        restoredColor.getHex1(), restoredColor.getHex2());
            case "Analogous":
            case "Split Complementary":
            case "Triadic":
                SQLiteHelper.getInstance(context).updateFavorites(restoredColor.getTitle(),
                        restoredColor.getDescription(), restoredColor.getHarmonyType(),
                        restoredColor.getHex1(),
                        restoredColor.getHex2(), restoredColor.getHex3());

            case "Monochromatic":
            case "Tetradic":
                SQLiteHelper.getInstance(context).updateFavorites(restoredColor.getTitle(),
                        restoredColor.getDescription(), restoredColor.getHarmonyType(),
                        restoredColor.getHex1(), restoredColor.getHex2(),
                        restoredColor.getHex3(), restoredColor.getHex4());



        }

        notifyItemInserted(position);

    }

    public View newView(Context context, Cursor cursor, ViewGroup parent){
        return cursorInflater.inflate(R.layout.favorites_row, parent, false);

    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        public ViewHolder(View view){
            super(view);
            textViewTitle = (TextView) view.findViewById(R.id.favorite_name);
            imageViewIcon = (ImageView) view.findViewById(R.id.favorite_type_image);
            viewColor1 = (View) view.findViewById(R.id.favorite_color_1);
            viewColor2 = (View) view.findViewById(R.id.favorite_color_2);
            viewColor3 = (View) view.findViewById(R.id.favorite_color_3);
            viewColor4 = (View) view.findViewById(R.id.favorite_color_4);

        }
    }


}