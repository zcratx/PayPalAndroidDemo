
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

        import java.util.Random;

        import okhttp3.MultipartBody;
        import okhttp3.RequestBody;
        import retrofit2.Call;
        import retrofit2.Callback;
        import retrofit2.Response;

public class PayPalWebFlowPaymentActivity extends AppCompatActivity implements PayPalListener {

    private ProgressBar progressBar;
    private String payment_submitted;

    private static final String TAG="PayPalWebFlowPaymentActivity";
    private static final String AMOUNT = "5";
    private static final String CURRENCY = "USD";

    private BraintreeClient braintreeClient;
    private PayPalClient payPalClient;
    private DataCollector dataCollector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        progressBar = findViewById(R.id.progressBar);
        handleLoading(false);

        Intent intent = getIntent();
        payment_submitted = intent.getStringExtra("payment_submitted");
        braintreeClient = new BraintreeClient(this, new TokenProvider());
        payPalClient = new PayPalClient(this, braintreeClient);
        payPalClient.setListener(this);

        dataCollector = new DataCollector(braintreeClient);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(payment_submitted != null) {
            createPayment();
        } else {
            handleLoading(false);
        }
    }

    private void createPayment() {
        PayPalCheckoutRequest request = new PayPalCheckoutRequest(AMOUNT);
        request.setCurrencyCode(CURRENCY);
        payPalClient.tokenizePayPalAccount(this, request);
    }

    private void handleLoading(boolean isDone) {
        if(isDone) {
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPayPalSuccess(@NonNull PayPalAccountNonce payPalAccountNonce) {
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

                // send the payment to the server
//               Call<PaymentReceipt> call = new HttpRequest().checkout().payment(
//                       RequestBody.create(MediaType.parse("text/plain"),nonce),
//                       RequestBody.create(MediaType.parse("text/plain"),deviceData),
//                       RequestBody.create(MediaType.parse("text/plain"),AMOUNT),
//                       RequestBody.create(MediaType.parse("text/plain"),orderId)
//
//               );

                Call<PaymentReceipt> call = new HttpRequest().checkout().payment(requestBody);

                call.enqueue(new Callback<PaymentReceipt>() {
                    @Override
                    public void onResponse(Call<PaymentReceipt> call, Response<PaymentReceipt> response) {
                        assert response.body() != null;
                        boolean status = response.body().getStatus();
                        String transactionID = response.body().getTransactionID();

                        if(status) {
                            Toast.makeText(PayPalWebFlowPaymentActivity.this, "Payment has been successful and the Transaction ID is "+ transactionID, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(PayPalWebFlowPaymentActivity.this, "Payment Failed", Toast.LENGTH_LONG).show();
                        }
                        handleLoading(true);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<PaymentReceipt> call, Throwable t) {
                        Exception ex = new Exception(t);
                        Toast.makeText(PayPalWebFlowPaymentActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();

                        finish();
                    }
                });
            }
        });
    }

    @Override
    public void onPayPalFailure(@NonNull Exception error) {
        Toast.makeText(PayPalWebFlowPaymentActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
        finish();
    }
}