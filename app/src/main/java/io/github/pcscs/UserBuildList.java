package io.github.pcscs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserBuildList extends AppCompatActivity {

    String username;
    DatabaseReference databaseReference;
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_build_list);

        mListView = (ListView) findViewById(R.id.buildList);

        TextView noBuild = (TextView) findViewById(R.id.noBuildsText);
        Button homeButton = (Button) findViewById(R.id.homeButton);
        Button createBuild = (Button) findViewById(R.id.createBuildButton);

        SharedPreferences preferences = getSharedPreferences("saveUser", MODE_PRIVATE);
        username = preferences.getString("username", "null");

        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://pcscs-4a56b.firebaseio.com/buildlist/" + username);
        if(databaseReference != null){
            Toast.makeText(this, username, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "https://pcscs-4a56b.firebaseio.com/buildlist/" + username, Toast.LENGTH_SHORT).show();
            noBuild.setVisibility(View.GONE);
            homeButton.setVisibility(View.GONE);
            createBuild.setVisibility(View.GONE);
        }


        FirebaseListAdapter<String> firebaseListAdapter = new FirebaseListAdapter<String>(
                this,
                String.class,
                android.R.layout.simple_list_item_1,
                databaseReference
        ) {

            @Override
            protected void populateView(View v, String model, int position) {
                TextView textView = (TextView) findViewById(android.R.id.text1);
                textView.setText(model);
            }
        };

        mListView.setAdapter(firebaseListAdapter);
    }
}
