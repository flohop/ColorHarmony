package com.example.colorharmony;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ColorPickerActivity  extends Activity {
    public ImageView imageView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.color_picker);

        imageView = (ImageView) findViewById(R.id.imageView2);

        Bundle ex = getIntent().getExtras();
        Bitmap bmp = ex.getParcelable("image");

        if(bmp != null){

        }

    }

    private  void saveProfilePicture(File tempFile) {

        String filename = "my_profile_picture.jpg";
        final File imageFile = new File(getExternalFilesDir("images"), filename);

        if (imageFile.exists()) {
            imageFile.delete();
        }

        String tempPath = tempFile.getAbsolutePath();
        try {
            Bitmap bitmapOrg = createOriginalBitmap(tempPath);


            FileOutputStream out = null;
            try {
                out = new FileOutputStream(imageFile);
                bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (final Exception e) {
            } finally {
                try {
                    if (out != null) {
                        out.close();

                        imageView.setImageBitmap(bitmapOrg);
                    }
                } catch (final IOException e) {
                }
            }
        } catch (final Exception e) {
            return;
        }
    }


    private Bitmap createOriginalBitmap(final String imagePath){
        final Bitmap bitmapOrg;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bitmapOrg = BitmapFactory.decodeFile(imagePath);
        } else {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inDither = true;
            bitmapOrg = BitmapFactory.decodeFile(imagePath, options);
        }
        return bitmapOrg;
    }


}

