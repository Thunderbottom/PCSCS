package io.github.pcscs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
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

import static java.lang.System.exit;

public class LoginActivity extends AppCompatActivity {

    public EditText userName;
    public EditText password;
    public String uid;
    public String getUN;
    public String getPW;
    private boolean setFlag = false;

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

        final CheckBox rememberCheck = (CheckBox)findViewById(R.id.rememberLogin);
        rememberCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rememberCheck.isChecked()){
                    rememberUser(1);
                }
                else{
                    rememberUser(0);
                }
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
                    TextInputLayout til_username = (TextInputLayout) findViewById(R.id.til_username);
                    TextInputLayout til_password = (TextInputLayout) findViewById(R.id.til_password);
                    til_username.setError(null);
                    til_password.setError(null);
                    if (TextUtils.isEmpty(getUN)) {
                        cancel = true;
                        til_username.setError(getString(R.string.errorField));
                        focusView = userName;
                    }
                    else if (TextUtils.isEmpty(getPW)) {
                        cancel = true;
                        til_password.setError(getString(R.string.errorField));
                        focusView = password;
                    }

                    if (cancel) {
                        focusView.requestFocus();
                    } else {
                        getUN = getUN.toLowerCase().trim();
                        progress = new ProgressDialog(v.getContext());
                        progress.setCancelable(true);
                        progress.setMessage("Logging in");
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.show();
                        mDatabase.child("users").child(getUN).child("email")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(!dataSnapshot.getValue().equals("null")){
                                            String userId = dataSnapshot.getValue(String.class);
                                            login(userId, getPW);
                                        }
                                        else{
                                            progress.dismiss();
                                            Toast.makeText(LoginActivity.this, "The username does not exist", Toast.LENGTH_SHORT).show();
                                            clearFields();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        progress.dismiss();
                                        Toast.makeText(LoginActivity.this, "There was some error processing. Please try again in some time!", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }
                else{
                    noNetwork();
                }
            }
        });

        TextView resetPass = (TextView) findViewById(R.id.resetPass);
        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (LoginActivity.this, UserCheck.class);
                startActivity(intent);
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

    // Try to login to the system
    private void login(String username, String pass){
        mFirebaseAuth.signInWithEmailAndPassword(username, pass)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progress.dismiss();
                            saveUser(getUN);
                            Log.d("getUN", "onComplete: " + getUN);
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

    // Check if user's email is verified
    private void checkIfEmailVerified()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        if (user.isEmailVerified())
        {
            setError();
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
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LoginActivity.this.finish();
                            FirebaseAuth.getInstance().signOut();
                            exit(0);
                        }
                    })
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

    // Send email verification to the user if their email is not verified
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


    // Clear username and password fields in case of error
    private void clearFields(){
        userName.setText("");
        password.setText("");
        userName.requestFocus();
    }

    private void saveUser(String username){
        SharedPreferences.Editor editor = getSharedPreferences("saveUser", MODE_PRIVATE).edit();
        editor.putString("username", username);
        editor.apply();
    }

    private void rememberUser(int val){
        SharedPreferences.Editor editor = getSharedPreferences("rememberUser", MODE_PRIVATE).edit();
        editor.putInt("remember", val);
        editor.apply();
    }


    private void setError() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("errorCheck", 0);
        editor.apply();
    }
}
