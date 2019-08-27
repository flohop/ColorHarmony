package com.example.colorharmony;


import android.graphics.Color;

import android.util.Log;

import com.example.colorharmony.color.ColorList;
import com.example.colorharmony.color.TColor;
import com.example.colorharmony.theory.AnalogousStrategy;
import com.example.colorharmony.theory.ComplementaryStrategy;
import com.example.colorharmony.theory.MonochromeTheoryStrategy;
import com.example.colorharmony.theory.SingleComplementStrategy;
import com.example.colorharmony.theory.SplitComplementaryStrategy;
import com.example.colorharmony.theory.TetradTheoryStrategy;
import com.example.colorharmony.theory.TriadTheoryStrategy;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import java.util.ArrayList;
import java.util.List;

public class CalculateHarmonyCalculator {



    public static TColor TColorFromRGB(float r, float g, float b){


        TColor newTColor = TColor.newRGB(r/ 255 , g /255,  b / 255);
        Log.d("TColor from RGB to HEX", newTColor.toHex());

        return newTColor;
    }


    public static TColor TColorFromHSV(float[] hsvFloat){

        TColor newTColor = TColor.newHSV(hsvFloat[0], hsvFloat[1], hsvFloat[2]);

        return newTColor;
    }



    public static ArrayList<String> calculateTetraTheory(TColor src) {

        TetradTheoryStrategy myTetradStragety = new TetradTheoryStrategy(90);
        ColorList myColorList = myTetradStragety.createListFromColor(src);

        ArrayList<String> masterArray = new ArrayList<>();

        TColor Tcolor1 = myColorList.get(1); //get first harmony color
        TColor Tcolor2 = myColorList.get(2); // get second harmony color
        TColor Tcolor3 = myColorList.get(3); // get third harmony color

        String myHex1 = Tcolor1.toHex();
        String myHex2 = Tcolor2.toHex();
        String myHex3 = Tcolor3.toHex();

        masterArray.add(0, myHex1);
        masterArray.add(1,myHex2);
        masterArray.add(2, myHex3);

        return masterArray;
    }

    public static ArrayList<String> calculateMonochromeStrategy(TColor src) {

        /**
         * Creates 4 additional shades of the given color, thus creating a monochrome
         * palette.
         */

        MonochromeTheoryStrategy monoStrategy = new MonochromeTheoryStrategy();
        ColorList myColorList = monoStrategy.createListFromColor(src);

        ArrayList<String> masterArray = new ArrayList<>();

        TColor Tcolor1 = myColorList.get(0); //get first monochromatic color
        TColor Tcolor2 = myColorList.get(1); // get second monochromatic color
        TColor Tcolor3 = myColorList.get(2); // get  third monochromatic color
        TColor Tcolor4 = myColorList.get(3); // get fourth monochromatic color

        String myHex1 = Tcolor1.toHex();
        String myHex2 = Tcolor2.toHex();
        String myHex3 = Tcolor3.toHex();
        String myHex4 = Tcolor4.toHex();

        //masterArray.add(0, myHex1);
        masterArray.add(0,myHex2);
        masterArray.add(1, myHex3);
        masterArray.add(2, myHex4);

        return masterArray;
    }

    public static ArrayList<String> calculateTriadStrategy(TColor src) {

        /**
         *  >triadic color scheme to create 3 compatible colors for the given one.
         */

        TriadTheoryStrategy monoStrategy = new TriadTheoryStrategy();
        ColorList myColorList = monoStrategy.createListFromColor(src);

        ArrayList<String> masterArray = new ArrayList<>();

        TColor Tcolor1 = myColorList.get(1); //get first monochromatic color
        TColor Tcolor2 = myColorList.get(2); // get second monochromatic color


        String myHex1 = Tcolor1.toHex();
        String myHex2 = Tcolor2.toHex();


        masterArray.add(0, myHex1);
        masterArray.add(1,myHex2);

        return masterArray;
    }

    public static ArrayList<String> calculateSplitComplementaryStrategy(TColor src) {

        SplitComplementaryStrategy splitStrategy = new SplitComplementaryStrategy();
        ColorList myColorList = splitStrategy.createListFromColor(src);

        ArrayList<String> masterArray = new ArrayList<>();

        TColor Tcolor1 = myColorList.get(1); //get first monochromatic color
        TColor Tcolor2 = myColorList.get(2); // get second monochromatic color

        String myHex1 = Tcolor1.toHex();
        String myHex2 = Tcolor2.toHex();

        masterArray.add(0, myHex1);
        masterArray.add(1,myHex2);

        return masterArray;
    }

    public static ArrayList<String> calculateComplementary(TColor src) {
        Log.d("Old hex", src.toHex());



        SingleComplementStrategy complementaryStrategy = new SingleComplementStrategy();
        ColorList myColorList = complementaryStrategy.createListFromColor(src);

        ArrayList<String> masterArray = new ArrayList<>();

        TColor Tcolor1 = myColorList.get(1); //get first complementary color
        Log.d("My TColor1: ", Tcolor1.toString());
       /* TColor Tcolor2 = myColorList.get(1); // get second complementary color
        TColor Tcolor3 = myColorList.get(2); // get  third complementary color
        TColor Tcolor4 = myColorList.get(3); // get fourth complementary color*/

        String myHex1 = Tcolor1.toHex();
        Log.d("Created HEX", myHex1);
   /*     String myHex2 = Tcolor2.toHex();
        String myHex3 = Tcolor3.toHex();
        String myHex4 = Tcolor4.toHex();
*/
        masterArray.add(0, myHex1);
    /*    masterArray.add(1, myHex2);
        masterArray.add(2, myHex3);
        masterArray.add(3, myHex4);*/

        return masterArray;
    }

    public static ArrayList<String> calculateAnalogous(TColor src) {

        AnalogousStrategy analogousStrategy = new AnalogousStrategy();
        ColorList myColorList = analogousStrategy.createListFromColor(src);

        ArrayList<String> masterArray = new ArrayList<>();

        TColor Tcolor1 = myColorList.get(1); //get first monochromatic color
        TColor Tcolor2 = myColorList.get(2); // get second monochromatic color
        TColor Tcolor3 = myColorList.get(3); // get  third monochromatic color
        TColor Tcolor4 = myColorList.get(4); // get fourth monochromatic color

        String myHex1 = Tcolor1.toHex();
        String myHex2 = Tcolor2.toHex();
        String myHex3 = Tcolor3.toHex();
        String myHex4 = Tcolor4.toHex();

        masterArray.add(0, myHex1);
        masterArray.add(1, myHex2);
        masterArray.add(2, myHex3);
        masterArray.add(3, myHex4);

        return masterArray;

    }
}
