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
import com.braintreepayments.api.DataCollector;
import com.braintreepayments.api.PayPalAccountNonce;
import com.braintreepayments.api.PayPalCheckoutRequest;
import com.braintreepayments.api.PayPalClient;
import com.braintreepayments.api.PayPalListener;
import com.braintreepayments.api.PayPalNativeCheckoutAccountNonce;
import com.braintreepayments.api.PayPalNativeCheckoutClient;
import com.braintreepayments.api.PayPalNativeCheckoutListener;
import com.braintreepayments.api.PayPalNativeCheckoutPaymentIntent;
import com.braintreepayments.api.PayPalNativeCheckoutRequest;

import java.util.Random;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayPalPaymentActivity extends AppCompatActivity implements PayPalNativeCheckoutListener {

    private ProgressBar progressBar;
    private String payment_submitted;

    private static final String TAG="PayPalPaymentActivity";
    private static final String AMOUNT = "5";
    private static final String CURRENCY = "USD";

    private BraintreeClient braintreeClient;
   

    private PayPalNativeCheckoutClient payPalNativeCheckoutClient;
    private DataCollector dataCollector;

    private boolean isNativeCheckoutInitiated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        progressBar = findViewById(R.id.progressBar);
        handleLoading(false);

        Intent intent = getIntent();
        payment_submitted = intent.getStringExtra("payment_submitted");
        braintreeClient = new BraintreeClient(this, new TokenProvider());
        payPalNativeCheckoutClient = new PayPalNativeCheckoutClient(braintreeClient);
        payPalNativeCheckoutClient.setListener(this);

        dataCollector = new DataCollector(braintreeClient);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(payment_submitted != null && !isNativeCheckoutInitiated) {
            createPayment();
        } else {
            handleLoading(false);
        }
    }

    private void createPayment() {
        PayPalNativeCheckoutRequest request = new PayPalNativeCheckoutRequest(AMOUNT);
        request.setCurrencyCode(CURRENCY);
        request.setReturnUrl("com.example.androiddemo://paypalpay");
        request.setDisplayName("Braintree Native SDK Demo");
        request.setShouldOfferPayLater(true);
        request.setBillingAgreementDescription("Placeholder for client end billing agreement description");
        request.setIntent(PayPalNativeCheckoutPaymentIntent.SALE);
        // mark the native checkout flow has been initiated
        isNativeCheckoutInitiated = true;
        payPalNativeCheckoutClient.launchNativeCheckout(this, request);
    }

    private void handleLoading(boolean isDone) {
        if(isDone) {
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPayPalSuccess(@NonNull PayPalNativeCheckoutAccountNonce payPalAccountNonce) {
        String nonce = payPalAccountNonce.getString();
        Log.d("ONPAYPALSUCCESS", "The nonce is  "+nonce);

        dataCollector.collectDeviceData(this, (deviceData, error) -> {
           if(error != null) {
               Log.e(TAG, error.getMessage());
           } else {
               Random random = new Random();
               int number = random.nextInt();
               String orderId = String.valueOf(Math.abs(number)).substring(0,4);

               deviceData.replace('"','\"');

               RequestBody requestBody = new MultipartBody.Builder()
                       .setType(MultipartBody.FORM)
                       .addFormDataPart("nonce", nonce)
                       .addFormDataPart("deviceData", deviceData)
                       .addFormDataPart("AMOUNT", AMOUNT)
                       .addFormDataPart("orderId", orderId)
                       .build();

 
               Call<PaymentReceipt> call = new HttpRequest().checkout().payment(requestBody);

               call.enqueue(new Callback<PaymentReceipt>() {
                    @Override
                   public void onResponse(Call<PaymentReceipt> call, Response<PaymentReceipt> response) {
                        assert response.body() != null;
                        boolean status = response.body().getStatus();
                        String transactionID = response.body().getTransactionID();

                        if(status) {
                            Toast.makeText(PayPalPaymentActivity.this, "Payment has been successful and the Transaction ID is "+ transactionID, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(PayPalPaymentActivity.this, "Payment Failed", Toast.LENGTH_LONG).show();
                        }
                        handleLoading(true);
                        finish();
                    }

                    @Override
                   public void onFailure(Call<PaymentReceipt> call, Throwable t) {
                        Exception ex = new Exception(t);
                        Toast.makeText(PayPalPaymentActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();

                        finish();
                    }
               });
           }
        });
    }

    @Override
    public void onPayPalFailure(@NonNull Exception error) {
        Toast.makeText(PayPalPaymentActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
        finish();
    }
}
