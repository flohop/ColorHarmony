package com.example.colorharmony;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.colorharmony.color.TColor;
import com.example.colorharmony.CalculateHarmonyCalculator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class ColorPickerActivity  extends Activity {
    public ImageView imageView;
    public ImageButton confirmColorButton;
    public Bitmap imageBitmap;
    public ConstraintLayout constraintLayout;
    public View colorView;
    public View colorView2;
    public View colorView3;
    public View colorView4;
    public View colorView5;
    public TextView colorText;
    public Spinner harmonyPicker;
    public ImageButton tacticInfo;

    private ArrayList<TacticItem> mTacticList;
    private TacticAdapter mAdapter;

    public boolean touched;
    public boolean rotationConfirmed;

    public TacticItem currentTacticItem;

    public String currentTactic;

    public TColor myTColor;
    public ArrayList<String> returnedHex;
    ArrayList<ArrayList<Integer>> myOuterRGB;

    int r;
    int g;
    int b;
    int averageColor;
    float[] hsv;

    public final String LOG_TAG = ColorPickerActivity.class.getSimpleName();



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.color_picker);

        imageView = (ImageView) findViewById(R.id.imageView2);
        confirmColorButton = (ImageButton) findViewById(R.id.confirmColorButton);
        constraintLayout = (ConstraintLayout) findViewById(R.id.linearLayout);
        colorView = (View) findViewById(R.id.colorView);
        colorView2 = (View) findViewById(R.id.colorView2);
        colorView3 = (View) findViewById(R.id.colorView3);
        colorView4 = (View) findViewById(R.id.colorView4);
        colorView5 = (View) findViewById(R.id.colorView5);
        colorText = (TextView) findViewById(R.id.colorText);
        harmonyPicker = (Spinner) findViewById(R.id.harmonyPicker);
        tacticInfo = (ImageButton) findViewById(R.id.tacticInfo);

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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(ColorPickerActivity.this,"nothing clicked",Toast.LENGTH_LONG).show();
            }
        });



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
                Log.d(LOG_TAG, "File not found exception");
            }

            catch (IOException ioe){
                Log.d(LOG_TAG, "Error loading the image");
            }

        }

        averageColor =  calculateAverageColor(imageBitmap, 1);
        constraintLayout.setBackgroundColor(averageColor);

        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache(true);


        //image view on touch listener


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
                    }

                    try {
                        int pixel = imageBitmap.getPixel((int) event.getX(), (int) event.getY());

                        //get RGB value
                        r = Color.red(pixel);
                        g = Color.green(pixel);
                        b = Color.blue(pixel);

                        hsv = new float[3];

                        Color.RGBToHSV(r, g, b, hsv);
                        Log.d("MY hsv", "h:" + hsv[0] + " s " + hsv[1] + " v:" + hsv[2]);

                        //getting Hex values

                        //set background color of view
                        //colorView.setBackgroundColor(Color.rgb(r, g, b));
                    } catch (IllegalArgumentException ex) {
                        Log.d(LOG_TAG, "IllegalArgumentException appeared");
                    }

                    Log.d(LOG_TAG, "R:" + r + ", G:" + g + ", B:" + b);

                    //set to returned harmonic color
                    Log.d(LOG_TAG, "Created new TColor on new values: " + hsv[0] + ", " + hsv[1] + ", " + hsv[2]);
                    myTColor = CalculateHarmonyCalculator.TColorFromRGB((float)r, (float)g, (float)b);

                    colorView.setBackgroundColor(Color.rgb(myTColor.red(),myTColor.green(), myTColor.blue()));


                    setHarmonicColors(myTColor);


                    Log.d(LOG_TAG, "NO EXCEPTION OCCURED");


                }
                return true;
            }
        });

        tacticInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Current tactic: " + currentTacticItem.getTacticName());
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

        Log.d(LOG_TAG, "Bitmap returned null");
        return null;
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
        mTacticList.add(0,new TacticItem("Complementary", R.drawable.ic_complementary_harmony, getResources().getString(R.string.complementary_info)));
        mTacticList.get(0).setAlertImageCode(R.drawable.color_wheel);

        mTacticList.add(1,new TacticItem("Monochromatic", R.drawable.ic_monochromatic_harmony, getResources().getString(R.string.monochrome_info)));
        mTacticList.get(1).setAlertImageCode(R.raw.basic_color_wheel);

        mTacticList.add(2,new TacticItem("Analogous", R.drawable.ic_analogous_harmony, getResources().getString(R.string.analogous_info)));
        mTacticList.get(2).setAlertImageCode(R.raw.basic_color_wheel);

        mTacticList.add(3,new TacticItem("Split Complementary", R.drawable.ic_split_complementary_harmony, getResources().getString(R.string.split_complementary_info)));
        mTacticList.get(3).setAlertImageCode(R.raw.basic_color_wheel);

        mTacticList.add(4,new TacticItem("Triadic", R.drawable.ic_triadic_harmony, getResources().getString(R.string.triadic_info)));
        mTacticList.get(4).setAlertImageCode(R.raw.basic_color_wheel);

        mTacticList.add(5,new TacticItem("Tetradic", R.drawable.ic_tetradic_harmony, getResources().getString(R.string.tetradic_info)));
        mTacticList.get(5).setAlertImageCode(R.raw.basic_color_wheel);
    }


    public void setHarmonicColors(TColor myTColor){

        ArrayList<String> hexArray = new ArrayList<>();

        // get the harmony colors based on the selected System

        if (currentTactic == "Complementary") {
            hexArray = CalculateHarmonyCalculator.calculateComplementary(myTColor);

            if(colorView2.getVisibility() == View.VISIBLE) {
                colorView2.setBackgroundColor(Color.parseColor("#" + hexArray.get(0)));
            }

            colorView3.setVisibility(View.INVISIBLE);
            colorView4.setVisibility(View.INVISIBLE);
            colorView5.setVisibility(View.INVISIBLE);
            //colorView3.setBackgroundColor(Color.parseColor("#" + hexArray.get(1)));
            //colorView4.setBackgroundColor(Color.parseColor("#" + hexArray.get(2)));
            //colorView5.setBackgroundColor(Color.parseColor("#" + hexArray.get(3)));
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
                colorView3.setBackgroundColor(Color.parseColor("#" + hexArray.get(1)));
            }
            if(colorView4.getVisibility() == View.VISIBLE){
                colorView4.setBackgroundColor(Color.parseColor("#" + hexArray.get(2)));
            }
            else{
                colorView4.setVisibility(View.VISIBLE);
                colorView4.setBackgroundColor(Color.parseColor("#" + hexArray.get(2)));
            }
         /*   if(colorView5.getVisibility() == View.VISIBLE){
                colorView5.setBackgroundColor(Color.parseColor("#" + hexArray.get(3)));
            }
            else{
                colorView5.setVisibility(View.VISIBLE);
                colorView5.setBackgroundColor(Color.parseColor("#" + hexArray.get(3)));
            }*/
        }
        else if (currentTactic == "Analogous"){
            hexArray = CalculateHarmonyCalculator.calculateAnalogous(myTColor);
            if(colorView2.getVisibility() == View.VISIBLE) {
                colorView2.setBackgroundColor(Color.parseColor("#" + hexArray.get(0)));
            }
            if(colorView3.getVisibility() == View.INVISIBLE) {
                colorView3.setVisibility(View.VISIBLE);
                colorView3.setBackgroundColor(Color.parseColor("#" + hexArray.get(3)));
            }
            else if(colorView3.getVisibility() == View.VISIBLE){
                colorView3.setBackgroundColor(Color.parseColor("#" + hexArray.get(3)));
            }

            colorView4.setVisibility(View.INVISIBLE);
            colorView5.setVisibility(View.INVISIBLE);

        }
        else if (currentTactic == "Split Complementary"){
            hexArray = CalculateHarmonyCalculator.calculateSplitComplementaryStrategy(myTColor);
            if(colorView2.getVisibility() == View.VISIBLE) {
                colorView2.setBackgroundColor(Color.parseColor("#" + hexArray.get(0)));
            }
            else{
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

            colorView4.setVisibility(View.INVISIBLE);
            colorView5.setVisibility(View.INVISIBLE);
            //colorView4.setBackgroundColor(Color.parseColor("#" + hexArray.get(2)));
            //colorView5.setBackgroundColor(Color.parseColor("#" + hexArray.get(3)));
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
                colorView3.setVisibility(View.VISIBLE);
                colorView3.setBackgroundColor(Color.parseColor("#" + hexArray.get(1)));
            }

            colorView4.setVisibility(View.INVISIBLE);
            colorView5.setVisibility(View.INVISIBLE);
            //colorView4.setBackgroundColor(Color.parseColor("#" + hexArray.get(2)));
            //colorView5.setBackgroundColor(Color.parseColor("#" + hexArray.get(3)));
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
                colorView3.setVisibility(View.VISIBLE);
                colorView3.setBackgroundColor(Color.parseColor("#" + hexArray.get(1)));
            }
            if(colorView4.getVisibility() == View.VISIBLE) {
                colorView4.setBackgroundColor(Color.parseColor("#" + hexArray.get(2)));
            }
            else{
                colorView4.setVisibility(View.VISIBLE);
                colorView4.setBackgroundColor(Color.parseColor("#" + hexArray.get(2)));
            }

            colorView5.setVisibility(View.INVISIBLE);
            //colorView5.setBackgroundColor(Color.parseColor("#" + hexArray.get(3)));
        }

    }

    private void openDialog() {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        LayoutInflater factory = LayoutInflater.from(ColorPickerActivity.this);
        View alertView = factory.inflate(R.layout.alert_dialog_content, null);

        TextView alertHeader = alertView.findViewById(R.id.alert_text_header);
        ImageView alertImage = alertView.findViewById(R.id.alert_text_image);
        TextView alertContent = alertView.findViewById(R.id.alert_text_dialog);

        //set the content
        alertHeader.setText(currentTacticItem.getTacticName());
        alertImage.setImageResource(currentTacticItem.getTacticImage());
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

