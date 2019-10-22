package com.florian.colorharmony_theory_stragety;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Hello slide
        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle(getResources().getString(R.string.intro_text_p1_title));
        sliderPage1.setDescription(getResources().getString(R.string.intro_text_p1_description));
        sliderPage1.setImageDrawable(R.mipmap.icon);
        sliderPage1.setBgColor(R.color.Chocolate);
        
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        //select image slide
        SliderPage slidePage2 = new SliderPage();
        slidePage2.setTitle(getResources().getString(R.string.intro_text_p2_title));
        slidePage2.setDescription(getResources().getString(R.string.intro_text_p2_description));
        slidePage2.setImageDrawable(R.drawable.start_screen_tour);
        slidePage2.setBgColor(R.color.Chocolate);

        addSlide(AppIntroFragment.newInstance(slidePage2));

        //inspect image slide 1
        SliderPage sliderPage3  = new SliderPage();
        sliderPage3.setTitle(getResources().getString(R.string.intro_text_p3_title));
        sliderPage3.setDescription(getResources().getString(R.string.intro_text_p3_description));
        sliderPage3.setImageDrawable(R.drawable.color_pick_basic_tour);
        sliderPage3.setBgColor(R.color.Chocolate);
        addSlide( AppIntroFragment.newInstance((sliderPage3)));

        //inserter, show rows
        SliderPage sliderPage311 = new SliderPage();
        sliderPage311.setTitle(getResources().getString(R.string.intro_text_p311_title));
        sliderPage311.setDescription(getResources().getString(R.string.intro_text_p311_description));
        sliderPage311.setImageDrawable(R.drawable.color_pick_basic_step2_tour);
        sliderPage311.setBgColor(R.color.Chocolate);
        addSlide(AppIntroFragment.newInstance(sliderPage311));

        //inserter, show infors
        SliderPage sliderPage31 = new SliderPage();
        sliderPage31.setTitle(getResources().getString(R.string.intro_text_p31_title));
        sliderPage31.setDescription(getResources().getString(R.string.intro_text_p31_description));
        sliderPage31.setImageDrawable(R.drawable.color_pick_basic_step3_tour);
        sliderPage31.setBgColor(R.color.Chocolate);
        addSlide(AppIntroFragment.newInstance(sliderPage31));

        // inspect image slide 2, save favorites
        SliderPage sliderPage4 = new SliderPage();
        sliderPage4.setTitle(getResources().getString(R.string.intro_text_p4_title));
        sliderPage4.setDescription(getResources().getString(R.string.intro_text_p4_description));
        sliderPage4.setImageDrawable(R.drawable.color_pick_basic_step4_tour);
        sliderPage4.setBgColor(R.color.Chocolate);

        addSlide(AppIntroFragment.newInstance(sliderPage4));

        // show favorites
        SliderPage sliderPage5 = new SliderPage();
        sliderPage5.setTitle(getResources().getString(R.string.intro_text_p5_title));
        sliderPage5.setDescription(getResources().getString(R.string.intro_text_p5_description));
        sliderPage5.setImageDrawable(R.drawable.fav_colors_tour);
        sliderPage5.setBgColor(R.color.Chocolate);
        addSlide(AppIntroFragment.newInstance(sliderPage5));

        // Settings
        SliderPage sliderPage6 = new SliderPage();
        sliderPage6.setTitle(getResources().getString(R.string.intro_text_p6_title));
        sliderPage6.setDescription(getResources().getString(R.string.intro_text_p6_description));
        sliderPage6.setImageDrawable(R.drawable.settings_tour);
        sliderPage6.setBgColor(R.color.Chocolate);
        addSlide(AppIntroFragment.newInstance(sliderPage6));

        // Go explore
        SliderPage sliderPage7 = new SliderPage();
        sliderPage7.setTitle(getResources().getString(R.string.intro_text_p7_title));
        sliderPage7.setDescription(getResources().getString(R.string.intro_text_p7_description));
        sliderPage7.setImageDrawable(R.mipmap.icon);
        sliderPage7.setBgColor(R.color.Chocolate);
        addSlide(AppIntroFragment.newInstance(sliderPage7));
        
        //optional 
        setBarColor(Color.parseColor("#008577"));
        setSeparatorColor(Color.parseColor("#2196F3"));
    }
    
    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Toast.makeText(this, getResources().getString(R.string.intro_finish_text), Toast.LENGTH_SHORT).show();
        this.finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        Toast.makeText(this, "Have fun!", Toast.LENGTH_SHORT).show();
        this.finish();
    }
}
