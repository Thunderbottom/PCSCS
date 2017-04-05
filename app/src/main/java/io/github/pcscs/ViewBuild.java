package io.github.pcscs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ViewBuild extends AppCompatActivity {

    String buildName, buildNumber;
    TextView buildNum, buildNm;
    ListView mListView;
    DatabaseReference databaseReference;

    private ArrayList<String> mBuildList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_build);
        setTitle("View Build");
        Bundle bundle = getIntent().getExtras();
        buildName = bundle.getString("buildName");
        buildNumber = bundle.getString("buildNo");

        buildNum = (TextView) findViewById(R.id.buildNumber);
        buildNum.append(buildNumber);

        buildNm = (TextView) findViewById(R.id.buildName);
        buildNm.setText(buildName);

        mListView = (ListView)findViewById(R.id.buildList);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mBuildList);
        mListView.setAdapter(arrayAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("buildlist").child(buildNumber);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
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

    }
}
