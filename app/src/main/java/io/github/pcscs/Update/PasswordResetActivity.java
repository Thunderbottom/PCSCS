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

import io.github.pcscs.R;

public class PasswordResetActivity extends AppCompatActivity {

    private String email;

    EditText emailID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        final FirebaseAuth mFirebaseAuth;

        mFirebaseAuth = FirebaseAuth.getInstance();

        emailID = (EditText)findViewById(R.id.emailCheckField);
        Button mConfirm = (Button) findViewById(R.id.confirmButton);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailID.getText().toString();
                TextInputLayout til_emailCheck = (TextInputLayout) findViewById(R.id.til_emailCheck);
                til_emailCheck.setError(null);
                if(TextUtils.isEmpty(email)){
                    til_emailCheck.setError(getString(R.string.errorField));
                    emailID.requestFocus();
                }
                else {
                    mFirebaseAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(PasswordResetActivity.this, getString(R.string.PasswordResetSent), Toast.LENGTH_SHORT).show();
                                        PasswordResetActivity.this.finish();
                                    }
                                    else {
                                        Toast.makeText(PasswordResetActivity.this, getString(R.string.PasswordResetFailed), Toast.LENGTH_SHORT).show();
                                        emailID.setText("");
                                    }
                                }
                            });
                }
            }
        });
    }
}
