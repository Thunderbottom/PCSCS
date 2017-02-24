package io.github.pcscs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

    private ProgressDialog progress;

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
                        progress = new ProgressDialog(v.getContext());
                        progress.setCancelable(true);
                        progress.setMessage("Logging in");
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.show();
                        if(android.util.Patterns.EMAIL_ADDRESS.matcher(getUN).matches())
                            login(getUN, getPW);
                        else{
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
                                            progress.dismiss();
                                            Toast.makeText(LoginActivity.this, R.string.errorProcessing, Toast.LENGTH_LONG).show();
                                            Log.d("TAG", "onCancelled: ", databaseError.toException());
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
                            progress.dismiss();
                            checkIfEmailVerified();
                            }
                        else {
                            progress.dismiss();
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

    private void checkIfEmailVerified()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        if (user.isEmailVerified())
        {
            Intent intent = new Intent (LoginActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.registeredVerification)
                    .setTitle("Email Unverified!")
                    .setCancelable(false)
                    .setPositiveButton("Send Confirmation", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            sendVerification();
                            FirebaseAuth.getInstance().signOut();
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void sendVerification()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent
                            Toast.makeText(LoginActivity.this, R.string.verificationSent, Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                            Toast.makeText(LoginActivity.this, R.string.verificationFailedToSend, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}
