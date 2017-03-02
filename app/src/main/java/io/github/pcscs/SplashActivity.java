package io.github.pcscs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    int rememberVal;

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
            SharedPreferences rememberLogin = getSharedPreferences("rememberUser", MODE_PRIVATE);
            rememberVal = rememberLogin.getInt("remember", 0);
            if (rememberVal == 0){
                FirebaseAuth.getInstance().signOut();
            }
            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            if (mFirebaseUser == null) {
                // Not logged in, launch the Log In activity
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
            }
            else{
                Intent intent = new Intent(this, UserProfileActivity.class);
                startActivity(intent);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
            }
        }
    }
    // Check if Application is executed for the first time
    public boolean isFirstTime() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return prefs.getBoolean("FirstTime", true);
    }

}
