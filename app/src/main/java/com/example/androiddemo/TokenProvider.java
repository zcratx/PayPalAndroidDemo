package com.example.androiddemo;

import android.util.Log;

import androidx.annotation.NonNull;

import com.braintreepayments.api.ClientTokenCallback;
import com.braintreepayments.api.ClientTokenProvider;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TokenProvider implements ClientTokenProvider {

    public static final String TAG = "TokenProvider";

    @Override
    //@GET("client_token")
    public void getClientToken(@NonNull ClientTokenCallback callback) {

        Call<DerivedClientToken> call = new HttpRequest().requestToken().getClientToken();

        //Async
        call.enqueue(new Callback<DerivedClientToken>() {

            @Override
            public void onResponse(Call<DerivedClientToken> call, Response<DerivedClientToken> response) {
                Log.d(TAG, "Client Token is "+response.body().getValue());
                //System.out.println(" Client Token is "+ response.body().getValue());
                //TODO: Check this method as the original method was getValue
                callback.onSuccess(response.body().getValue());
            }

            @Override
            public void onFailure(Call<DerivedClientToken> call, Throwable t) {
                Log.d(TAG, "Client Token Exception "+t);
                System.out.println(" Client Token Exception "+ t.getMessage());
                callback.onFailure(new Exception(t));
            }

        });
    }

}
