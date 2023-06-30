package com.example.androiddemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btnSubmit = findViewById(R.id.button);
    }

    public void handleSubmit(View view) {
        Intent paymentIntent = new Intent(this, PaymentActivity.class);
        paymentIntent.putExtra("payment_submitted", "payment");
        startActivity(paymentIntent);
    }
}
