package com.example.androiddemo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.braintreepayments.api.BraintreeClient;
import com.braintreepayments.api.DataCollector;
import com.braintreepayments.api.PayPalClient;
import com.braintreepayments.api.UserCanceledException;
import com.braintreepayments.api.VenmoAccountNonce;
import com.braintreepayments.api.VenmoClient;
import com.braintreepayments.api.VenmoIsReadyToPayCallback;
import com.braintreepayments.api.VenmoListener;
import com.braintreepayments.api.VenmoPaymentMethodUsage;
import com.braintreepayments.api.VenmoRequest;

import java.math.BigDecimal;
import java.util.Random;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VenmoPaymentActivity extends AppCompatActivity implements VenmoListener {

    private BraintreeClient braintreeClient;
    private VenmoClient venmoClient;
    private DataCollector dataCollector;

    private static final String TAG = "VenmoPaymentActivity";

    private static final String AMOUNT="5";

    private ProgressBar progressBar;
    private String payment_submitted;

    private boolean tokenizationDone = false;

    private static boolean userLoginThroughWeb = false;

    private static final int VENMO_URL_INVOKING_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        progressBar = findViewById(R.id.progressBar);
        handleLoading(false);

        Intent intent = getIntent();
        payment_submitted = intent.getStringExtra("payment_submitted");

        braintreeClient = new BraintreeClient(this, new TokenProvider());
        venmoClient = new VenmoClient(this, braintreeClient);

        // first check if Venmo is on this device or can be installed
        //venmoClient.isReadyToPay(this, null);

        dataCollector = new DataCollector(braintreeClient);
        venmoClient.setListener(this);


        if(!userLoginThroughWeb && !venmoClient.isVenmoAppSwitchAvailable(this)) {
            Log.e(TAG, "Venmo Application is not installed and the User has not logged in through WEB!");



            // This for now is commented as I do not have a compatible phone to install Venmo
            venmoClient.showVenmoInGooglePlayStore(this);
        }
    }



    private void tokenizeVenmoAccount() {
        VenmoRequest request = new VenmoRequest(VenmoPaymentMethodUsage.MULTI_USE);
        //request.setProfileId("1953896702662410263");
        request.setProfileId("1953896702662410263");
        request.setShouldVault(false);
        request.setTotalAmount("5");
        request.setDisplayName("Zcratx Store");
        request.setCollectCustomerBillingAddress(false);
        request.setCollectCustomerShippingAddress(false);

        venmoClient.tokenizeVenmoAccount(this, request);
        tokenizationDone = true;
    }

    @Override
    public void onVenmoSuccess(@NonNull VenmoAccountNonce venmoAccountNonce) {
        dataCollector.collectDeviceData(this, (deviceData, dataCollectorError) -> {
            // send venmoAccountNonce.getString() and deviceData to server

            if(dataCollectorError != null) {
                Log.e(TAG, dataCollectorError.getMessage());
            } else {
                Random random = new Random();
                int number = random.nextInt();
                String orderId = String.valueOf(Math.abs(number)).substring(0,4);

                deviceData.replace('"','\"');

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("nonce", venmoAccountNonce.getString())
                        .addFormDataPart("deviceData", deviceData)
                        .addFormDataPart("AMOUNT", AMOUNT)
                        .addFormDataPart("orderId", orderId)
                        .build();

                Call<PaymentReceipt> call = new HttpRequest().venmoCheckout().payment(requestBody);

                call.enqueue(new Callback<PaymentReceipt>() {
                    @Override
                    public void onResponse(Call<PaymentReceipt> call, Response<PaymentReceipt> response) {
                        assert response.body() != null;
                        boolean status = response.body().getStatus();
                        String transactionID = response.body().getTransactionID();

                        if(status) {
                            Toast.makeText(VenmoPaymentActivity.this, "Payment has been successful and the Transaction ID is "+ transactionID, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(VenmoPaymentActivity.this, "Payment Failed", Toast.LENGTH_LONG).show();
                        }
                        handleLoading(true);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<PaymentReceipt> call, Throwable t) {
                        Exception ex = new Exception(t);
                        Toast.makeText(VenmoPaymentActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();

                        finish();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(payment_submitted != null && !tokenizationDone) {
            tokenizeVenmoAccount();
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

    @Override
    public void onVenmoFailure(@NonNull Exception error) {
        if (error instanceof UserCanceledException) {
            Toast.makeText(VenmoPaymentActivity.this, "User cancelled the payment", Toast.LENGTH_LONG).show();
            finish();
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(VenmoPaymentActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
