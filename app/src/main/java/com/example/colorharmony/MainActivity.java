package com.example.colorharmony;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Window;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;




import es.dmoral.toasty.Toasty;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;

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

    //PayPal infos
    String clientId = "AVj9-bLME-v2UqSd9fcn5p0HRXgZkUIMtStHOgSikWN9KL2m9g1YWheiDhiAmhPtQgLJI9XD5kpnkJMS";
    String clientSecret = "EFXHmh-OxOsBveyKtTtZ8gCQEh6fFMiIdlwW02-DdWQbI5Yuw6fum-R_E3i_VInlNPTn9gAC0e42UIQC";
    PayPalConfiguration configuration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myContext = this;

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
        Wallet.WalletOptions walletOptions =
                new Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST) // change to ENVIRONMENT_PRODUCTION later
                        .build();
        paymentsClient = Wallet.getPaymentsClient(this, walletOptions);
        
        //setup paypal
        configuration = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                .clientId(clientId);

        Intent intent = new Intent();
        intent.setClass(myContext, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration);
        startService(intent);


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

            Log.d(LOG_TAG, "onCreate: button is blue!!");
        }
        else{
            cameraImageButton.setBackgroundResource(R.drawable.round_button_day);
            galleryImageButton.setBackgroundResource(R.drawable.round_button_day);
            Log.d(LOG_TAG, "onCreate: button is green!!");
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
        
        else if(result_code == Activity.RESULT_OK && request_code==667){
            PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            Toasty.normal(this, "Big Thanks!", getResources().getDrawable(R.drawable.heart_icon, null)).show();
            GMailSender sender = new GMailSender("armorgames555@googlemail.com", "armorgames555");
            try {
                sender.sendMail("Got a donation", "You received a donation from your app Color Harmony for " + donation_amount + "€",
                        "armorgames555@googlemail.com", "hoppe.florian02@gmail.com");
                Toast.makeText(myContext, "Send mail", Toast.LENGTH_LONG).show();
                Log.d(LOG_TAG, "onActivityResult: send email succesfully");
            }

            catch (Exception e){
                Log.e("Send mail", e.getMessage(), e);
            }
            if(confirmation != null){
                try{

                    Log.i("paymentExample", confirmation.toJSONObject().toString(4));
                }
                
                catch ( JSONException e) {
                    Log.e("paymentExample", "an extremely unlikely failure occurred", e);
                }
            }
        }
        else if(result_code == Activity.RESULT_CANCELED && request_code == 667){
            Toast.makeText(myContext, "Donation canceled", Toast.LENGTH_SHORT).show();
        }
        else if(result_code == PaymentActivity.RESULT_EXTRAS_INVALID && request_code == 667) {
            Toast.makeText(myContext, "Something invalid happened", Toast.LENGTH_SHORT).show();
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

                sendBitmapToWhatsApp("Check out this cool app", BitmapFactory.decodeResource(this.getResources(),R.drawable.color_wheel));
                return (true);

            case R.id.favorite_colors:
                //let the user see hit saved color palettes
                startActivity(new Intent(this, FavoritesActivity.class));
                return (true);

            case R.id.settings:
                startActivity(new Intent(this, EditPreferences.class));
                return (true);

            case R.id.support_me:
                showSupportDialog();
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

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    private void showSupportDialog(){

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
                Toast.makeText(MainActivity.this, "Clicked:" + objects.get(position).getName(), Toast.LENGTH_SHORT).show();
                //set paypal payment details
                PayPalPayment payment = new PayPalPayment(new BigDecimal(objects.get(position).getPrice()),
                        "EUR", "Donate " + objects.get(position).getName() + " to Florian", PayPalPayment.PAYMENT_INTENT_SALE);
                
                Intent intent = new Intent(myContext, PaymentActivity.class);
                
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                donation_amount = (int) objects.get(position).getPrice();
                startActivityForResult(intent,667);
                
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
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("theme_switch")) {

            Log.d(LOG_TAG, "onSharedPreferenceChanged: mApplyNightMode changed to:" + mApplyNightMode.toString());
        }
    }


    public void sendBitmapToWhatsApp(String pack, Bitmap bitmap) {
        Log.d(TAG, "sendBitmapToWhatsApp: got called");
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
            Log.e("Error on sharing", e + " ");
            Toast.makeText(this, "App not Installed", Toast.LENGTH_SHORT).show();
        }

    }
}
