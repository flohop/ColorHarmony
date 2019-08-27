package com.example.colorharmony;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyCursorAdapter extends CursorAdapter{

    private LayoutInflater cursorInflater;

    public MyCursorAdapter(Context context, Cursor cursor, int flags){
        super(context, cursor, flags);

        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public void bindView(View view, Context context, Cursor cursor) {

        //get the elements from the xml
        TextView textViewTitle = (TextView) view.findViewById(R.id.favorite_name);
        ImageView imageViewIcon = (ImageView) view.findViewById(R.id.favorite_type_image);
        View viewColor1 = (View) view.findViewById(R.id.favorite_color_1);
        View viewColor2 = (View) view.findViewById(R.id.favorite_color_2);
        View viewColor3 = (View) view.findViewById(R.id.favorite_color_3);
        View viewColor4 = (View) view.findViewById(R.id.favorite_color_4);

        //get the values from the cursor
        String title = cursor.getString(cursor.getColumnIndex(SQLiteHelper.TITLE));
        String type = cursor.getString(cursor.getColumnIndex(SQLiteHelper.HARMONY_TYPE));
        String hex1 = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLOR_VALUE_1));
        String hex2 = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLOR_VALUE_2));
        String hex3 = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLOR_VALUE_3));
        String hex4 = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLOR_VALUE_4));

        //assign the values to the elements
        textViewTitle.setText(title);

        //search for the icon
        switch(type){
            case "Complementary":
                imageViewIcon.setBackgroundResource(R.drawable.ic_complementary_harmony);
            case "Monochromatic":
                imageViewIcon.setBackgroundResource(R.drawable.ic_monochromatic_harmony);
            case "Analogous":
                imageViewIcon.setBackgroundResource(R.drawable.ic_analogous_harmony);
            case "Split Complementary":
                imageViewIcon.setBackgroundResource(R.drawable.ic_split_complementary_harmony);
            case "Triadic":
                imageViewIcon.setBackgroundResource(R.drawable.ic_triadic_harmony);
            case "Tetradic":
                imageViewIcon.setBackgroundResource(R.drawable.ic_tetradic_harmony);
        }

        //set the colors(handle no color available later)

        viewColor1.setBackgroundColor(Color.parseColor("#" + hex1));
        viewColor2.setBackgroundColor(Color.parseColor("#" + hex2));
        viewColor3.setBackgroundColor(Color.parseColor("#" + hex3));
        viewColor4.setBackgroundColor(Color.parseColor("#" + hex4));
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent){
        return cursorInflater.inflate(R.layout.favorites_row, parent, false);

    }


}
