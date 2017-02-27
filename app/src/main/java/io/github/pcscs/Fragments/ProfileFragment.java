package io.github.pcscs.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.pcscs.R;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    public String userUID;
    TextView nameTextView;
    TextView mailTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        try{
            assert firebaseUser != null;
            userUID = firebaseUser.getUid();
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Something unexpected occurred", Toast.LENGTH_SHORT).show();
        }
        SharedPreferences savedUser = this.getActivity().getSharedPreferences("saveUser", Context.MODE_PRIVATE);
        String username = savedUser.getString("username","null");
        if(username.equals("null")){
            Toast.makeText(getActivity(), "Something unexpected occured, username null", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Username not defined. Found username: " + username);
        }
        else {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(userUID).child(username)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String showUsername = dataSnapshot.child("name").getValue(String.class);
                            String showEmail = dataSnapshot.child("email").getValue(String.class);
                            setImage(showUsername);
                            setText(showUsername,showEmail);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

        }
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Profile");
    }

    public String trimUppercase(String fullName){
        Pattern p = Pattern.compile("((^| )[A-Za-z])");
        Matcher m = p.matcher(fullName);
        String initials="";
        while(m.find()){
            initials+=m.group().trim();
        }
        return initials.toUpperCase();
    }

    public void setText(String userText, String mailText){
        mailTextView = (TextView) getView().findViewById(R.id.profileEmail);
        mailTextView.setText(mailText);
        nameTextView = (TextView) getView().findViewById(R.id.profileName);
        nameTextView.setText(userText);
    }

    public void setImage(String DrawName){
        TextDrawable profileImage = TextDrawable.builder()
                .buildRound(trimUppercase(DrawName), Color.BLACK);
        ImageView image = (ImageView) getView().findViewById(R.id.image_view);
        image.setImageDrawable(profileImage);
    }
}
