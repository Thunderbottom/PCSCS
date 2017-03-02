package io.github.pcscs.Fragments;


import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.pcscs.R;
import io.github.pcscs.Update.UserUpdateActivity;
import io.github.pcscs.UserCheck;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    TextView nameTextView;
    TextView mailTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout userProfileLayout= (LinearLayout) inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser!=null;

        SharedPreferences savedUser = this.getActivity().getSharedPreferences("saveUser", Context.MODE_PRIVATE);
        String username = savedUser.getString("username","null");
        if(username.equals("null")){
            Toast.makeText(getActivity(), "Something unexpected occurred", Toast.LENGTH_SHORT).show();
            Log.d("User", "Username not defined. Found username: " + username);
        }
        else {
            mailTextView = (TextView) userProfileLayout.findViewById(R.id.profileEmail);
            mailTextView.setText(firebaseUser.getEmail());
            nameTextView = (TextView) userProfileLayout.findViewById(R.id.profileName);
            nameTextView.setText(firebaseUser.getDisplayName());
            TextDrawable profileImage = TextDrawable.builder()
                    .buildRound(trimUppercase(firebaseUser.getDisplayName()), Color.BLACK);
            ImageView image = (ImageView) userProfileLayout.findViewById(R.id.image_view);
            image.setImageDrawable(profileImage);
        }
        return userProfileLayout;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Profile");
        TextView editProfile = (TextView) getView().findViewById(R.id.editProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getActivity(), UserCheck.class);
                startActivityForResult(intent, 111);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 111)
        {
            String dataResult = data.getStringExtra("result");
            int iDataResult = Integer.parseInt(dataResult);
            if (iDataResult == 1)
            {
                Intent intent = new Intent(getActivity(), UserUpdateActivity.class);
                startActivity(intent);
            }
        }
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
}
