package com.codepath.gumapathi.nytsearch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.gumapathi.nytsearch.Model.ArticleResponse;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ListActivity extends AppCompatActivity {
    final Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }

    public void onClickSearch(View view) {
        searchArticles();
    }

    private void searchArticles() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=054c6d1972c142b4a8c84bbb300c4d87")
                .build();
        Log.i("SAMY", "Searching articles");
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("SAMY", "Failed Search");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                Log.i("SAMY", "response");
                if (!response.isSuccessful()) {
                    Log.i("SAMY", "unsuccessful");
                    throw new IOException("Unexpected code " + response);
                } else {
                    Log.i("SAMY", "successful");
                    //runOnUiThread(new Runnable() {
                        //@Override
                        //public void run() {
                            try {
                                Log.i("SAMY", "running of UI Thread");
                                ArticleResponse artResponse = gson.fromJson(response.body().charStream(), ArticleResponse.class);
                                //JSONArray movieResults = json.getJSONArray("results");
                                Log.i("SAMY", artResponse.getCopyright());
                            } catch (Exception e) {
                                Log.i("SAMY", e.toString());
                            }
                        //}
                    //});
                }
            }
        });
    }
}
