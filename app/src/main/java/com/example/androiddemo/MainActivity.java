package com.example.androiddemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText cardNumberEditText;
    private EditText expirationDateEditText;
    private EditText cvvEditText;

    private Button submitButton;
    private Button btnSubmit;

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // this is for Credit Card
        cardNumberEditText = findViewById(R.id.editTextCardNumber);
        expirationDateEditText = findViewById(R.id.editTextExpirationDate);
        cvvEditText = findViewById(R.id.editTextCVV);
        submitButton = findViewById(R.id.submitButton);

        // this is for PayPal
        btnSubmit = findViewById(R.id.button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle submit button click event
                String cardNumber = cardNumberEditText.getText().toString();
                String expirationDate = expirationDateEditText.getText().toString();
                String cvv = cvvEditText.getText().toString();

                // Perform necessary actions with the entered credit card information
                // (e.g., validation, API calls, etc.)
//                Log.d(TAG, "The card number passed is "+cardNumber);
//                Log.d(TAG, "The expiration date is "+expirationDate);
//                Log.d(TAG, "The CVV passed is "+cvv);

                // call payment activity and call the server
                // (e.g., validation, API calls, etc.)
                Intent creditCardPaymentIntent = new Intent(MainActivity.this, CreditCardPaymentActivity.class);
                creditCardPaymentIntent.putExtra("payment_submitted", "payment");
                creditCardPaymentIntent.putExtra("cardNumber", cardNumber);
                creditCardPaymentIntent.putExtra("expirationDate", expirationDate);
                creditCardPaymentIntent.putExtra("cvv", cvv);
                startActivity(creditCardPaymentIntent);
            }
        });
    }

    public void handleSubmit(View view) {
        Intent paymentIntent = new Intent(this, PaymentActivity.class);
        paymentIntent.putExtra("payment_submitted", "payment");
        startActivity(paymentIntent);
    }


}
