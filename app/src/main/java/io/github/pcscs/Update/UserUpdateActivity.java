package io.github.pcscs.Update;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.github.pcscs.R;
import io.github.pcscs.UserProfileActivity;

public class UserUpdateActivity extends AppCompatActivity {

    String getName, getEmail, getcEmail, username;
    int error = 0;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        final EditText changeName = (EditText)findViewById(R.id.nameChangeField);
        final EditText changeEmail = (EditText)findViewById(R.id.emailChangeField);
        final EditText changeEmailConfirm = (EditText)findViewById(R.id.emailChangeFieldconfirm);
        final TextInputLayout til_changeName = (TextInputLayout) findViewById(R.id.til_changeName);
        final TextInputLayout til_changeEmailConfirm = (TextInputLayout) findViewById(R.id.til_changeEmailconfirm);
        final TextInputLayout til_changeEmail = (TextInputLayout) findViewById(R.id.til_changeEmail);

        changeName.setText(mFirebaseUser.getDisplayName());
        changeEmail.setText(mFirebaseUser.getEmail());
        changeEmailConfirm.setText(mFirebaseUser.getEmail());

        // Goto update password
        TextView editPassword = (TextView)findViewById(R.id.editProfilePassword);
        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (UserUpdateActivity.this, UserPasswordUpdateActivity.class);
                startActivity(intent);
            }
        });

        // Cancel update profile and go back
        Button cancelUpdate = (Button) findViewById(R.id.CancelButton);
        cancelUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserUpdateActivity.this.finish();
            }
        });

        // Update user details
        Button updateUser = (Button) findViewById(R.id.changeProfileButton);
        updateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                til_changeName.setError(null);
                til_changeEmail.setError(null);
                til_changeEmailConfirm.setError(null);

                getName = changeName.getText().toString();
                getEmail = changeEmail.getText().toString();
                getcEmail = changeEmailConfirm.getText().toString();

                if(TextUtils.isEmpty(getName)){
                    til_changeName.setError(getString(R.string.errorField));
                    changeName.requestFocus();
                }
                else if (TextUtils.isEmpty(getEmail)){
                    til_changeEmail.setError(getString(R.string.errorField));
                    changeEmail.requestFocus();
                }
                else if (TextUtils.isEmpty(getcEmail)){
                    til_changeEmailConfirm.setError(getString(R.string.errorField));
                    changeEmailConfirm.requestFocus();
                }
                else if(!getEmail.equals(getcEmail)){
                    changeEmailConfirm.setText("");
                    til_changeEmailConfirm.setError(getString(R.string.emailError));
                    changeEmailConfirm.requestFocus();
                }
                else {
                    // Change details
                    progress = new ProgressDialog(UserUpdateActivity.this);
                    progress.setCancelable(true);
                    progress.setMessage("Updating Profile");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.show();
                    if(!mFirebaseUser.getDisplayName().equals(getName)){
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(getName)
                                .build();
                        mFirebaseUser.updateProfile(profileChangeRequest)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(!task.isSuccessful()){
                                            setError();
                                            Log.d("Update name failed ", "onComplete: " + task.getException().getMessage());
                                        }

                                    }
                                });
                    }
                    if(!mFirebaseUser.getEmail().equals(getEmail.toLowerCase())) {
                            mFirebaseUser.updateEmail(getEmail.toLowerCase())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                SharedPreferences preferences = getSharedPreferences("saveUser", MODE_PRIVATE);
                                                username = preferences.getString("username", "null");
                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                                databaseReference.child("users").child(username).child("email").setValue(getEmail)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(!task.isSuccessful()){
                                                                    setError();
                                                                }
                                                                else{
                                                                    sendVerification();
                                                                    Toast.makeText(UserUpdateActivity.this, "Email updated. Please verify the new email address.", Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                            }
                                            else{
                                                setError();
                                                Log.d("Update email failed ", "onComplete: " + task.getException().getMessage());
                                            }
                                        }
                                    });
                    }
                }
                progress.dismiss();
                if (!(error == 0)){
                    Toast.makeText(UserUpdateActivity.this, getString(R.string.profileUpdateFailed) + error, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(UserUpdateActivity.this, getString(R.string.profileUpdated), Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(UserUpdateActivity.this, UserProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void setError() {
        error += 1;
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
                            Toast.makeText(UserUpdateActivity.this, R.string.verificationSent, Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                            Toast.makeText(UserUpdateActivity.this, R.string.verificationFailedToSend, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}
