package com.florian.colorharmony_theory_stragety;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.florian.colorharmony_theory_stragety.color.TColor;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;



public class ColorPickerActivity  extends Activity {
    public ImageView imageView;
    public Bitmap imageBitmap;
    public ConstraintLayout constraintLayout;
    public View colorView;
    public View colorView2;
    public View colorView3;
    public View colorView4;
    public TextView colorText;
    public Spinner harmonyPicker;
    public ImageButton tacticInfo;
    public ImageView savePalete;
    public ImageButton tool_bar;


    public TextView colorTextView1;
    public TextView colorTextView2;
    public TextView colorTextView3;
    public TextView colorTextView4;

    private ArrayList<TacticItem> mTacticList;
    private ArrayList<String> hexArray;
    private TacticAdapter mAdapter;

    public boolean touched;
    public boolean rotationConfirmed;
    public boolean showText = false;
    public String currentColorTextFormat;

    public TacticItem currentTacticItem;

    public String currentTactic;
    public String currentHexColor;

    public TColor myTColor;
    public ArrayList<String> returnedHex;
    ArrayList<ArrayList<Integer>> myOuterRGB;

    int r;
    int g;
    int b;
    int averageColor;
    float[] hsv;

    InterstitialAd mInterstitialAd;



    //prefs
    private static final String PREF_SHOW_FORMATING ="checkbox";
    private static final String PREF_CHOOSE_FORMATNG ="list";
    public  ClipboardManager clipboardManager;


    public final String LOG_TAG = ColorPickerActivity.class.getSimpleName();


    @Override
    public void onBackPressed() {
        if(mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }


        super.onBackPressed();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.color_picker);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        clipboardManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);


        showText = prefs.getBoolean(PREF_SHOW_FORMATING, true);
        currentColorTextFormat = prefs.getString(PREF_CHOOSE_FORMATNG, "Hex");

        imageView = (ImageView) findViewById(R.id.imageView2);
        constraintLayout = (ConstraintLayout) findViewById(R.id.linearLayout);
        colorView = (View) findViewById(R.id.colorView);
        colorView2 = (View) findViewById(R.id.colorView2);
        colorView3 = (View) findViewById(R.id.colorView3);
        colorView4 = (View) findViewById(R.id.colorView4);
        colorText = (TextView) findViewById(R.id.colorText);
        harmonyPicker = (Spinner) findViewById(R.id.harmonyPicker);
        tacticInfo = (ImageButton) findViewById(R.id.tacticInfo);
        savePalete = (ImageButton) findViewById(R.id.savePalette);
        colorTextView1 = (TextView) findViewById(R.id.colorViewText);
        colorTextView2 = (TextView) findViewById(R.id.colorViewText2);
        colorTextView3 = (TextView) findViewById(R.id.colorViewText3);
        colorTextView4 = (TextView) findViewById(R.id.colorViewText4);
        tool_bar = (ImageButton) findViewById(R.id.tool_bar);

        MobileAds.initialize(this, "ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); //test ad code
        mInterstitialAd.loadAd(new AdRequest.Builder().build());




        switch(currentColorTextFormat) {
            case "Hex":
                break;
            case "RGB":
            case "HSV":
                colorTextView1.getLayoutParams().height = 200;
                colorTextView1.getLayoutParams().width = 170;
                colorTextView2.getLayoutParams().height = 200;
                colorTextView2.getLayoutParams().width = 170;
                colorTextView3.getLayoutParams().height = 200;
                colorTextView3.getLayoutParams().width = 170;
                colorTextView4.getLayoutParams().height = 200;
                colorTextView4.getLayoutParams().width = 170;
                break;
            case "CMYK":
                colorTextView1.getLayoutParams().height = 250;
                colorTextView1.getLayoutParams().width = 170;
                colorTextView2.getLayoutParams().height = 250;
                colorTextView2.getLayoutParams().width = 170;
                colorTextView3.getLayoutParams().height = 250;
                colorTextView3.getLayoutParams().width = 170;
                colorTextView4.getLayoutParams().height = 250;
                colorTextView4.getLayoutParams().width = 170;
                break;
        }

        initList();

        //create the Spinner Object and implement the  Listener
        mAdapter = new TacticAdapter(this,mTacticList);
        harmonyPicker.setAdapter(mAdapter);

        harmonyPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentTacticItem = (TacticItem) parent.getItemAtPosition(position);
                String clickedTacticName = currentTacticItem.getTacticName();

                currentTactic = clickedTacticName;

                if(currentTactic != null && myTColor != null) {
                    setHarmonicColors(myTColor);
                    setHarmonyText();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(ColorPickerActivity.this,"nothing clicked",Toast.LENGTH_LONG).show();
            }
        });

        savePalete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(currentHexColor == null){
                    Toast.makeText(ColorPickerActivity.this, "Choose color first", Toast.LENGTH_SHORT).show();
                }

                else{
                    openSaveDialog(hexArray);

                }

            }
        });

        tool_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                finish();
            }
        });

        //bind the colorViews

        touched = false;
        rotationConfirmed = false;
        Bundle ex = getIntent().getExtras();

        try {
            File temporaryFile = (File) ex.get("temporary_file");

            if (temporaryFile != null) {
                imageBitmap = saveProfilePicture(temporaryFile);

            }
        }

        catch(NullPointerException e){
            Uri gallery_uri = getIntent().getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), gallery_uri);
                imageView.setImageBitmap(imageBitmap);
            }
            catch(FileNotFoundException ext){
            }

            catch (IOException ioe){
            }

        }

        try {
            averageColor = calculateAverageColor(imageBitmap, 1);


        }
        catch(NullPointerException e){
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            Intent returnIntent = new Intent(this, MainActivity.class);
            startActivity(returnIntent);
        }

        constraintLayout.setBackgroundColor(averageColor);


        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache(true);


        //image view on touch listener
        colorView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(colorView.getVisibility() == View.VISIBLE) {


                    ClipData colorData = ClipData.newPlainText("Color value", colorTextView1.getText().toString());
                    clipboardManager.setPrimaryClip(colorData);
                    Toast.makeText(ColorPickerActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        colorView2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(colorView.getVisibility() == View.VISIBLE) {
                    ClipData colorData = ClipData.newPlainText("Color value", colorTextView2.getText().toString());
                    clipboardManager.setPrimaryClip(colorData);
                    Toast.makeText(ColorPickerActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        colorView3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(colorView.getVisibility() == View.VISIBLE) {
                    ClipData colorData = ClipData.newPlainText("Color value", colorTextView3.getText().toString());
                    clipboardManager.setPrimaryClip(colorData);
                    Toast.makeText(ColorPickerActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        colorView4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(colorView.getVisibility() == View.VISIBLE) {
                    ClipData colorData = ClipData.newPlainText("Color value", colorTextView4.getText().toString());
                    clipboardManager.setPrimaryClip(colorData);
                    Toast.makeText(ColorPickerActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });


        imageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    v.performClick();
                }

                if ((event.getAction() == MotionEvent.ACTION_DOWN) || (event.getAction() == MotionEvent.ACTION_MOVE)) {
                    imageBitmap = imageView.getDrawingCache();

                    if(colorView.getVisibility() == View.INVISIBLE){
                        colorView.setVisibility(View.VISIBLE);
                        colorView2.setVisibility(View.VISIBLE);
                        colorText.setVisibility(View.INVISIBLE);

                        if(showText){
                            colorTextView1.setVisibility(View.VISIBLE);
                            colorTextView2.setVisibility(View.VISIBLE);
                        }
                    }

                    try {
                        int pixel = imageBitmap.getPixel((int) event.getX(), (int) event.getY());

                        //get RGB value
                        r = Color.red(pixel);
                        g = Color.green(pixel);
                        b = Color.blue(pixel);

                        hsv = new float[3];

                        Color.RGBToHSV(r, g, b, hsv);

                    } catch (IllegalArgumentException ex) {
                    }


                    //set to returned harmonic color
                    myTColor = CalculateHarmonyCalculator.TColorFromRGB((float)r, (float)g, (float)b);
                    currentHexColor = myTColor.toHex();

                    colorView.setBackgroundColor(Color.parseColor("#" + myTColor.toHex()));
                    setHarmonicColors(myTColor);
                    setHarmonyText();

                }
                return true;
            }
        });

        tacticInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

    }

    private  Bitmap saveProfilePicture(File tempFile) {

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

                        return bitmapOrg;
                    }
                } catch (final IOException e) {
                    //do something
                }
            }
        } catch (final Exception e) {
            //do something

        }

        return null;
    }

    private Bitmap createOriginalBitmap(final String imagePath) throws NullPointerException{
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



    public int calculateAverageColor(android.graphics.Bitmap bitmap, int pixelSpacing) {
        int R = 0; int G = 0; int B = 0;


        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int n = 0;
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < pixels.length; i += pixelSpacing) {
            int color = pixels[i];
            R += Color.red(color);
            G += Color.green(color);
            B += Color.blue(color);
            n++;
        }
        return Color.rgb(R / n, G / n, B / n);
    }

    private void initList() {
        mTacticList = new ArrayList<>();
        mTacticList.add(0,new TacticItem("Complementary", R.drawable.ic_complementary_harmony, getResources().getString(R.string.complementary_info), R.drawable.complementary_show));
        mTacticList.get(0).setAlertImageCode(R.raw.basic_color_wheel);

        mTacticList.add(1,new TacticItem("Monochromatic", R.drawable.ic_monochromatic_harmony, getResources().getString(R.string.monochrome_info), R.drawable.monochromatic_show));
        mTacticList.get(1).setAlertImageCode(R.raw.basic_color_wheel);

        mTacticList.add(2,new TacticItem("Analogous", R.drawable.ic_analogous_harmony, getResources().getString(R.string.analogous_info), R.drawable.analogous_show));
        mTacticList.get(2).setAlertImageCode(R.raw.basic_color_wheel);

        mTacticList.add(3,new TacticItem("Split Complementary", R.drawable.ic_split_complementary_harmony, getResources().getString(R.string.split_complementary_info), R.drawable.split_complementary_show));
        mTacticList.get(3).setAlertImageCode(R.raw.basic_color_wheel);

        mTacticList.add(4,new TacticItem("Triadic", R.drawable.ic_triadic_harmony, getResources().getString(R.string.triadic_info), R.drawable.triadic_show));
        mTacticList.get(4).setAlertImageCode(R.raw.basic_color_wheel);

        mTacticList.add(5,new TacticItem("Tetradic", R.drawable.ic_tetradic_harmony, getResources().getString(R.string.tetradic_info), R.drawable.tetradic_show));
        mTacticList.get(5).setAlertImageCode(R.raw.basic_color_wheel);
    }


    public void setHarmonicColors(TColor myTColor){

        hexArray = new ArrayList<>();

        // get the harmony colors based on the selected System

        if (currentTactic == "Complementary") {
            hexArray = CalculateHarmonyCalculator.calculateComplementary(myTColor);

            if(colorView2.getVisibility() == View.VISIBLE) {
                colorView2.setBackgroundColor(Color.parseColor("#" + hexArray.get(0)));
            }

            colorView3.setVisibility(View.INVISIBLE);
            colorView4.setVisibility(View.INVISIBLE);

            colorTextView3.setVisibility(View.INVISIBLE);
            colorTextView4.setVisibility(View.INVISIBLE);

            }



        else if (currentTactic == "Monochromatic"){
            hexArray = CalculateHarmonyCalculator.calculateMonochromeStrategy(myTColor);
            if(colorView2.getVisibility() == View.VISIBLE) {
                colorView2.setBackgroundColor(Color.parseColor("#" + hexArray.get(0)));
            }
            if(colorView3.getVisibility() == View.VISIBLE) {
                colorView3.setBackgroundColor(Color.parseColor("#" + hexArray.get(1)));
            }
            else {

                colorView3.setVisibility(View.VISIBLE);
                if(showText) {
                    colorTextView3.setVisibility(View.VISIBLE);
                }
                colorView3.setBackgroundColor(Color.parseColor("#" + hexArray.get(1)));
            }
            if(colorView4.getVisibility() == View.VISIBLE){
                colorView4.setBackgroundColor(Color.parseColor("#" + hexArray.get(2)));
            }
            else{
                colorView4.setVisibility(View.VISIBLE);
                if(showText) {
                    colorTextView4.setVisibility(View.VISIBLE);
                }
                colorView4.setBackgroundColor(Color.parseColor("#" + hexArray.get(2)));
            }

        }
        else if (currentTactic == "Analogous"){
            hexArray = CalculateHarmonyCalculator.calculateAnalogous(myTColor);
            if(colorView2.getVisibility() == View.VISIBLE) {
                colorView2.setBackgroundColor(Color.parseColor("#" + hexArray.get(0)));
            }
            if(colorView3.getVisibility() == View.INVISIBLE) {
                if(showText) {
                    colorTextView3.setVisibility(View.VISIBLE);
                }
                colorView3.setVisibility(View.VISIBLE);
                colorView3.setBackgroundColor(Color.parseColor("#" + hexArray.get(3)));
            }
            else if(colorView3.getVisibility() == View.VISIBLE){
                colorView3.setBackgroundColor(Color.parseColor("#" + hexArray.get(3)));
            }
            colorTextView4.setVisibility(View.INVISIBLE);
            colorView4.setVisibility(View.INVISIBLE);


        }
        else if (currentTactic == "Split Complementary"){
            hexArray = CalculateHarmonyCalculator.calculateSplitComplementaryStrategy(myTColor);
            if(colorView2.getVisibility() == View.VISIBLE) {
                colorView2.setBackgroundColor(Color.parseColor("#" + hexArray.get(0)));
            }
            else{
                if(showText) {
                    colorTextView3.setVisibility(View.VISIBLE);
                }
                colorView.setVisibility(View.VISIBLE);
                colorView2.setBackgroundColor(Color.parseColor("#" + hexArray.get(0)));

            }
            if(colorView3.getVisibility() == View.VISIBLE) {
                colorView3.setBackgroundColor(Color.parseColor("#" + hexArray.get(1)));
            }
            else{
                colorView3.setVisibility(View.VISIBLE);
                colorView3.setBackgroundColor(Color.parseColor("#" + hexArray.get(1)));
            }

            colorTextView4.setVisibility(View.INVISIBLE);
            colorView4.setVisibility(View.INVISIBLE);


        }

        else if (currentTactic == "Triadic"){
            hexArray = CalculateHarmonyCalculator.calculateTriadStrategy(myTColor);
            if(colorView2.getVisibility() == View.VISIBLE) {
                colorView2.setBackgroundColor(Color.parseColor("#" + hexArray.get(0)));
            }
            else{
                colorView2.setVisibility(View.VISIBLE);
                colorView2.setBackgroundColor(Color.parseColor("#" + hexArray.get(0)));
            }
            if(colorView3.getVisibility() == View.VISIBLE) {
                colorView3.setBackgroundColor(Color.parseColor("#" + hexArray.get(1)));
            }
            else{
                if(showText) {
                    colorTextView3.setVisibility(View.VISIBLE);
                }
                colorView3.setVisibility(View.VISIBLE);
                colorView3.setBackgroundColor(Color.parseColor("#" + hexArray.get(1)));
            }

            colorView4.setVisibility(View.INVISIBLE);
            colorTextView4.setVisibility(View.INVISIBLE);

        }
        else if (currentTactic == "Tetradic"){
            hexArray = CalculateHarmonyCalculator.calculateTetraTheory(myTColor);
            if(colorView2.getVisibility() == View.VISIBLE) {
                colorView2.setBackgroundColor(Color.parseColor("#" + hexArray.get(0)));
            }
            else{
                colorView2.setVisibility(View.VISIBLE);
                colorView2.setBackgroundColor(Color.parseColor("#" + hexArray.get(0)));
            }
            if(colorView3.getVisibility() == View.VISIBLE) {
                colorView3.setBackgroundColor(Color.parseColor("#" + hexArray.get(1)));
            }
            else{
                if(showText) {
                    colorTextView3.setVisibility(View.VISIBLE);
                }

                colorView3.setVisibility(View.VISIBLE);
                colorView3.setBackgroundColor(Color.parseColor("#" + hexArray.get(1)));

            }
            if(colorView4.getVisibility() == View.VISIBLE) {
                colorView4.setBackgroundColor(Color.parseColor("#" + hexArray.get(2)));
            }
            else{
                colorView4.setVisibility(View.VISIBLE);
                if(showText) {
                    colorTextView4.setVisibility(View.VISIBLE);
                }
                colorView4.setBackgroundColor(Color.parseColor("#" + hexArray.get(2)));
            }


        }

    }

    public void setHarmonyText(){

         if(showText){


             if(hexArray != null){
                 switch (currentTactic){
                     case "Complementary":
                         colorTextView1.setText(getCorrectColorFormat(currentHexColor));
                         colorTextView2.setText(getCorrectColorFormat(hexArray.get(0)));
                         break;

                     case "Split Complementary":
                     case "Triadic":
                     case "Analogous":
                         colorTextView3.setVisibility(View.VISIBLE);
                         colorTextView1.setText(getCorrectColorFormat(currentHexColor));
                         colorTextView2.setText(getCorrectColorFormat(hexArray.get(0)));
                         colorTextView3.setText(getCorrectColorFormat(hexArray.get(1)));
                         break;

                     case "Monochromatic":
                     case "Tetradic":
                         colorTextView3.setVisibility(View.VISIBLE);
                         colorTextView1.setText(getCorrectColorFormat(currentHexColor));
                         colorTextView2.setText(getCorrectColorFormat(hexArray.get(0)));
                         colorTextView3.setText(getCorrectColorFormat(hexArray.get(1)));
                         colorTextView4.setText(getCorrectColorFormat(hexArray.get(2)));
                         break;

                 }
             }
         }
    }

    private String getCorrectColorFormat(String originalHex){

        switch(currentColorTextFormat){
            case "Hex":
                return "#" + originalHex;
            case "RGB":
                return CalculateHarmonyCalculator.RGBFromHex(originalHex);
            case "HSV":
                return CalculateHarmonyCalculator.HSVFromHex(originalHex);
            case "CMYK":
                return CalculateHarmonyCalculator.CMYKFromHex(originalHex);
            default:
                return null;
        }
    }





    private void openSaveDialog(final ArrayList<String> colorValues) {



        AlertDialog saveAlertDialog = new AlertDialog.Builder(this).create();

        LayoutInflater factory = LayoutInflater.from(ColorPickerActivity.this);
        View saveView = factory.inflate(R.layout.save_palete_dialog, null);

        final EditText header1 = saveView.findViewById(R.id.favorite_name_enter);
        final EditText description = saveView.findViewById(R.id.favorite_content_enter);

        saveAlertDialog.setView(saveView);
        new Dialog(getApplicationContext());

        //set button
        saveAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Save", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //performed when action is positive
                String myHeader = header1.getText().toString();
                String myContent = description.getText().toString();
                String myTactic = currentTactic;
                //currentHexColor = myTColor.toHex();

                String hex1;
                String hex2;
                String hex3;
                String hex4;

                switch(myTactic){
                    case "Complementary" :
                        hex1 = currentHexColor;
                        hex2 = hexArray.get(0);
                        SQLiteHelper.getInstance(ColorPickerActivity.this)
                                .updateFavorites(myHeader, myContent, myTactic, hex1,hex2);
                        break;
                    case "Analogous":
                    case "Split Complementary":
                    case "Triadic":
                        hex1 = currentHexColor;
                        try {
                            hex2 = hexArray.get(0);
                            hex3 = hexArray.get(1);
                            SQLiteHelper.getInstance(ColorPickerActivity.this)
                                    .updateFavorites(myHeader, myContent, myTactic, hex1,hex2, hex3);
                        }
                        catch (IndexOutOfBoundsException e){

                        }
                    break;
                    case "Monochromatic":
                    case "Tetradic":
                        //original color
                        hex1 = currentHexColor;
                        hex2 = hexArray.get(0);
                        try {
                            hex3 = hexArray.get(1);
                            hex4 = hexArray.get(2);
                            SQLiteHelper.getInstance(ColorPickerActivity.this)
                                    .updateFavorites(myHeader, myContent, myTactic, hex1, hex2, hex3, hex4);
                        }
                        catch(IndexOutOfBoundsException e){
                        }
                        break;
                }

                Toast.makeText(ColorPickerActivity.this, "Saved", Toast.LENGTH_LONG).show();
            }
        });

        saveAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ColorPickerActivity.this, "Canceled", Toast.LENGTH_LONG).show();

            }
        });

        saveAlertDialog.show();
    }

    private void openDialog() {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        LayoutInflater factory = LayoutInflater.from(ColorPickerActivity.this);
        View alertView = factory.inflate(R.layout.alert_dialog_content, null);

        TextView alertHeader = alertView.findViewById(R.id.alert_text_header);
        ImageView alertImage = alertView.findViewById(R.id.alert_text_image);
        TextView alertContent = alertView.findViewById(R.id.alert_text_dialog);

        alertHeader.setPaintFlags(alertHeader.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //set the content
        alertHeader.setText(currentTacticItem.getTacticName());
        alertImage.setImageResource(currentTacticItem.getShowImageResource());
        alertContent.setText(currentTacticItem.getInfoText());

        alertDialog.setView(alertView);


        //Set Button
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Perform Action on Button
            }
        });
        new Dialog(getApplicationContext());
        alertDialog.show();

        // Set Properties for OK Button
        final Button okBT = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams neutralBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        okBT.setTextColor(getResources().getColor(R.color.Coral, null));
        okBT.setLayoutParams(neutralBtnLP);

    }
}


