package io.github.pcscs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    public EditText nField;
    public EditText unField;
    public EditText eField;
    public EditText passField;
    public EditText cpassField;

    public String mName;
    public String mUsername;
    public String mEmailid;
    public String mPass;
    public String mcPass;
    public String UID;
    public boolean setFlag = false;

    private ProgressDialog progress;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();


        nField = (EditText) findViewById(R.id.nameField);
        unField = (EditText) findViewById(R.id.userNameField);
        eField = (EditText) findViewById(R.id.emailField);
        passField = (EditText) findViewById(R.id.passwordField);
        cpassField = (EditText) findViewById(R.id.confirmPasswordField);

        // Go Back on Pressing Cancel
        Button mCancel = (Button) findViewById(R.id.CancelButton);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
            }
        });

        Button mRegister = (Button) findViewById(R.id.RegisterButton);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    View focusView = null;
                    boolean cancel = false;
                    //mProgressbar.setVisibility(View.VISIBLE);

                    mName = nField.getText().toString().trim();
                    mUsername = unField.getText().toString().trim();
                    mEmailid = eField.getText().toString().trim();
                    mPass = passField.getText().toString().trim();
                    mcPass = cpassField.getText().toString().trim();

                    TextInputLayout til_regname = (TextInputLayout) findViewById(R.id.til_regname);
                    TextInputLayout til_regusername = (TextInputLayout) findViewById(R.id.til_regusername);
                    TextInputLayout til_regemail = (TextInputLayout) findViewById(R.id.til_regemail);
                    TextInputLayout til_regpass = (TextInputLayout) findViewById(R.id.til_regpass);
                    TextInputLayout til_regcpass = (TextInputLayout) findViewById(R.id.til_regcpass);


                    til_regname.setError(null);
                    til_regusername.setError(null);
                    til_regemail.setError(null);
                    til_regpass.setError(null);
                    til_regcpass.setError(null);

                    if (TextUtils.isEmpty(mName)) {
                        cancel = true;
                        til_regname.setError(getString(R.string.errorField));
                        focusView = nField;
                        setPass();
                    } else if (TextUtils.isEmpty(mUsername)) {
                        cancel = true;
                        til_regusername.setError(getString(R.string.errorField));
                        focusView = unField;
                        setPass();
                    } else if (TextUtils.isEmpty(mEmailid)) {
                        cancel = true;
                        til_regemail.setError(getString(R.string.errorField));
                        focusView = eField;
                        setPass();
                    } else if (TextUtils.isEmpty(mPass)) {
                        cancel = true;
                        til_regpass.setError(getString(R.string.errorField));
                        focusView = passField;
                    } else if (TextUtils.isEmpty(mcPass)) {
                        cancel = true;
                        til_regcpass.setError(getString(R.string.errorField));
                        focusView = cpassField;
                    } else if (!(mPass.equals(mcPass))) {
                        cancel = true;
                        til_regcpass.setError((getString(R.string.passError)));
                        setPass();
                        focusView = passField;
                    }
                    if (cancel) {
                        //mProgressbar.setVisibility(View.GONE);
                        focusView.requestFocus();
                    } else {
                        // TODO: Encrypt password
                        progress = new ProgressDialog(RegisterActivity.this);
                        progress.setCancelable(true);
                        progress.setMessage("Registering");
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.show();
                        mUsername = mUsername.toLowerCase();
                        mEmailid = mEmailid.toLowerCase();
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("users")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot username : dataSnapshot.getChildren()){
                                            if(username.toString().equals(mUsername)){
                                                // Username exists
                                                makeFlagSet();
                                            }
                                        }
                                        if(!setFlag) {
                                            regUser(mName, mUsername, mEmailid, mPass);
                                        }
                                        else{
                                            progress.dismiss();
                                            Toast.makeText(RegisterActivity.this, "The username already exists!", Toast.LENGTH_SHORT).show();
                                            unField.setText("");
                                            setPass();
                                            unField.requestFocus();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        progress.dismiss();
                                        Toast.makeText(RegisterActivity.this, R.string.errorProcessing, Toast.LENGTH_LONG).show();
                                        Log.d("TAG", "onCancelled: ", databaseError.toException());
                                    }
                                });
                    }

                }
                else {
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

    private void noNetwork() {
        // If network isn't available, prompt user to open Network Settings
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.no_network)
                .setCancelable(false)
                // Quit if user declines
                .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RegisterActivity.this.finish();
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

    private void setPass() {
        passField.setText("");
        cpassField.setText("");
    }

    private void regUser(final String name, final String username, final String emailId, final String password) {
        mFirebaseAuth.createUserWithEmailAndPassword(emailId, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("users").child(username).child("email").setValue(emailId);
                            FirebaseUser regUser = task.getResult().getUser();
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            regUser.updateProfile(profileChangeRequest);
                            regUser.sendEmailVerification();
                            FirebaseAuth.getInstance().signOut();
                            progress.dismiss();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            RegisterActivity.this.finish();
                            Toast.makeText(RegisterActivity.this, R.string.registerSuccess, Toast.LENGTH_SHORT).show();
                        } else {
                            progress.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                            builder.setMessage(task.getException().getMessage())
                                    .setTitle(R.string.login_error_title)
                                    .setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
    }

    private void makeFlagSet(){
        setFlag = true;
    }

}
