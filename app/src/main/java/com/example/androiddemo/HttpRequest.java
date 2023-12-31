package com.example.androiddemo;


//import okhttp3.OkHttpClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class HttpRequest {

//    private static final String TOKEN_ENDPOINT = "http://10.0.2.2:8080/rs/";
//    private static final String CHECKOUT_ENDPOINT = "http://10.0.2.2:8080/rs/";
//
//    private static final String CREDITCARD_CHECKOUT_ENDPOINT = "http://10.0.2.2:8080/rs/";
//
//    private static final String VENMO_CHECKOUT_ENDPOINT = "http://10.0.2.2:8080/rs/";

    private static final String TOKEN_ENDPOINT = "https://zcratx-bt-java-7787bd095269.herokuapp.com/rs/";
    private static final String CHECKOUT_ENDPOINT = "https://zcratx-bt-java-7787bd095269.herokuapp.com/rs/";

    private static final String CREDITCARD_CHECKOUT_ENDPOINT = "https://zcratx-bt-java-7787bd095269.herokuapp.com/rs/";

    private static final String VENMO_CHECKOUT_ENDPOINT = "https://zcratx-bt-java-7787bd095269.herokuapp.com/rs/";

    private static final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    //@GET("client_token")
    public Api requestToken() {
        return this.build(TOKEN_ENDPOINT).create(Api.class);
    }

    public Api checkout() {
        return this.build(CHECKOUT_ENDPOINT).create(Api.class);
    }

    public Api creditCardCheckout()  {
        return this.build(CREDITCARD_CHECKOUT_ENDPOINT).create(Api.class);
    }

    public Api venmoCheckout() {
        return this.build(VENMO_CHECKOUT_ENDPOINT).create(Api.class);
    }


    private Retrofit build(String endpoint) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(endpoint)
                //.addConverterFactory(GsonConverterFactory.create(gson));
                .addConverterFactory(GsonConverterFactory.create());

        builder.client(httpClient.build());
        Retrofit retrofit = builder.build();
        return retrofit;

    }
}
