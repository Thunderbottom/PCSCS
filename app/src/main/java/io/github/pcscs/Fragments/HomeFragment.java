package io.github.pcscs.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.github.pcscs.R;
import io.github.pcscs.SearchActivity;
import io.github.pcscs.UserBuildList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    String username;
    DatabaseReference databaseReference;
    RelativeLayout homeLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_home, container, false);
        return homeLayout;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Home");

        final TextView buildCount = (TextView) homeLayout.findViewById(R.id.textView);

        final Button viewBuilds = (Button) homeLayout.findViewById(R.id.viewBuilds);
        viewBuilds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (getActivity(), UserBuildList.class);
                startActivity(i);
            }
        });

        final Button createBuilds = (Button) homeLayout.findViewById(R.id.createBuildButton);
        createBuilds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (getActivity(), SearchActivity.class);
                startActivity(i);
            }
        });

        SharedPreferences savedUser = this.getActivity().getSharedPreferences("saveUser", Context.MODE_PRIVATE);
        username = savedUser.getString("username","null");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("builds").child(username);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                buildCount.append(" " + Long.toString(dataSnapshot.getChildrenCount()) + " builds");
                if (dataSnapshot.getChildrenCount() == 0) {
                    viewBuilds.setVisibility(View.GONE);
                    createBuilds.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        FloatingActionButton floatingActionButton = (FloatingActionButton) homeLayout.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SearchActivity.class);
                startActivity(i);
            }
        });
    }

}
