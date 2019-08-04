package com.example.colorharmony;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    ImageButton cameraImageButton;
    ImageButton galleryImageButton;
    private static final int REQUEST_STORAGE = 123;
    private static final int REQUEST_IMAGE_CAPTURE = 1888;
    public static final String IMAGE_URI_CODE = "IMAGE_URI";
    public static final String IMAGE_NAME = "CAPTURED_IMAGE";

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
        return(ContextCompat.checkSelfPermission(MainActivity.this, perm)==
                (PackageManager.PERMISSION_GRANTED));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission,
                                           int[] grantResults) {
        if(hasPermission(Manifest.permission.CAMERA)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startCamera();
            }
        }
    }

    @Override
    protected void onActivityResult(int request_code, int result_code, Intent data){

        if((request_code ==REQUEST_IMAGE_CAPTURE) && result_code== RESULT_OK) {

            saveProfilePicture(tempProfileImageFile);

        }
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

                        Intent intent = new Intent(MainActivity.this, ColorPickerActivity.class);
                        intent.putExtra("image", filename);

                        startActivity(intent);
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


}
