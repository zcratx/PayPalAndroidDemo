package com.example.androiddemo;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    @POST("checkout_paypal")
    //@FormUrlEncoded
    //@Multipart
    Call<PaymentReceipt> payment(@Body RequestBody body);
    //Call<PaymentReceipt> payment(@Part("nonce") RequestBody nonce, @Part("deviceData") RequestBody deviceData, @Part("amount") RequestBody amount, @Part("orderId") RequestBody orderId );
    //Call<PaymentReceipt> payment(@Field("nonce") String nonce,  @Field("amount") String amount, @Field("orderId") String orderId );

    @GET("client_token")
    Call<DerivedClientToken> getClientToken();

    @POST("checkout_creditcard")
    Call<PaymentReceipt> creditCardPayment(@Body RequestBody body);

    @POST("checkout_venmo")
    Call<PaymentReceipt> venmoPayment(@Body RequestBody body);


}
