package com.example.androiddemo;

        import android.content.Intent;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.ProgressBar;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;

        import com.braintreepayments.api.BraintreeClient;
        import com.braintreepayments.api.Card;
        import com.braintreepayments.api.CardClient;
        import com.braintreepayments.api.DataCollector;
        import com.braintreepayments.api.PayPalAccountNonce;
        import com.braintreepayments.api.PayPalCheckoutRequest;
        import com.braintreepayments.api.PayPalClient;
        import com.braintreepayments.api.PayPalListener;
        import com.google.gson.Gson;
        import com.google.gson.JsonParser;

        import org.json.JSONObject;

        import java.io.IOException;
        import java.util.Random;

        import okhttp3.MediaType;
        import okhttp3.MultipartBody;
        import okhttp3.RequestBody;
        import okio.BufferedSink;
        import retrofit2.Call;
        import retrofit2.Callback;
        import retrofit2.Response;

public class CreditCardPaymentActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private String payment_submitted;

    private String cardNumber;

    private String expirationDate;

    private String cvv;

    private static final String TAG="CreditCardPaymentActivity";

    private BraintreeClient braintreeClient;

    private CardClient cardClient;
    private DataCollector dataCollector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credit_card_activity_payment);

        progressBar = findViewById(R.id.progressBar);
        handleLoading(false);

        // get all the information from the invoking activity which is MainActivity.java
        Intent intent = getIntent();
        payment_submitted = intent.getStringExtra("payment_submitted");
        cardNumber = intent.getStringExtra("cardNumber");
        expirationDate = intent.getStringExtra("expirationDate");
        cvv = intent.getStringExtra("cvv");

        Log.d(TAG, "The credit card number at CreditCardPaymentActivity is "+cardNumber);

        Card card = new Card();
        //TODO: Put the information below for this to work
        card.setNumber(cardNumber);
        card.setCvv(expirationDate);
        card.setExpirationDate(cvv);

        braintreeClient = new BraintreeClient(this, new TokenProvider());
        dataCollector = new DataCollector(braintreeClient);
        cardClient = new CardClient(braintreeClient);
        cardClient.tokenize(card,(cardNonce,error)-> {
            //TODO: Print the nonce that comes from the server
            Log.d(TAG, "The nonce created at CreditCardPaymentActivity is "+cardNonce);

            //TODO: Submit the nonce to the server for processing
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        if(payment_submitted != null) {
            handleLoading(true);
        } else {
            handleLoading(false);
        }
    }


    private void handleLoading(boolean isDone) {
        if(isDone) {
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
    }


}
