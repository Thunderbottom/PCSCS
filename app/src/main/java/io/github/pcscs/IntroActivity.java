package io.github.pcscs;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;


public class IntroActivity extends AppIntro2 {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();
        // Just set a title, description, background and image. AppIntro will do the rest.
        // Placeholder text for now
        addSlide(AppIntroFragment.newInstance("Nice Title",
                "Nice Description",
                R.mipmap.ic_launcher,
                Color.parseColor("#212121")));
        addSlide(AppIntroFragment.newInstance("Another Nice Title",
                "Another Nice Description",
                R.mipmap.ic_launcher,
                Color.parseColor("#212121")));
        addSlide(AppIntroFragment.newInstance("HEHE YOU CAN\'T SKIP THIS",
                "HUEHUEHUEHUE",
                R.mipmap.ic_launcher,
                Color.parseColor("#212121")));

        setProgressButtonEnabled(true);
        showSkipButton(false);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        this.finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
