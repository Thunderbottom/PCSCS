package io.github.pcscs.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import io.github.pcscs.AboutActivity;
import io.github.pcscs.R;

public class SettingsFragment extends Fragment {
        final String[] settingList = new String[]{"About"};

        public SettingsFragment() {
            // Required empty public constructor
        }
    
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View settingView = inflater.inflate(R.layout.fragment_settings, container, false);
            ListView list = (ListView) settingView.findViewById(R.id.settingsListView);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, settingList);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                        long arg3) {
                    // TODO Auto-generated method stub
                    Log.v("TAG", "CLICKED row number: " + arg2);
                    //Toast.makeText(getActivity(), settingList[arg2], Toast.LENGTH_SHORT).show();
                    switch(arg2) {
                        case 0:
                            Intent intent = new Intent(getActivity(), AboutActivity.class);
                            startActivity(intent);
                            break;
                    }
                }

            });

            return settingView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getActivity().setTitle("Settings");
        }
}
