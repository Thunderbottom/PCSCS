package io.github.pcscs;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class SearchActivity extends AppCompatActivity {

    int error = 0;
    public long dataLength;
    String username;
    String [] cpulist, cablist, gpulist, mobolist, monlist, psulist;
    String getCPU, getCAB, getGPU, getMOBO, getMON, getPSU, getBuild;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("buildlist");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setLength(dataSnapshot.getChildrenCount());
            }

            @Override public void onCancelled(DatabaseError databaseError) {

            }
        });

        cpulist = getResources().getStringArray(R.array.cpulist);
        ArrayAdapter<String> adapterCPU = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item,cpulist);
        final AutoCompleteTextView cpuSearch= (AutoCompleteTextView)findViewById(R.id.CPUSearchList);
        cpuSearch.setThreshold(1);
        cpuSearch.setAdapter(adapterCPU);

        cablist = getResources().getStringArray(R.array.cablist);
        ArrayAdapter<String> adapterCAB = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item,cablist);
        final AutoCompleteTextView cabSearch= (AutoCompleteTextView)findViewById(R.id.CABSearchList);
        cabSearch.setThreshold(1);
        cabSearch.setAdapter(adapterCAB);

        gpulist = getResources().getStringArray(R.array.gpulist);
        ArrayAdapter<String> adapterGPU = new ArrayAdapter<>
                (this,android.R.layout.select_dialog_item,gpulist);
        final AutoCompleteTextView gpuSearch= (AutoCompleteTextView)findViewById(R.id.GPUSearchList);
        gpuSearch.setThreshold(1);
        gpuSearch.setAdapter(adapterGPU);

        mobolist = getResources().getStringArray(R.array.mobolist);
        ArrayAdapter<String> adapterMOBO = new ArrayAdapter<>
                (this,android.R.layout.select_dialog_item,mobolist);
        final AutoCompleteTextView moboSearch= (AutoCompleteTextView)findViewById(R.id.MOBOSearchList);
        moboSearch.setThreshold(1);
        moboSearch.setAdapter(adapterMOBO);

        monlist = getResources().getStringArray(R.array.monlist);
        ArrayAdapter<String> adapterMON = new ArrayAdapter<>
                (this,android.R.layout.select_dialog_item,monlist);
        final AutoCompleteTextView monSearch= (AutoCompleteTextView)findViewById(R.id.monitorSearchList);
        monSearch.setThreshold(1);
        monSearch.setAdapter(adapterMON);

        psulist = getResources().getStringArray(R.array.psulist);
        ArrayAdapter<String> adapterPSU = new ArrayAdapter<>
                (this,android.R.layout.select_dialog_item,psulist);
        final AutoCompleteTextView psuSearch= (AutoCompleteTextView)findViewById(R.id.PSUSearchList);
        psuSearch.setThreshold(1);
        psuSearch.setAdapter(adapterPSU);

        final EditText buildName = (EditText) findViewById(R.id.buildName);

        Button createButton = (Button) findViewById(R.id.createBuildButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCPU = cpuSearch.getText().toString();
                getCAB = cabSearch.getText().toString();
                getGPU = gpuSearch.getText().toString();
                getMOBO = moboSearch.getText().toString();
                getMON = monSearch.getText().toString();
                getPSU = psuSearch.getText().toString();
                getBuild = buildName.getText().toString();

                if (TextUtils.isEmpty(getCPU) || TextUtils.isEmpty(getCAB) || TextUtils.isEmpty(getGPU)
                        || TextUtils.isEmpty(getMOBO) || TextUtils.isEmpty(getMON) || TextUtils.isEmpty(getPSU)){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(v.getContext());
                    builder1.setMessage(getString(R.string.emptyFields));
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
                else {
                    // DATABASE
                    SharedPreferences preferences = getSharedPreferences("saveUser", MODE_PRIVATE);
                    username = preferences.getString("username", "null");
                    databaseReference = FirebaseDatabase.getInstance()
                            .getReference()
                            .child("buildlist");
                    databaseReference.child(getLength())
                            .child("CPU")
                            .setValue(getCPU)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                   if(!task.isSuccessful()){
                                       setError();
                                   }
                                }
                            });
                    databaseReference.child(getLength())
                            .child("Cabinet")
                            .setValue(getCAB)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful()){
                                        setError();
                                    }
                                }
                            });
                    databaseReference.child(getLength())
                            .child("GPU")
                            .setValue(getGPU)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful()){
                                        setError();
                                    }
                                }
                            });
                    databaseReference.child(getLength())
                            .child("Monitor")
                            .setValue(getMON)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful()){
                                        setError();
                                    }
                                }
                            });
                    databaseReference.child(getLength())
                            .child("Motherboard")
                            .setValue(getMOBO)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful()){
                                        setError();
                                    }
                                }
                            });
                    databaseReference.child(getLength())
                            .child("PSU")
                            .setValue(getPSU)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful()){
                                        setError();
                                    }
                                }
                            });
                    databaseReference = FirebaseDatabase.getInstance().getReference()
                            .child("TDP");
                    databaseReference.child(getLength())
                            .setValue(getTDP() + "W")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful()){
                                        setError();
                                    }
                                }
                            });
                    databaseReference = FirebaseDatabase.getInstance().getReference()
                            .child("builds")
                            .child(username);
                    Toast.makeText(SearchActivity.this, "Build ID: " + getLength(), Toast.LENGTH_SHORT).show();
                    databaseReference.child(getLength()).setValue(getBuild);

                    if (error >= 1){
                        Toast.makeText(SearchActivity.this, getString(R.string.buildUpdateFailed + error), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(SearchActivity.this, getString(R.string.buildUpdated), Toast.LENGTH_SHORT).show();
                        Intent i = new Intent (v.getContext(), UserProfileActivity.class);
                        startActivity(i);
                        SearchActivity.this.finish();
                    }
                }


            }
        });
    }

    public void setError(){
        error += 1;
    }
    public void setLength(long length){
        dataLength = length;
    }

    public String getLength(){
        return Long.toString(dataLength + 1);
    }

    public String getTDP(){
        Random rand = new Random();
        return Integer.toString(rand.nextInt(600 - 400 + 1) + 400);
    }
}
