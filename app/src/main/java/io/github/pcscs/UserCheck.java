package io.github.pcscs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
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

public class UserCheck extends AppCompatActivity {

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    private String checkPW;
    private String username;


    EditText password;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_check);

        mFirebaseAuth = FirebaseAuth.getInstance();

        password = (EditText) findViewById(R.id.passwordCheckField);

        Button mConfirm = (Button) findViewById(R.id.confirmButton);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPW = password.getText().toString();
                TextInputLayout checkPassword = (TextInputLayout) findViewById(R.id.til_passwordCheck);
                checkPassword.setError(null);
                if(TextUtils.isEmpty(checkPW)){
                    checkPassword.setError(getString(R.string.errorField));
                    password.requestFocus();
                }
                else {
                    progress = new ProgressDialog(v.getContext());
                    progress.setCancelable(false);
                    progress.setMessage("Confirming Identity");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.show();
                    mFirebaseUser = mFirebaseAuth.getCurrentUser();
                    assert mFirebaseUser != null;
                    username = mFirebaseUser.getEmail();
                    assert username != null;

                    mFirebaseAuth.signInWithEmailAndPassword(username, checkPW)
                            .addOnCompleteListener(UserCheck.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progress.dismiss();
                                        sendVal();
                                        UserCheck.this.finish();
                                    }
                                    else {
                                        progress.dismiss();
                                        setError();
                                        int errors = getError();
                                        Toast.makeText(UserCheck.this, "Failed to Verify, tries done: " + errors, Toast.LENGTH_SHORT).show();
                                        if(errors == 3){
                                            FirebaseAuth.getInstance().signOut();
                                            Toast.makeText(UserCheck.this, getString(R.string.securityLogout), Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                        else {
                                            password.setText("");
                                            password.requestFocus();
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent output = new Intent();
        output.putExtra("result", "2");
        setResult(111, output);
        UserCheck.this.finish();
    }

    private void sendVal() {
            Intent output = new Intent();
            output.putExtra("result", "1");
            setResult(111, output);
    }

    private void setError() {
        int error = getError();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("errorCheck", error+1);
        editor.apply();
    }
    
    private int getError(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getInt("errorCheck", 0);
    }
}
