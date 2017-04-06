package io.github.pcscs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String getFB, spinnerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setTitle("Feedback");

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<>();
        categories.add("Request");
        categories.add("Suggestion");
        categories.add("Report a Problem");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        final EditText feedback = (EditText) findViewById(R.id.feedbackField);

        final TextInputLayout feedbackText = (TextInputLayout) findViewById(R.id.feedbackText);
        feedbackText.setError(null);

        Button submitButton = (Button) findViewById(R.id.submitFeedback);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFB = feedback.getText().toString();
                if (TextUtils.isEmpty(getFB)){
                    feedbackText.setError(getString(R.string.errorField));
                    feedback.requestFocus();
                }
                else {
                    spinnerText = spinner.getSelectedItem().toString();
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"huzz.work@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "PCSCS: " + spinnerText);
                    i.putExtra(Intent.EXTRA_TEXT   , getFB);
                    try {
                        startActivity(Intent.createChooser(i, "Select an email client"));
                        feedback.setText("");
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(FeedbackActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
