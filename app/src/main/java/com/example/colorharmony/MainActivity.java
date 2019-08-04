package com.example.colorharmony;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    ImageButton cameraImageButton;
    ImageButton galleryImageButton;
    private static final int REQUEST_STORAGE = 123;
    private static final int REQUEST_IMAGE_CAPTURE = 1888;
    public static final String IMAGE_URI_CODE = "IMAGE_URI";
    public static final String IMAGE_NAME = "CAPTURED_IMAGE";
    File photo;
    private java.net.URI mImageUri;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraImageButton = (ImageButton) findViewById(R.id.cameraImageButton);
        galleryImageButton = (ImageButton) findViewById(R.id.galleryImageButton);

        cameraImageButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //open the camera(ask for permission) and take a picture for later use

                if(hasPermission(Manifest.permission.CAMERA) && hasPermission(WRITE_EXTERNAL_STORAGE)) {
                    //open the camera

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                        File photoFile = null;
                        try{
                            photoFile = createImageFile();
                        }
                        catch(IOException ioe){
                            //Error occured while creating the file
                        }
                        if(photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                                    "com.example.colorharmony.fileprovider", photoFile);


                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                            Log.d("TESTING1", photoURI.getClass().toString());
                            takePictureIntent.setData(photoURI);
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                        }

                    }

                }
                else{
                    requestPermissions(new String[]{Manifest.permission.CAMERA}
                    ,REQUEST_STORAGE );
                }
            }
        });



        galleryImageButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                //open the gallery and let the user pick an image for later use
            }
        });

    }

    private boolean hasPermission(String perm) {
        return(ContextCompat.checkSelfPermission(this, perm)==
                (PackageManager.PERMISSION_GRANTED));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission,
                                           int[] grantResults) {
        if(hasPermission(Manifest.permission.CAMERA)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int request_code, int result_code, Intent data){

        if((request_code ==REQUEST_IMAGE_CAPTURE) && result_code== RESULT_OK) {

            android.net.Uri imgUri = data.getData();
            Log.d("Florian", imgUri.toString());
            Intent colorPickerIntent = new Intent(MainActivity.this, ColorPickerActivity.class);
            colorPickerIntent.setData(imgUri);
            startActivity(colorPickerIntent);


        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);

        return(super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.share:
                //let the user share the app
                return (true);

            case R.id.support_me:
                //let the use  buy me a coffee
                return (true);

            case R.id.settings:
                //let the user alter the settings
                return (true);
        }

        return (super.onOptionsItemSelected(item));

    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);


        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
