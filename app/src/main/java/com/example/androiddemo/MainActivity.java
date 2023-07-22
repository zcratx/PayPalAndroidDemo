package com.example.androiddemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androiddemo.util.CreditCardValidator;

public class MainActivity extends AppCompatActivity {

    private EditText cardNumberEditText;
    private EditText expirationDateEditText;
    private EditText cvvEditText;
    private EditText zipcodeEditText;

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
        zipcodeEditText = findViewById(R.id.editTextZip);
        submitButton = findViewById(R.id.submitButton);

        // this is for PayPal
        btnSubmit = findViewById(R.id.button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle submit button click event
                String cardNumber = cardNumberEditText.getText().toString();

                //validate the cardnumber and make sure it is correct, else throw an error
                if(!CreditCardValidator.isValidCardNumber(cardNumber)) {
                    Toast.makeText(MainActivity.this,"Please input a valid credit card number", Toast.LENGTH_LONG).show();

                    return;
                }

                String expirationDate = expirationDateEditText.getText().toString();

                if(!expirationDate.contains("/")) {
                    Toast.makeText(MainActivity.this,"Date has to be in mm/dd format", Toast.LENGTH_LONG).show();

                    return;
                }

                String expirationMonth = expirationDate.substring(0,expirationDate.lastIndexOf("/"));
                String expirationYear = expirationDate.substring(expirationDate.lastIndexOf("/")+1);

                try {
                    int expirationMonthInt = Integer.parseInt(expirationMonth);
                    int expirationYearInt = Integer.parseInt(expirationYear);

                    if (!CreditCardValidator.checkExpirationDate(expirationMonthInt, expirationYearInt)) {

                        Toast.makeText(MainActivity.this,"Date has to be a valid date", Toast.LENGTH_LONG).show();

                        return;
                    }
                } catch(Exception e ) {
                    Toast.makeText(MainActivity.this,"Date has to be in mm/yy format", Toast.LENGTH_LONG).show();

                    return;
                }


                String cvv = cvvEditText.getText().toString();

                if(!CreditCardValidator.checkCVV(cvv)) {
                    Toast.makeText(MainActivity.this,"CVV has to be valid", Toast.LENGTH_LONG).show();

                    return;
                }

                String zipcode = zipcodeEditText.getText().toString();

                if(zipcode.length() != 5) {
                    Toast.makeText(MainActivity.this,"Input a valid zipcode", Toast.LENGTH_LONG).show();

                    return;
                }

                // call payment activity and call the server
                // (e.g., validation, API calls, etc.)
                Intent creditCardPaymentIntent = new Intent(MainActivity.this, CreditCardPaymentActivity.class);
                creditCardPaymentIntent.putExtra("payment_submitted", "payment");
                creditCardPaymentIntent.putExtra("cardNumber", cardNumber);
                creditCardPaymentIntent.putExtra("expirationDate", expirationDate);
                creditCardPaymentIntent.putExtra("cvv", cvv);
                creditCardPaymentIntent.putExtra("zipcode", zipcode);
                startActivity(creditCardPaymentIntent);
            }
        });
    }

    // this is to handle paypal submit
    public void handlePayPalSubmit(View view) {
        Intent paymentIntent = new Intent(this, PayPalPaymentActivity.class);
        paymentIntent.putExtra("payment_submitted", "payment");
        startActivity(paymentIntent);
    }

    // this is to handle VENMO submit

    public void handleVenmoSubmit(View view) {
        Intent venmoPaymentIntent = new Intent(this, VenmoPaymentActivity.class);
        venmoPaymentIntent.putExtra("payment_submitted", "payment");
        startActivity(venmoPaymentIntent);
    }

}
