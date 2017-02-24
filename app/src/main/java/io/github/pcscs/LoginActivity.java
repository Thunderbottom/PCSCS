package io.github.pcscs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    public EditText userName;
    public EditText password;
    public String getUN;
    public String getPW;

    // Firebase
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // Check if network is available
        if(!isNetworkAvailable()) {
            noNetwork();
        }

        // EditText for Username and Password
        userName = (EditText)findViewById(R.id.usernameField);
        password = (EditText)findViewById(R.id.passwordField);

        // Take user to Register Activity
        Button mReg = (Button)findViewById(R.id.RegisterButton);
        mReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regActivity = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(regActivity);
            }
        });

        Button mLogin = (Button)findViewById(R.id.loginButton);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()) {
                    boolean cancel = false;
                    View focusView = null;
                    getUN = userName.getText().toString();
                    getPW = password.getText().toString();
                    userName.setError(null);
                    password.setError(null);
                    if (TextUtils.isEmpty(getUN)) {
                        cancel = true;
                        userName.setError(getString(R.string.errorField));
                        focusView = userName;
                    }
                    if (TextUtils.isEmpty(getPW)) {
                        cancel = true;
                        password.setError(getString(R.string.errorField));
                        focusView = password;
                    }

                    if (cancel) {
                        focusView.requestFocus();
                    } else {
                        if(android.util.Patterns.EMAIL_ADDRESS.matcher(getUN).matches())
                            login(getUN, getPW);
                        else{
                            //TODO: Login Logic
                            mDatabase.child("users").child(getUN).child("email")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot!=null){
                                                String userId = dataSnapshot.getValue(String.class);
                                                login(userId, getPW);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(LoginActivity.this, "There was some error processing. Please try again in some time!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                }
                else{
                    noNetwork();
                }
            }
        });
    }

    // Check if network is available
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void noNetwork(){
        // If network isn't available, prompt user to open Network Settings
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.no_network)
                .setCancelable(false)
                // Quit if user declines
                .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LoginActivity.this.finish();
                    }
                })
                // Open Internet settings if user agrees
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        startActivity(intent);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void login(String username, String pass){
        mFirebaseAuth.signInWithEmailAndPassword(username, pass)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage(task.getException().getMessage())
                                    .setTitle(R.string.login_error_title)
                                    .setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
    }
}
