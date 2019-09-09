package com.example.colorharmony;

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
        sliderPage1.setTitle("Welcome to Color Harmony!");
        sliderPage1.setDescription("Create stunning color palettes and explore the compositions of your favorite images");
        sliderPage1.setImageDrawable(R.mipmap.icon);
        sliderPage1.setBgColor(R.color.Chocolate);
        
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        //select image slide
        SliderPage slidePage2 = new SliderPage();
        slidePage2.setTitle("1. Create or choose and image");
        slidePage2.setDescription("You will select a color from this image to calculate the harmony from");
        slidePage2.setImageDrawable(R.drawable.start_screen_tour);
        slidePage2.setBgColor(R.color.Chocolate);

        addSlide(AppIntroFragment.newInstance(slidePage2));

        //inspect image slide 1
        SliderPage sliderPage3  = new SliderPage();
        sliderPage3.setTitle("2. Click on your image");
        sliderPage3.setDescription("Clicking on the image sets the base color from which the harmony will be " +
                "calculated, to change the harmony type, just click on the list and choose one that suits your " +
                "intentions. Pro tip: By clicking on one of the above colors, you can save the color code to your clipboard." +
                "By clicking the top back button you get back to the main page");
        sliderPage3.setImageDrawable(R.drawable.color_pick_basic_tour);
        sliderPage3.setBgColor(R.color.Chocolate);

        addSlide( AppIntroFragment.newInstance((sliderPage3)));

        //inserter, show infors
        SliderPage sliderPage31 = new SliderPage();
        sliderPage31.setTitle("3. Learn the theory");
        sliderPage31.setDescription("By clicking in the i-Icon, you get to learn, what the current tactic" +
                "is used for, and how to use it");
        sliderPage31.setImageDrawable(R.drawable.color_pick_basic_step3_tour);
        sliderPage31.setBgColor(R.color.Chocolate);

        // inspect image slide 2, save favorites
        SliderPage sliderPage4 = new SliderPage();
        sliderPage4.setTitle("4. Save your favorite colors");
        sliderPage4.setDescription("Got a color scheme that fits you? Perfect! Now click on the save icon," +
                "enter the name, and a description, to save your colors for later");
        sliderPage4.setImageDrawable(R.drawable.color_pick_basic_step4_tour);
        sliderPage4.setBgColor(R.color.Chocolate);

        addSlide(AppIntroFragment.newInstance(sliderPage4));

        // show favorites
        SliderPage sliderPage5 = new SliderPage();
        sliderPage5.setTitle("5. Visit your favorites");
        sliderPage5.setDescription("By clicking on the star icon on the task bar, you can take a look at " +
                "all your saved color palettes, click on one item of the list, to further inspect it." +
                " By swiping either left or right, you delete the item from your collection. By clicking on it, you get more details, " +
                "and the ability, to share your scheme with your friends");
        sliderPage5.setImageDrawable(R.drawable.fav_colors_tour);
        sliderPage5.setBgColor(R.color.Chocolate);
        addSlide(AppIntroFragment.newInstance(sliderPage5));

        // Settings
        SliderPage sliderPage6 = new SliderPage();
        sliderPage6.setTitle("6. Change settings");
        sliderPage6.setDescription("By clicking on the gear wheel on the task bar, you can change the settings." +
                " Here you can choose if you want the color formats shown and in what format you want them. Additionally," +
                "for all your nigh owls out there, you can switch on the night theme.");
        sliderPage6.setImageDrawable(R.drawable.settings_tour);
        sliderPage6.setBgColor(R.color.Chocolate);
        addSlide(AppIntroFragment.newInstance(sliderPage6));

        // Go explore
        SliderPage sliderPage7 = new SliderPage();
        sliderPage7.setTitle("Explore, create and have fun");
        sliderPage7.setDescription("Congratulations, you've finished this quick overview and are now ready to dive " +
                "in deep into the world of colors!");
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
        Toast.makeText(this, "Have fun!", Toast.LENGTH_SHORT).show();
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
