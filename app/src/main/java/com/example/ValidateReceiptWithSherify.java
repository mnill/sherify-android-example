package com.example;

import android.os.AsyncTask;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ValidateReceiptWithSherify extends AsyncTask<ValidationData, Void, String> {

    public interface AsyncResponse {
        void processFinish(Boolean error, String status, String transactionID);
    }

    public AsyncResponse delegate = null;
    private Exception exception;
    private String status;

    public ValidateReceiptWithSherify(AsyncResponse asyncResponse) {
        delegate = asyncResponse;
    }

    @Override
    protected String doInBackground(ValidationData... data) {
        try {
            ValidationData toValidate = data[0];

            OkHttpClient client = new OkHttpClient.Builder()
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .retryOnConnectionFailure(true)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .cache(null)
                    .build();;

            RequestBody formBody = new FormBody.Builder()
                    .add("receipt", toValidate.receipt)
                    .add("signature", toValidate.signature)
                    .add("user", toValidate.userid)
                    .build();

            Request request = new Request.Builder()
                    .url(toValidate.url)
                    .post(formBody)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
                throw new Exception("Wrong answer");

            String responseBody = response.body().string();
            JSONParser parser = new JSONParser();
            Object resultObject = parser.parse(responseBody);
            JSONObject obj = (JSONObject) resultObject;

            if (obj.containsKey("status") && obj.containsKey("transaction")) {
                String result = (String) obj.get("status");
                String transaction = (String) obj.get("transaction");
                this.status = result;
                return transaction;
            } else {
                throw new Exception("Bad receipt");
            }
        } catch (Exception e) {
            this.exception = e;
            return null;
        }
    }

    protected void onPostExecute(String transactionID) {
        delegate.processFinish(this.exception != null, this.status, transactionID);
    }
}