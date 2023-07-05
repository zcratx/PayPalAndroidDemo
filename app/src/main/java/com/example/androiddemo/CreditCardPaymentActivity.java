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

    private static final String AMOUNT = "5";
    private static final String CURRENCY = "USD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credit_card_activity_payment);

        progressBar = findViewById(R.id.progressBar);
        handleLoading(true);

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
            if(error != null) {
                Log.d(TAG, "The ERROR at CreditCardPaymentActivity is "+error.getMessage());

                Toast.makeText(CreditCardPaymentActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                //IMPORTANT: The activity finised here as we cannot tokenize the card
                finish();
            }

            Log.d(TAG, "The nonce created at CreditCardPaymentActivity is "+cardNonce.getString());

            // submitting the nonce to the server
            dataCollector.collectDeviceData(this, (deviceData, deviceDataerror) -> {
                if(deviceDataerror != null) {
                    Log.e(TAG, deviceDataerror.getMessage());
                } else {
                    Random random = new Random();
                    int number = random.nextInt();
                    String orderId = String.valueOf(Math.abs(number)).substring(0,4);

                    deviceData.replace('"','\"');

                    assert cardNonce != null;
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("nonce", cardNonce.getString())
                            .addFormDataPart("deviceData", deviceData)
                            .addFormDataPart("AMOUNT", AMOUNT)
                            .addFormDataPart("orderId", orderId)
                            .build();

                    Call<PaymentReceipt> call = new HttpRequest().creditCardCheckout().creditCardPayment(requestBody);

                    call.enqueue(new Callback<PaymentReceipt>() {
                        @Override
                        public void onResponse(Call<PaymentReceipt> call, Response<PaymentReceipt> response) {
                            assert response.body() != null;
                            boolean status = response.body().getStatus();
                            String transactionID = response.body().getTransactionID();

                            if(status) {
                                Toast.makeText(CreditCardPaymentActivity.this, "Payment has been successful and the Transaction ID is "+ transactionID, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(CreditCardPaymentActivity.this, "Payment Failed", Toast.LENGTH_LONG).show();
                            }
                            handleLoading(true);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<PaymentReceipt> call, Throwable t) {
                            Exception ex = new Exception(t);
                            Toast.makeText(CreditCardPaymentActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();

                            finish();
                        }
                    });
                }
            }); // this is where data collector will end


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
