package io.github.pcscs.Update;

import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.github.pcscs.R;

public class UserPasswordUpdateActivity extends AppCompatActivity {

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    String getPass, getcPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_password_update);

        final EditText changePass = (EditText) findViewById(R.id.passChangeField);
        final EditText changePassConfirm = (EditText) findViewById(R.id.passChangeConfirmField);
        final TextInputLayout til_changePass = (TextInputLayout) findViewById(R.id.til_changePass);
        final TextInputLayout til_changePassConfirm = (TextInputLayout) findViewById(R.id.til_changePassConfirm);

        // Cancel password update and go back
        Button cancelPassUpdate = (Button) findViewById(R.id.CancelButton);
        cancelPassUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserPasswordUpdateActivity.this.finish();
            }
        });


        // Update user password
        Button updatePassword = (Button) findViewById(R.id.passChangeButton);
        updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserPasswordUpdateActivity.this, "Update password", Toast.LENGTH_SHORT).show();
                til_changePass.setError(null);
                til_changePassConfirm.setError(null);

                getPass = changePass.getText().toString();
                getcPass = changePassConfirm.getText().toString();

                if(TextUtils.isEmpty(getPass)){
                    til_changePass.setError(getString(R.string.errorField));
                    changePass.requestFocus();
                }
                else if (TextUtils.isEmpty(getcPass)){
                    til_changePassConfirm.setError(getString(R.string.errorField));
                    changePassConfirm.requestFocus();
                }
                else if (getPass.equals(getcPass)){
                    til_changePassConfirm.setError(getString(R.string.errorField));
                    changePassConfirm.requestFocus();
                }
                else {
                    mFirebaseUser = mFirebaseAuth.getCurrentUser();
                    assert mFirebaseUser != null;
                    mFirebaseUser.updatePassword(getPass)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(UserPasswordUpdateActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                        UserPasswordUpdateActivity.this.finish();
                                    }
                                }
                            });
                }
            }
        });
    }
}
