package io.github.pcscs;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class ManageBuild extends AppCompatActivity {

    String buildNumber, username;
    String getBN, getCPU, getCAB, getGPU, getMOBO, getMON, getPSU;
    ProgressDialog progress;
    DatabaseReference databaseReference;
    int error = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_build);
        setTitle("Manage Build");

        Bundle bundle = getIntent().getExtras();
        buildNumber = bundle.getString("buildNumber");

        final EditText buildName = (EditText) findViewById(R.id.nameField);
        final EditText cpu = (EditText) findViewById(R.id.cpuField);
        final EditText cab = (EditText) findViewById(R.id.cabField);
        final EditText gpu = (EditText) findViewById(R.id.gpuField);
        final EditText mobo = (EditText) findViewById(R.id.moboField);
        final EditText mon = (EditText) findViewById(R.id.monField);
        final EditText psu = (EditText) findViewById(R.id.psuField);

        final TextInputLayout buildNameText = (TextInputLayout) findViewById(R.id.manageName);
        final TextInputLayout cpuText = (TextInputLayout) findViewById(R.id.manageCPU);
        final TextInputLayout cabText = (TextInputLayout) findViewById(R.id.manageCAB);
        final TextInputLayout gpuText = (TextInputLayout) findViewById(R.id.manageGPU);
        final TextInputLayout moboText = (TextInputLayout) findViewById(R.id.manageMOBO);
        final TextInputLayout monText = (TextInputLayout) findViewById(R.id.manageMON);
        final TextInputLayout psuText = (TextInputLayout) findViewById(R.id.managePSU);

        buildName.setText(bundle.getString("buildName"));
        cpu.setText(bundle.getString("CPU"));
        cab.setText(bundle.getString("CAB"));
        gpu.setText(bundle.getString("GPU"));
        mobo.setText(bundle.getString("MOBO"));
        mon.setText(bundle.getString("MON"));
        psu.setText(bundle.getString("PSU"));

        SharedPreferences preferences = getSharedPreferences("saveUser", MODE_PRIVATE);
        username = preferences.getString("username", "null");

        Button updateButton = (Button) findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildNameText.setError(null);
                cpuText.setError(null);
                cabText.setError(null);
                gpuText.setError(null);
                moboText.setError(null);
                monText.setError(null);
                psuText.setError(null);

                if (TextUtils.isEmpty(buildName.getText().toString())){
                    buildNameText.setError(getString(R.string.errorField));
                    buildName.requestFocus();
                }
                else if (TextUtils.isEmpty(cpu.getText().toString())){
                    cpuText.setError(getString(R.string.errorField));
                    cpu.requestFocus();
                }
                else if (TextUtils.isEmpty(cab.getText().toString())){
                    cabText.setError(getString(R.string.errorField));
                    cab.requestFocus();
                }
                else if (TextUtils.isEmpty(gpu.getText().toString())){
                    gpuText.setError(getString(R.string.errorField));
                    gpu.requestFocus();
                }
                else if (TextUtils.isEmpty(mobo.getText().toString())){
                    moboText.setError(getString(R.string.errorField));
                    mobo.requestFocus();
                }
                else if (TextUtils.isEmpty(mon.getText().toString())){
                    monText.setError(getString(R.string.errorField));
                    mon.requestFocus();
                }
                else if (TextUtils.isEmpty(psu.getText().toString())){
                    psuText.setError(getString(R.string.errorField));
                    psu.requestFocus();
                }
                else {
                    getBN = buildName.getText().toString();
                    getCPU = cpu.getText().toString();
                    getCAB = cab.getText().toString();
                    getGPU = gpu.getText().toString();
                    getMOBO = mobo.getText().toString();
                    getMON = mon.getText().toString();
                    getPSU = psu.getText().toString();
                    progress = new ProgressDialog(ManageBuild.this);
                    progress.setCancelable(false);
                    progress.setMessage("Updating Build");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.show();


                    databaseReference = FirebaseDatabase.getInstance().getReference().child("buildlist").child(buildNumber);
                    databaseReference
                            .child("CPU").setValue(getCPU)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()){
                                        setError();
                                    }
                                }
                            });
                    databaseReference
                            .child("GPU").setValue(getGPU)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()){
                                        setError();
                                    }
                                }
                            });
                    databaseReference
                            .child("Cabinet").setValue(getCAB)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()){
                                        setError();
                                    }
                                }
                            });
                    databaseReference
                            .child("Monitor").setValue(getMON)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()){
                                        setError();
                                    }
                                }
                            });
                    databaseReference
                            .child("PSU").setValue(getPSU)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()){
                                        setError();
                                    }
                                }
                            });
                    databaseReference
                            .child("Motherboard").setValue(getMOBO)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()){
                                        setError();
                                    }
                                }
                            });

                    databaseReference = FirebaseDatabase.getInstance().getReference().child("builds").child(username);
                    databaseReference.child(buildNumber)
                            .setValue(getBN)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()){
                                        setError();
                                    }
                                }
                            });
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("TDP");
                    databaseReference.child(buildNumber)
                            .setValue(getTDP() + "W")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()){
                                        setError();
                                    }
                                }
                            });
                    progress.dismiss();
                    if(error >= 1){
                        Toast.makeText(ManageBuild.this, getString(R.string.buildUpdateFailed) + error, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(ManageBuild.this, getString(R.string.buildUpdated), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    public void setError(){
        error += 1;
    }

    public String getTDP(){
        Random rand = new Random();
        return Integer.toString(rand.nextInt(600 - 400 + 1) + 400);
    }
}
