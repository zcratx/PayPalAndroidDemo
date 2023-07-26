package com.example.androiddemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.AccessibleSupportedCardTypesView;
import com.braintreepayments.cardform.view.CardForm;
import com.example.androiddemo.util.CreditCardValidator;

public class MainActivity extends AppCompatActivity {


    // this is for PayPal Native Checkout flow
    private Button btnSubmit;

    // this is for PayPal webflow checkout flow
    private Button btnWebCheckoutSubmit;

    private Button btnCardSubmit;

    private static final String TAG = "MainActivity";

//    private static final CardType[] SUPPORTED_CARD_TYPES = { CardType.VISA, CardType.MASTERCARD, CardType.DISCOVER,
//            CardType.AMEX, CardType.DINERS_CLUB, CardType.JCB, CardType.MAESTRO, CardType.UNIONPAY,
//            CardType.HIPER, CardType.HIPERCARD };

    private static final CardType[] SUPPORTED_CARD_TYPES = { CardType.VISA, CardType.MASTERCARD, CardType.DISCOVER,
            CardType.AMEX, CardType.DINERS_CLUB, CardType.JCB, CardType.MAESTRO, CardType.UNIONPAY,
            CardType.HIPER, CardType.HIPERCARD };

    private AccessibleSupportedCardTypesView mSupportedCardTypesView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSupportedCardTypesView = findViewById(R.id.supported_card_types);
        mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);



        CardForm cardForm = (CardForm) findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .maskCardNumber(true)
                .maskCvv(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(true)
                //.mobileNumberRequired(true)
                //.saveCardCheckBoxChecked(true)
                //.saveCardCheckBoxVisible(true)
                .cardholderName(CardForm.FIELD_REQUIRED)
                //.mobileNumberExplanation("Make sure SMS is enabled for this mobile number")
                //.actionLabel(getString(R.string.purchase))
                .setup(this);

        btnCardSubmit = findViewById(R.id.card_button);

        // this is for PayPal native checkout flow
        btnSubmit = findViewById(R.id.button);

        // this is for PayPal web flow checkout flow
        //btnWebCheckoutSubmit = findViewById(R.id.paypal_webflow_button);

        cardForm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                cardForm.validate();
                return;
            }
        });

        btnCardSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle submit button click event
                if (cardForm.isValid()) {
                    String cardNumber = cardForm.getCardNumber();
                    String expirationMonth = cardForm.getExpirationMonth();
                    String expirationYear = cardForm.getExpirationYear();
                    String cvv = cardForm.getCvv();
                    String cardHolderName = cardForm.getCardholderName();
                    String zipcode  = cardForm.getPostalCode();
                    String countryCode = cardForm.getCountryCode();
                    //String customerMobileNumber = cardForm.getMobileNumber();

                    String expirationDate = expirationMonth+"/"+expirationYear;

                    // call payment activity and call the server
                    // (e.g., validation, API calls, etc.)
                    Intent creditCardPaymentIntent = new Intent(MainActivity.this, CreditCardPaymentActivity.class);
                    creditCardPaymentIntent.putExtra("payment_submitted", "payment");
                    creditCardPaymentIntent.putExtra("cardNumber", cardNumber);
                    creditCardPaymentIntent.putExtra("expirationDate", expirationDate);
                    creditCardPaymentIntent.putExtra("cvv", cvv);
                    creditCardPaymentIntent.putExtra("zipcode", zipcode);
                    startActivity(creditCardPaymentIntent);

                } else {
                    cardForm.validate();
                    return;
                }


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

//    public void handlePayPalWebFlowSubmit(View view) {
//        Intent paymentIntent = new Intent(this, PayPalWebFlowPaymentActivity.class);
//        paymentIntent.putExtra("payment_submitted", "payment");
//        startActivity(paymentIntent);
//    }

}
