package com.example.androiddemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class VenmoLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Invoke the URL and handle the completion
        invokeURL();

        // Set the result and finish the activity
        setResult(RESULT_OK);
        finish();
    }

    private void invokeURL() {
        Intent tempIntent = new Intent(Intent.ACTION_VIEW);
        tempIntent.setData(Uri.parse(
                "https://venmo.com/go/checkout"));
        //startActivity(tempIntent);

    }


}
