package com.example.colorharmony;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;

public class ColorPickerActivity  extends Activity {
    public ImageView imageView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.color_picker);

        imageView = (ImageView) findViewById(R.id.imageView);

        Uri imgUri = getIntent().getData();
        File imgFile = new File(imgUri.getPath());

        if(imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);

        }



    }
}
