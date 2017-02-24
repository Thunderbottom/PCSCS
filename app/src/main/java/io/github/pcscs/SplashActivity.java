package io.github.pcscs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isFirstTime()) {
            Intent introActivity = new Intent(SplashActivity.this, IntroActivity.class);
            startActivity(introActivity);
            finish();
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("FirstTime", false);
            editor.apply();
        }
        else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
    // Check if Application is executed for the first time
    public boolean isFirstTime() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return prefs.getBoolean("FirstTime", true);
    }
}
