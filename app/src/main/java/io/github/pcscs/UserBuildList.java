package io.github.pcscs;

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

import java.util.ArrayList;

public class UserBuildList extends AppCompatActivity {

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

        mListView = (ListView)findViewById(R.id.buildList);

        noBuild = (TextView) findViewById(R.id.noBuildsText);
        homeButton = (Button) findViewById(R.id.homeButton);
        createBuild = (Button) findViewById(R.id.createBuildButton);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mBuildList);
        mListView.setAdapter(arrayAdapter);

        SharedPreferences preferences = getSharedPreferences("saveUser", MODE_PRIVATE);
        username = preferences.getString("username", "null");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("builds");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot != null){
                    noBuild.setVisibility(View.GONE);
                    homeButton.setVisibility(View.GONE);
                    createBuild.setVisibility(View.GONE);
                }
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = (String) parent.getItemAtPosition(position);
                Toast.makeText(UserBuildList.this, value, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
