package com.example.colorharmony;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    ImageButton cameraImageButton;
    ImageButton galleryImageButton;
    private static final int REQUEST_STORAGE = 123;
    private static final int REQUEST_IMAGE_CAPTURE = 1888;
    private static final int REQUEST_IMAGE_GALLERY = 1889;
    private static final int RESULT_LOAD_IMAGE = 124;


    String pathToFile;
    private File tempProfileImageFile;

    private final String LOG_TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraImageButton = (ImageButton) findViewById(R.id.cameraImageButton);
        galleryImageButton = (ImageButton) findViewById(R.id.galleryImageButton);



        cameraImageButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //open the camera(ask for permission) and take a picture for later use

                if(hasPermission(Manifest.permission.CAMERA)) {
                    //open the camera

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startCamera();

                    }
                }
                else{
                    requestPermissions(new String[]{Manifest.permission.CAMERA}
                    ,REQUEST_IMAGE_CAPTURE );
                }
            }
        });


        galleryImageButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(hasPermission(READ_EXTERNAL_STORAGE)) {
                    openGallery();
                }
                else{
                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_IMAGE_GALLERY);
                }
            }
        });


    }

    private boolean hasPermission(String perm) {
        return(ContextCompat.checkSelfPermission(MainActivity.this, perm)==
                (PackageManager.PERMISSION_GRANTED));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        if(hasPermission(Manifest.permission.CAMERA) && requestCode ==  REQUEST_IMAGE_CAPTURE) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startCamera();
            }
        }

        if(hasPermission(READ_EXTERNAL_STORAGE )&& requestCode == REQUEST_IMAGE_GALLERY) {

            Log.d(LOG_TAG, "I have the permission");

           openGallery();

        }
    }

    @Override
    protected void onActivityResult(int request_code, int result_code, Intent data){

        Log.d(LOG_TAG, "Got a result from an intent");

        if((request_code ==REQUEST_IMAGE_CAPTURE) && (result_code==RESULT_OK)) {

            sendTemporaryFile(tempProfileImageFile);
        }

        else if((request_code == RESULT_LOAD_IMAGE) && (result_code==RESULT_OK)) {

            Log.d(LOG_TAG, "Hello world, i got a positive result");

            Log.d(LOG_TAG, data.getData().toString());
            Uri selectedImage = data.getData();

            Intent startPickerIntent = new Intent(this, ColorPickerActivity.class);
            startPickerIntent.setData(selectedImage);
            startActivity(startPickerIntent);

        }

        if(result_code == RESULT_OK) {
            Log.d(LOG_TAG, (Integer.toString(request_code)));
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

                //temporary:
                startActivity(new Intent(this, FavoritesActivity.class));
                return (true);

            case R.id.settings:
                startActivity(new Intent(this, EditPreferences.class));
                return (true);
        }

        return (super.onOptionsItemSelected(item));

    }


    // opening camera and setting image methods
    private void startCamera() {
        File tempFile = createTempImageFile();
        if (tempFile != null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if ((intent.resolveActivity(getPackageManager()) != null)) {
                tempProfileImageFile = tempFile;
                Uri photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".profileimage.fileprovider", tempFile);

                List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createTempImageFile() {
        try {
            return File.createTempFile(
                    "TEMP_PROFILE_IMAGE",
                    ".jpg",
                    getExternalFilesDir("images")
            );
        } catch (IOException e) {
            return null;
        }
    }

    private  void sendTemporaryFile(File tempFile) {

        String filename = "my_profile_picture.jpg";
        final File imageFile = new File(getExternalFilesDir("images"), filename);

        if (imageFile.exists()) {
            imageFile.delete();
        }

        Intent intent = new Intent(MainActivity.this, ColorPickerActivity.class);
        intent.putExtra("temporary_file", tempFile);

        startActivity(intent);
    }

    private void openGallery(){

        Intent open_gallery_intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if(open_gallery_intent.resolveActivity(getPackageManager()) != null) {


            open_gallery_intent.setType("image/*");
            startActivityForResult(open_gallery_intent, RESULT_LOAD_IMAGE);
        }
        else{
            Log.d(LOG_TAG, "Oh no");
        }
    }
}
