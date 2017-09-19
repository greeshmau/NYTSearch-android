package com.codepath.gumapathi.nytsearch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.codepath.gumapathi.nytsearch.Adapter.ArticlesAdapter;
import com.codepath.gumapathi.nytsearch.Helpers.EndlessRecyclerViewScrollListener;
import com.codepath.gumapathi.nytsearch.Model.ArticleResponse;
import com.codepath.gumapathi.nytsearch.Model.Doc;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ListActivity extends AppCompatActivity {
    final Gson gson = new Gson();
    ArticlesAdapter articlesAdapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    ArrayList<Doc> allArticles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        RecyclerView rvArticles = (RecyclerView) findViewById(R.id.rvArticles);
        allArticles = new ArrayList<>();
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        // Attach the layout manager to the recycler view
        rvArticles.setLayoutManager(staggeredGridLayoutManager);
        articlesAdapter = new ArticlesAdapter(allArticles);
        rvArticles.setAdapter(articlesAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextSetOfArticles(page);
                searchArticles();
            }
        };
        // Adds the scroll listener to RecyclerView
        rvArticles.addOnScrollListener(scrollListener);

    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void loadNextSetOfArticles(int pageNum) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=054c6d1972c142b4a8c84bbb300c4d87" + "&page=" + pageNum;
        Log.i("SAMY-againsearch", url);
        Request request = new Request.Builder()
                .url(url)
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
                    Log.i("SAMY", "unsuccessful"+response.toString());
                    throw new IOException("Unexpected code " + response);
                } else {
                    Log.i("SAMY", "successful");
                    Log.i("SAMY", "running on UI Thread");
                    final ArticleResponse artResponse = gson.fromJson(response.body().charStream(), ArticleResponse.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                //JSONArray movieResults = json.getJSONArray("results");
                                allArticles.addAll(artResponse.getResponse().getDocs());
                                articlesAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                Log.i("SAMY-error", e.toString());
                                //e.printStackTrace();
                            }
                        }//end run()
                    });//end runOnUiThread
                }
            }
        });
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
                    Log.i("SAMY", "running on UI Thread");
                    String responseData = response.body().string();
                    Log.i("SAMY-response", responseData);
                    //JSONObject json = new JSONObject(responseData);
                    final ArticleResponse artResponse = gson.fromJson(responseData, ArticleResponse.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                allArticles.addAll(artResponse.getResponse().getDocs());
                                articlesAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                Log.i("SAMY-error", e.toString());
                                e.printStackTrace();
                                //e.printStackTrace();
                            }
                        }//end run()
                    });//end runOnUiThread
                }
            }
        });
    }
}
