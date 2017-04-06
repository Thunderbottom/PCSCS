package io.github.pcscs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewBuild extends AppCompatActivity {

    String buildName, buildNumber;

    // For the parts list
    String CPU, MOBO, GPU, PSU, MON, CAB;
    TextView buildNum, buildNm, TDPVal;
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

        databaseReference = FirebaseDatabase.getInstance().getReference().child("TDP").child(buildNumber);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    setTDP(dataSnapshot.getValue().toString());
                }
                else
                    setTDP("500W");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

        Button manageBuild = (Button) findViewById(R.id.manageButton);
        manageBuild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CPU = mListView.getAdapter().getItem(0).toString();
                CAB = mListView.getAdapter().getItem(1).toString();
                GPU = mListView.getAdapter().getItem(2).toString();
                MON = mListView.getAdapter().getItem(3).toString();
                MOBO = mListView.getAdapter().getItem(4).toString();
                PSU = mListView.getAdapter().getItem(5).toString();
                Intent i = new Intent(v.getContext(), ManageBuild.class);
                Bundle bundle = new Bundle();
                bundle.putString("CPU", CPU);
                bundle.putString("CAB", CAB);
                bundle.putString("GPU", GPU);
                bundle.putString("MON", MON);
                bundle.putString("MOBO", MOBO);
                bundle.putString("PSU", PSU);
                bundle.putString("buildName", buildName);
                bundle.putString("buildNumber", buildNumber);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }

    public void setTDP(String TDP){
        TextView TDPVal = (TextView) findViewById(R.id.tdpVal);
        TDPVal.append(TDP);

    }
}
