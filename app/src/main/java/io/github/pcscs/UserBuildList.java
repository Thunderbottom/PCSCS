package io.github.pcscs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserBuildList extends AppCompatActivity {

    int key = 0;
    TextView noBuild;
    Button homeButton, createBuild;
    String username;
    DatabaseReference databaseReference;
    ListView mListView;

    private ArrayList<String> mBuildList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_build_list);
        setTitle("Build List");

        mListView = (ListView)findViewById(R.id.buildList);

        noBuild = (TextView) findViewById(R.id.noBuildsText);
        homeButton = (Button) findViewById(R.id.homeButton);
        createBuild = (Button) findViewById(R.id.createBuildButton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserBuildList.this, UserProfileActivity.class);
                startActivity(i);
                UserBuildList.this.finish();
            }
        });

        createBuild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserBuildList.this, SearchActivity.class);
                startActivity(i);
                UserBuildList.this.finish();
            }
        });

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mBuildList);
        mListView.setAdapter(arrayAdapter);

        SharedPreferences preferences = getSharedPreferences("saveUser", MODE_PRIVATE);
        username = preferences.getString("username", "null");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("builds").child(username);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot != null){
                    noBuild.setVisibility(View.GONE);
                    homeButton.setVisibility(View.GONE);
                    createBuild.setVisibility(View.GONE);
                }
                assert dataSnapshot != null;
                String value = dataSnapshot.getValue(String.class);
                mBuildList.add(value);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String value = (String) parent.getItemAtPosition(position);
                databaseReference
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot uniqueData : dataSnapshot.getChildren()){
                                    if (uniqueData.getValue().equals(value)){
                                        Intent i = new Intent(view.getContext(), ViewBuild.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("buildNo", uniqueData.getKey());
                                        bundle.putString("buildName", uniqueData.getValue().toString());
                                        i.putExtras(bundle);
                                        startActivity(i);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(UserBuildList.this, "Error retrieving data for the build!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
