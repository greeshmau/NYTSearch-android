package com.codepath.gumapathi.nytsearch;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

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

//import android.support.v7.widget.


public class ListActivity extends AppCompatActivity {
    final Gson gson = new Gson();
    ArticlesAdapter articlesAdapter;
    ArrayList<Doc> allArticles;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        /*
         * Toolbar related stuff
         */
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        // Find the toolbar view and set as ActionBar
        // ...
        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        /*
         * Recycleview related stuff
         */

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
                //searchArticles();
            }
        };
        // Adds the scroll listener to RecyclerView
        rvArticles.addOnScrollListener(scrollListener);

    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //Log ("SAMY", "Searching articles");
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                searchArticles(query);
                Toast.makeText(ListActivity.this, "Searching! " + query, Toast.LENGTH_SHORT).show();
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_filter) {
            Toast.makeText(this, "Filtering clicked", Toast.LENGTH_LONG).show();
        }

        if (id == R.id.action_search) {
            Toast.makeText(this, "Search clicked", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                    Log.i("SAMY", "unsuccessful" + response.toString());
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

    private void searchArticles(String query) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=054c6d1972c142b4a8c84bbb300c4d87" + "&q=" + query)
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
