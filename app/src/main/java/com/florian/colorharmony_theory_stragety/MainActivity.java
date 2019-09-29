package com.florian.colorharmony_theory_stragety;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.wallet.PaymentsClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    ImageButton cameraImageButton;
    ImageButton galleryImageButton;
    TextView cameraText;


    TextView galleryText;
    private static final int REQUEST_STORAGE = 123;
    private static final int REQUEST_IMAGE_CAPTURE = 1888;
    private static final int REQUEST_IMAGE_GALLERY = 1889;
    private static final int RESULT_LOAD_IMAGE = 124;
    private Boolean mApplyNightMode = false;

    private PaymentsClient paymentsClient;
    private ConstraintLayout cLayout;


    String pathToFile;


    private File tempProfileImageFile;
    private AdView mAdView;
    SharedPreferences prefs;
    private Context myContext;

    private int donation_amount;
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    // PayPal user
    private static final String PAYPAL_USER = "edyta.hoppe@gmail.com";
    private static final String PAYPAL_CURRENCY_CODE = "EUR";

    //Google
    private static final String GOOGLE_PUBKEY = "441020815658-394s447pmfj4l2t0qj7ilsos0kuq5rpo.apps.googleusercontent.com";
    private static final String[] GOOGLE_CATALOG = new String[]{"ntpsync.donation.1",
            "ntpsync.donation.2", "ntpsync.donation.5", "ntpsync.donation.10"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myContext = this;

        //show tutorial of app was never opened
        String tutorialKey ="tut_key";
        Boolean firstTime = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(tutorialKey, true);
        if(firstTime) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(tutorialKey, false).apply();
            startActivity(new Intent(this, IntroActivity.class));
        }
        

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("theme_switch", false) == true){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Window windows = this.getWindow();
            windows.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            windows.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            windows.setStatusBarColor(ContextCompat.getColor(this, R.color.dark_theme_blue));
        }
        else{
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Window windows = this.getWindow();
            windows.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            windows.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            windows.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));


        }
        getDelegate().applyDayNight();


        //configure google pay

        //find elements
        cameraImageButton = (ImageButton) findViewById(R.id.cameraImageButton);
        cameraText = (TextView) findViewById(R.id.textView);
        galleryText = (TextView) findViewById(R.id.textView2);
        galleryImageButton = (ImageButton) findViewById(R.id.galleryImageButton);
        cLayout = (ConstraintLayout) findViewById(R.id.activity_main_constraint_layout);

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("theme_switch", false) == true) {
            cameraImageButton.setBackgroundResource(R.drawable.round_button_night);
            galleryImageButton.setBackgroundResource(R.drawable.round_button_night);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.dark_theme_blue)));
        }
        else{
            cameraImageButton.setBackgroundResource(R.drawable.round_button_day);
            galleryImageButton.setBackgroundResource(R.drawable.round_button_day);
        }

        try {
            cameraImageButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    //open the camera(ask for permission) and take a picture for later use

                    if (hasPermission(Manifest.permission.CAMERA)) {
                        //open the camera

                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startCamera();

                        }
                    } else {
                        if(Build.VERSION.SDK_INT >= 23) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}
                                    , REQUEST_IMAGE_CAPTURE);
                        }
                    }


                }
            });
        }
        catch (NullPointerException e){

        }

        try {
            galleryImageButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    if (hasPermission(READ_EXTERNAL_STORAGE)) {
                        openGallery();
                    } else {
                        if(Build.VERSION.SDK_INT >= 23) {
                            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_IMAGE_GALLERY);
                        }
                    }

                }
            });
        }
        catch (NullPointerException e){

        }
        MobileAds.initialize(this,
                getString(R.string.admob_app_id));
        // Find Banner ad
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        // Display Banner ad
        mAdView.loadAd(adRequest);

        //dark theme

        cLayout = findViewById(R.id.activity_main_constraint_layout);




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
           openGallery();

        }
    }

    @Override
    protected void onActivityResult(int request_code, int result_code, Intent data){

        if((request_code ==REQUEST_IMAGE_CAPTURE) && (result_code==RESULT_OK)) {

            sendTemporaryFile(tempProfileImageFile);
        }

        else if((request_code == RESULT_LOAD_IMAGE) && (result_code==RESULT_OK)) {
            Uri selectedImage = data.getData();

            Intent startPickerIntent = new Intent(this, ColorPickerActivity.class);
            startPickerIntent.setData(selectedImage);
            startActivity(startPickerIntent);

        }



    // removed for as long as i cant figure out how to integrate google pay
      /*  else if(result_code == RESULT_OK) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag("donationsFragment");
            if(fragment != null){
                fragment.onActivityResult(request_code, result_code, data);
            }
        }*/
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

                sendBitmapToWhatsApp("Check out this cool app", BitmapFactory.decodeResource(this.getResources(),R.drawable.color_wheel));
                return (true);

            case R.id.favorite_colors:
                //let the user see hit saved color palettes
                startActivity(new Intent(this, FavoritesActivity.class));
                return (true);

            case R.id.settings:
                startActivity(new Intent(this, EditPreferences.class));
                return (true);

            //removed for as long as i cant figure out how to integrate google pay
           /* case R.id.support_me:
                showSupportDialog();
                return (true);*/
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
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //support me removed for as long as i cant figure out how to get it to work with google pay
    /*private void showSupportDialog(){

        ImageButton back_arrow;
        
        final ArrayList<SupportMeObject> objects = new ArrayList<>();

        objects.add(new SupportMeObject("Water", R.drawable.water_bottle_icon, 1));
        objects.add(new SupportMeObject("Coffee", R.drawable.coffee_icon, 2));
        objects.add(new SupportMeObject("Snack", R.drawable.chips_icon, 5));
        objects.add(new SupportMeObject("Meal", R.drawable.meal_icon, 10));


        final AlertDialog supportDialog = new AlertDialog.Builder(this).create();

        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        View saveView = factory.inflate(R.layout.support_me_dialog, null);

        if(PreferenceManager.getDefaultSharedPreferences(myContext).getBoolean("theme_switch", false) == true) {
            saveView.findViewById(R.id.support_dialog_header).setBackgroundColor(ContextCompat.getColor(this,R.color.dark_theme_primary_color));
        }

        final ListView listView = saveView.findViewById(R.id.support_list_view);
        listView.setAdapter(new SupportAdapter(objects, this));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //set donation details


            }
        });

        back_arrow = saveView.findViewById(R.id.support_me_back_button);

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportDialog.cancel();
            }
        });
        supportDialog.setView(saveView);
        new Dialog(getApplicationContext());
        supportDialog.show();
    }*/

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("theme_switch")) {

        }
    }


    public void sendBitmapToWhatsApp(String pack, Bitmap bitmap) {
        PackageManager pm = this.getPackageManager();
        try {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                //permission not granted
                if(Build.VERSION.SDK_INT >= 23) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                    shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
            else {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "Title", null);
                Uri imageUri = Uri.parse(path);

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                sendIntent.putExtra(Intent.EXTRA_TEXT, pack);
                sendIntent.setType("image/*");
                startActivity(Intent.createChooser(sendIntent, "Select app"));
            }
        } catch (Exception e) {
            Toast.makeText(this, "App not Installed", Toast.LENGTH_SHORT).show();
        }

    }
}
